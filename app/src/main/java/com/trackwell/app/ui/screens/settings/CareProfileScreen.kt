package com.trackwell.app.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trackwell.app.data.preferences.AppSettings
import com.trackwell.app.data.preferences.UserPreferences
import com.trackwell.app.ui.home.components.VascularBackground
import com.trackwell.app.ui.home.components.neuRaisedL
import com.trackwell.app.ui.theme.TargetTokens
import com.trackwell.app.ui.theme.TargetTypography
import kotlinx.coroutines.launch

@Composable
fun CareProfileScreen(
    userPreferences: UserPreferences,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.activity.compose.BackHandler {
        onNavigateUp()
    }

    val settings by userPreferences.settingsFlow.collectAsState(initial = AppSettings())
    val scope = rememberCoroutineScope()
    val statusBarInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    var patientName by remember(settings) { mutableStateOf(settings.patientName) }
    var healthContext by remember(settings) { mutableStateOf(settings.healthContext) }
    var bloodGroup by remember(settings) { mutableStateOf(settings.bloodGroup) }
    var transplantDate by remember(settings) { mutableStateOf(settings.transplantDate) }
    var emergencyNotes by remember(settings) { mutableStateOf(settings.emergencyNotes) }
    var nephrologistContact by remember(settings) { mutableStateOf(settings.nephrologistContact) }
    var isSaved by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TargetTokens.bg)
    ) {
        VascularBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(statusBarInset + 8.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateUp,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TargetTokens.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Care Profile",
                        style = TargetTypography.greeting.copy(fontSize = 22.sp),
                        color = TargetTokens.textPrimary
                    )
                    Text(
                        text = "Optional medical details",
                        style = TargetTypography.subtitle.copy(fontSize = 12.sp),
                        color = TargetTokens.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Informational Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .neuRaisedL(cornerRadius = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(TargetTokens.surface)
                    .border(1.dp, TargetTokens.chipBlue, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Add medical details only if they help you manage your care. This data remains completely private on your device.",
                        fontSize = 12.5.sp,
                        color = Color(0xFF0369A1),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Form Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .neuRaisedL(cornerRadius = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(TargetTokens.surface)
                    .border(1.dp, TargetTokens.divider, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = patientName,
                        onValueChange = { patientName = it },
                        label = { Text("Preferred Name / Nickname") },
                        placeholder = { Text("e.g. Alex") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = TargetTokens.divider
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = healthContext,
                        onValueChange = { healthContext = it },
                        label = { Text("Health Context / Care Goal") },
                        placeholder = { Text("e.g. Kidney Care, Diabetes, General Fitness") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = TargetTokens.divider
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = { bloodGroup = it },
                        label = { Text("Blood Group") },
                        placeholder = { Text("e.g. O+, B+") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = TargetTokens.divider
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = transplantDate,
                        onValueChange = { transplantDate = it },
                        label = { Text("Transplant Date") },
                        placeholder = { Text("YYYY-MM-DD") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = TargetTokens.divider
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = nephrologistContact,
                        onValueChange = { nephrologistContact = it },
                        label = { Text("Emergency Contact Only") },
                        placeholder = { Text("e.g. +1 (555) 019-2834") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = TargetTokens.divider
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = emergencyNotes,
                        onValueChange = { emergencyNotes = it },
                        label = { Text("Emergency / Clinical Notes") },
                        placeholder = { Text("e.g. Health conditions, allergies, or emergency info") },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF0284C7),
                            unfocusedBorderColor = TargetTokens.divider
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                userPreferences.updateCareProfile(
                                    patientName = patientName,
                                    healthContext = healthContext,
                                    bloodGroup = bloodGroup,
                                    transplantDate = transplantDate,
                                    emergencyNotes = emergencyNotes,
                                    nephrologistContact = nephrologistContact
                                )
                                isSaved = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isSaved) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Saved Successfully", fontWeight = FontWeight.Bold)
                            } else {
                                Text("Save Care Profile", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
