package com.trackwell.app.domain.fhir

import com.google.gson.GsonBuilder
import com.trackwell.app.data.local.dao.DailyLogDao
import com.trackwell.app.data.local.dao.FluidEntryDao
import com.trackwell.app.data.local.dao.MedicationDao
import com.trackwell.app.data.local.dao.SugarEntryDao
import com.trackwell.app.domain.util.DateTimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// FHIR R4 Data Structures for JSON Serialization
data class FhirCoding(
    val system: String,
    val code: String,
    val display: String
)

data class FhirCodeableConcept(
    val coding: List<FhirCoding>,
    val text: String
)

data class FhirQuantity(
    val value: Double,
    val unit: String,
    val system: String = "http://unitsofmeasure.org",
    val code: String
)

data class FhirResource(
    val resourceType: String,
    val id: String = UUID.randomUUID().toString(),
    val status: String? = null,
    val subject: Map<String, String>? = mapOf("reference" to "Patient/trackwell-patient"),
    val code: FhirCodeableConcept? = null,
    val valueQuantity: FhirQuantity? = null,
    val effectiveDateTime: String? = null,
    val medicationCodeableConcept: FhirCodeableConcept? = null,
    val dosage: List<Map<String, String>>? = null
)

data class FhirBundleEntry(
    val resource: FhirResource
)

data class FhirBundle(
    val resourceType: String = "Bundle",
    val id: String = UUID.randomUUID().toString(),
    val type: String = "collection",
    val timestamp: String = java.time.Instant.now().toString(),
    val entry: List<FhirBundleEntry>
)

@Singleton
class FhirR4Exporter @Inject constructor(
    private val dailyLogDao: DailyLogDao,
    private val fluidEntryDao: FluidEntryDao,
    private val sugarEntryDao: SugarEntryDao,
    private val medicationDao: MedicationDao
) {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    companion object {
        const val LOINC_SYSTEM = "http://loinc.org"
        const val RXNORM_SYSTEM = "http://www.nlm.nih.gov/research/umls/rxnorm"

        // LOINC Medical Codes
        const val LOINC_FLUID_INTAKE = "9052-2"
        const val LOINC_URINE_OUTPUT = "9209-4"
        const val LOINC_BLOOD_GLUCOSE = "15074-8"
        const val LOINC_BODY_WEIGHT = "29463-7"
    }

    /**
     * Export all clinical data records into HL7 FHIR R4 JSON Bundle.
     */
    suspend fun generateFhirBundleJson(startDate: String = "2000-01-01", endDate: String = "2099-12-31"): String = withContext(Dispatchers.IO) {
        val dailyLogs = dailyLogDao.getDailyLogsListInRange(startDate, endDate)
        val fluidEntries = fluidEntryDao.getEntriesListInRange(startDate, endDate)
        val sugarEntries = sugarEntryDao.getEntriesListInRange(startDate, endDate)
        val medications = medicationDao.getAllList()

        val entries = mutableListOf<FhirBundleEntry>()

        // 1. Patient Resource
        val patientResource = FhirResource(
            resourceType = "Patient",
            id = "trackwell-patient",
            status = "active"
        )
        entries.add(FhirBundleEntry(patientResource))

        // 2. Observations for Daily Logs (Morning Weight)
        dailyLogs.forEach { log ->
            log.morningWeight?.let { weight ->
                entries.add(
                    FhirBundleEntry(
                        FhirResource(
                            resourceType = "Observation",
                            status = "final",
                            code = FhirCodeableConcept(
                                coding = listOf(FhirCoding(LOINC_SYSTEM, LOINC_BODY_WEIGHT, "Body weight")),
                                text = "Body Weight"
                            ),
                            valueQuantity = FhirQuantity(value = weight, unit = "kg", code = "kg"),
                            effectiveDateTime = "${log.date}T08:00:00Z"
                        )
                    )
                )
            }
        }

        // 3. Observations for Fluid & Urine Entries
        fluidEntries.forEach { entry ->
            val loincCode = if (entry.type == "URINE") LOINC_URINE_OUTPUT else LOINC_FLUID_INTAKE
            val label = if (entry.type == "URINE") "Urine Output" else "Fluid Intake"
            entries.add(
                FhirBundleEntry(
                    FhirResource(
                        resourceType = "Observation",
                        status = "final",
                        code = FhirCodeableConcept(
                            coding = listOf(FhirCoding(LOINC_SYSTEM, loincCode, label)),
                            text = "$label (${entry.type})"
                        ),
                        valueQuantity = FhirQuantity(value = entry.amountMl.toDouble(), unit = "mL", code = "mL"),
                        effectiveDateTime = "${entry.date}T12:00:00Z"
                    )
                )
            )
        }

        // 4. Observations for Blood Sugar Entries
        sugarEntries.forEach { entry ->
            entries.add(
                FhirBundleEntry(
                    FhirResource(
                        resourceType = "Observation",
                        status = "final",
                        code = FhirCodeableConcept(
                            coding = listOf(FhirCoding(LOINC_SYSTEM, LOINC_BLOOD_GLUCOSE, "Glucose in Blood")),
                            text = "Blood Glucose (${entry.slot})"
                        ),
                        valueQuantity = FhirQuantity(value = entry.sugarMgDl.toDouble(), unit = "mg/dL", code = "mg/dL"),
                        effectiveDateTime = "${entry.date}T12:00:00Z"
                    )
                )
            )
        }

        // 5. MedicationStatement Resources
        medications.forEach { med ->
            entries.add(
                FhirBundleEntry(
                    FhirResource(
                        resourceType = "MedicationStatement",
                        status = if (med.isTaken) "completed" else "active",
                        medicationCodeableConcept = FhirCodeableConcept(
                            coding = listOf(FhirCoding(RXNORM_SYSTEM, "RxNorm-Med", med.name)),
                            text = med.name
                        ),
                        dosage = listOf(
                            mapOf(
                                "text" to med.dosage,
                                "scheduledTime" to med.scheduledTime
                            )
                        )
                    )
                )
            )
        }

        val bundle = FhirBundle(entry = entries)
        gson.toJson(bundle)
    }

    /**
     * Export FHIR R4 JSON Bundle to a local file in cache directory.
     */
    suspend fun exportFhirBundleToFile(targetFile: File): File = withContext(Dispatchers.IO) {
        val jsonString = generateFhirBundleJson()
        val fos = FileOutputStream(targetFile)
        fos.write(jsonString.toByteArray(StandardCharsets.UTF_8))
        fos.close()
        targetFile
    }
}
