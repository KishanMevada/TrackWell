package com.trackwell.app.ui.screens.importdata.parser

import android.graphics.Bitmap
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.trackwell.app.data.model.ParsedDayRecord
import com.trackwell.app.data.model.ParsedFluidItem
import com.trackwell.app.data.model.ParsedSugarItem
import com.trackwell.app.domain.model.FluidType
import com.trackwell.app.domain.model.SugarSlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object GeminiVisionParser {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun parseImageWithGemini(
        bitmap: Bitmap,
        apiKey: String
    ): Result<List<ParsedDayRecord>> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Gemini API key is not configured."))
        }

        try {
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, bos)
            val base64Image = Base64.encodeToString(bos.toByteArray(), Base64.NO_WRAP)

            val prompt = """
                You are a medical OCR parser for a kidney transplant routine log.
                Extract all dates, morning weight in kg, fluid intake entries, urine output entries, and blood sugar / insulin entries.
                Respond strictly with valid JSON only in this format:
                [
                  {
                    "date": "YYYY-MM-DD",
                    "morningWeight": 70.2,
                    "intakeList": [200, 250],
                    "urineList": [300, 400],
                    "sugarEntries": [
                      {"slot": "BEFORE_BREAKFAST", "sugar": 110, "insulin": 4.0},
                      {"slot": "BEFORE_LUNCH", "sugar": 125, "insulin": 6.0},
                      {"slot": "AFTER_LUNCH", "sugar": 140, "insulin": 0.0},
                      {"slot": "BEFORE_DINNER", "sugar": 130, "insulin": 4.0}
                    ]
                  }
                ]
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                            put(JSONObject().apply {
                                put("inline_data", JSONObject().apply {
                                    put("mime_type", "image/jpeg")
                                    put("data", base64Image)
                                })
                            })
                        })
                    })
                })
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
            val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder().url(url).post(requestBody).build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Gemini request failed: ${response.code} ${response.message}"))
            }

            val responseBody = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response body"))
            val jsonRoot = JSONObject(responseBody)
            val candidates = jsonRoot.getJSONArray("candidates")
            val candidate = candidates.getJSONObject(0)
            val content = candidate.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            val text = parts.getJSONObject(0).getString("text")

            val cleanJson = text.replace("```json", "").replace("```", "").trim()
            val jsonArray = JSONArray(cleanJson)

            val records = mutableListOf<ParsedDayRecord>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val date = obj.getString("date")
                val morningWeight = if (obj.has("morningWeight") && !obj.isNull("morningWeight")) obj.getDouble("morningWeight") else null

                val record = ParsedDayRecord(date = date, morningWeightKg = morningWeight)

                if (obj.has("intakeList")) {
                    val intakeArr = obj.getJSONArray("intakeList")
                    for (j in 0 until intakeArr.length()) {
                        record.fluidItems.add(
                            ParsedFluidItem(amountMl = intakeArr.getInt(j), type = FluidType.INTAKE)
                        )
                    }
                }

                if (obj.has("urineList")) {
                    val urineArr = obj.getJSONArray("urineList")
                    for (j in 0 until urineArr.length()) {
                        record.fluidItems.add(
                            ParsedFluidItem(amountMl = urineArr.getInt(j), type = FluidType.URINE)
                        )
                    }
                }

                if (obj.has("sugarEntries")) {
                    val sugarArr = obj.getJSONArray("sugarEntries")
                    for (j in 0 until sugarArr.length()) {
                        val sugarObj = sugarArr.getJSONObject(j)
                        val slot = try { SugarSlot.valueOf(sugarObj.getString("slot")) } catch (e: Exception) { SugarSlot.BEFORE_BREAKFAST }
                        val sugar = sugarObj.getInt("sugar")
                        val insulin = if (sugarObj.has("insulin")) sugarObj.getDouble("insulin") else 0.0
                        record.sugarItems[slot] = ParsedSugarItem(slot = slot, sugarMgDl = sugar, insulinUnits = insulin)
                    }
                }

                records.add(record)
            }

            Result.success(records)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
