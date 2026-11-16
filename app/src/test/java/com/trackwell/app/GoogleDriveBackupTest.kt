package com.trackwell.app

import com.trackwell.app.data.local.entity.DailyLogEntity
import com.trackwell.app.data.local.entity.FluidEntryEntity
import com.trackwell.app.data.local.entity.SugarEntryEntity
import com.trackwell.app.ui.screens.exportdata.generator.AppBackupData
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class GoogleDriveBackupTest {

    private val defaultKeyBytes = "TrackWellSecureBackupKey2026Transplant".toByteArray(StandardCharsets.UTF_8).copyOf(32)
    private val gson = Gson()

    @Test
    fun testBackupEncryptionAndDecryptionCycle() {
        val backupData = AppBackupData(
            version = 1,
            exportDate = "2026-11-14",
            dailyLogs = listOf(
                DailyLogEntity(date = "2026-11-14", morningWeight = 55.85)
            ),
            fluidEntries = listOf(
                FluidEntryEntity(id = 1, date = "2026-11-14", timestamp = 1788220000000, amountMl = 200, type = "INTAKE"),
                FluidEntryEntity(id = 2, date = "2026-11-14", timestamp = 1788225000000, amountMl = 300, type = "URINE")
            ),
            sugarEntries = listOf(
                SugarEntryEntity(id = 1, date = "2026-11-14", slot = "BEFORE_BREAKFAST", sugarMgDl = 91, insulinUnits = 0.0, timestamp = 1788220000000)
            ),
            optionalMetrics = emptyList(),
            medications = emptyList()
        )

        // 1. Serialize
        val plaintextJson = gson.toJson(backupData)
        val plaintextBytes = plaintextJson.toByteArray(StandardCharsets.UTF_8)

        // 2. Encrypt with AES-GCM
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        val secretKey = SecretKeySpec(defaultKeyBytes, "AES")
        val encryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        val ciphertext = encryptCipher.doFinal(plaintextBytes)

        assertTrue(ciphertext.isNotEmpty())

        // 3. Decrypt
        val decryptCipher = Cipher.getInstance("AES/GCM/NoPadding")
        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        val decryptedBytes = decryptCipher.doFinal(ciphertext)
        val decryptedJson = String(decryptedBytes, StandardCharsets.UTF_8)

        val restoredData = gson.fromJson(decryptedJson, AppBackupData::class.java)

        assertNotNull(restoredData)
        assertEquals(1, restoredData.dailyLogs.size)
        assertEquals(55.85, restoredData.dailyLogs[0].morningWeight ?: 0.0, 0.01)
        assertEquals(2, restoredData.fluidEntries.size)
        assertEquals("INTAKE", restoredData.fluidEntries[0].type)
        assertEquals(1, restoredData.sugarEntries.size)
        assertEquals(91, restoredData.sugarEntries[0].sugarMgDl)
    }
}
