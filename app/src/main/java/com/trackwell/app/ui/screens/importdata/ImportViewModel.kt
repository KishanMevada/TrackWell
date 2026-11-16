package com.trackwell.app.ui.screens.importdata

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.trackwell.app.data.local.dao.DailyLogDao
import com.trackwell.app.data.model.DuplicateResolution
import com.trackwell.app.data.model.ParsedDayRecord
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.data.repository.ImportRepository
import com.trackwell.app.ui.screens.importdata.parser.CsvExcelParser
import com.trackwell.app.ui.screens.importdata.parser.DetectedLayout
import com.trackwell.app.ui.screens.importdata.parser.GeminiVisionParser
import com.trackwell.app.ui.screens.importdata.parser.OfflineLayoutParser
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

sealed class ImportUiState {
    data object Idle : ImportUiState()
    data class Processing(val progressMessage: String) : ImportUiState()
    data class Review(
        val records: List<ParsedDayRecord>,
        val detectedLayout: DetectedLayout,
        val duplicateCount: Int,
        val outlierCount: Int
    ) : ImportUiState()
    data class Success(val batchId: Long, val message: String) : ImportUiState()
    data class Error(val message: String) : ImportUiState()
}

@HiltViewModel
class ImportViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val importRepository: ImportRepository,
    private val dailyLogDao: DailyLogDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<ImportUiState>(ImportUiState.Idle)
    val uiState: StateFlow<ImportUiState> = _uiState.asStateFlow()

    val importBatches = importRepository.observeImportBatches().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun processImages(uris: List<Uri>, useGeminiIfAvailable: Boolean = true) {
        viewModelScope.launch {
            _uiState.value = ImportUiState.Processing("Reading ${uris.size} image(s)...")

            val combinedRecords = mutableListOf<ParsedDayRecord>()
            var detectedLayout = DetectedLayout.FLUID_GRID
            val settings = userPreferences.settingsFlow.first()

            for ((index, uri) in uris.withIndex()) {
                _uiState.value = ImportUiState.Processing("Processing image ${index + 1} of ${uris.size}...")

                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

                    // Try Gemini if configured and requested
                    if (useGeminiIfAvailable && settings.geminiApiKey.isNotBlank()) {
                        val geminiResult = GeminiVisionParser.parseImageWithGemini(bitmap, settings.geminiApiKey)
                        if (geminiResult.isSuccess && geminiResult.getOrNull()?.isNotEmpty() == true) {
                            combinedRecords.addAll(geminiResult.getOrNull()!!)
                            continue
                        }
                    }

                    // Fallback to ML Kit On-device OCR
                    val inputImage = InputImage.fromBitmap(bitmap, 0)
                    val visionText = suspendCancellableCoroutine { cont ->
                        textRecognizer.process(inputImage)
                            .addOnSuccessListener { cont.resume(it) }
                            .addOnFailureListener { cont.resumeWithException(it) }
                    }
                    val rawText = visionText.text

                    detectedLayout = OfflineLayoutParser.detectLayout(rawText)
                    val parsed = OfflineLayoutParser.parseOcrText(rawText)
                    combinedRecords.addAll(parsed)
                } catch (e: Exception) {
                    _uiState.value = ImportUiState.Error("Error scanning image: ${e.localizedMessage}")
                    return@launch
                }
            }

            if (combinedRecords.isEmpty()) {
                _uiState.value = ImportUiState.Error("No valid health routines or dates could be detected in the selected images.")
                return@launch
            }

            // Check duplicates against existing database records
            var duplicateCount = 0
            for (rec in combinedRecords) {
                val existing = dailyLogDao.getDailyLog(rec.date)
                if (existing != null) {
                    rec.isDuplicate = true
                    duplicateCount++
                }
            }

            val outlierCount = combinedRecords.count { it.hasOutliers }

            _uiState.value = ImportUiState.Review(
                records = combinedRecords,
                detectedLayout = detectedLayout,
                duplicateCount = duplicateCount,
                outlierCount = outlierCount
            )
        }
    }

    fun processCsvFile(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = ImportUiState.Processing("Parsing CSV file...")
            try {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@launch
                val reader = BufferedReader(InputStreamReader(inputStream))
                val content = reader.readText()
                reader.close()

                val records = CsvExcelParser.parseCsv(content)
                if (records.isEmpty()) {
                    _uiState.value = ImportUiState.Error("No records found in CSV file.")
                    return@launch
                }

                var duplicateCount = 0
                for (rec in records) {
                    val existing = dailyLogDao.getDailyLog(rec.date)
                    if (existing != null) {
                        rec.isDuplicate = true
                        duplicateCount++
                    }
                }

                _uiState.value = ImportUiState.Review(
                    records = records,
                    detectedLayout = DetectedLayout.UNIFIED_HEALTH,
                    duplicateCount = duplicateCount,
                    outlierCount = records.count { it.hasOutliers }
                )
            } catch (e: Exception) {
                _uiState.value = ImportUiState.Error("Failed to parse file: ${e.localizedMessage}")
            }
        }
    }

    fun updateRecordResolution(recordIndex: Int, resolution: DuplicateResolution) {
        val current = _uiState.value
        if (current is ImportUiState.Review) {
            val updated = current.records.toMutableList()
            updated[recordIndex].duplicateResolution = resolution
            _uiState.value = current.copy(records = updated)
        }
    }

    fun saveReviewBatch() {
        val current = _uiState.value
        if (current is ImportUiState.Review) {
            viewModelScope.launch {
                _uiState.value = ImportUiState.Processing("Saving reviewed data to log...")
                val batchId = importRepository.saveImportBatch(current.records, current.detectedLayout.name)
                _uiState.value = ImportUiState.Success(batchId, "Successfully saved ${current.records.size} days of routine data.")
            }
        }
    }

    fun undoBatch(batchId: Long) {
        viewModelScope.launch {
            importRepository.undoImportBatch(batchId)
        }
    }

    fun resetState() {
        _uiState.value = ImportUiState.Idle
    }
}
