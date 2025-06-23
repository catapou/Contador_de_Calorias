package com.example.contador_de_calorias

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.RadioButton
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row


// Reusable colors
val FadedGreen = Color(0xFF6C9E6C)
val FadedRed = Color(0xFFB36B6B)
val FadedBlue = Color(0xFF6A8EAE)

// Meal data class
data class Meal(
    val name: String,
    val calories: Int,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0,
    val salt: Double = 0.0,
    val fiber: Int = 0,
    val polyols: Int = 0,
    val starch: Int = 0
)

// User information data class
data class UserInfo(
    val dob: String = "",
    val weight: String = "",
    val height: String = "",
    val activityLevel: String = ""
)

// Reusable macro input field
@Composable
fun MacroInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Number,
    isDouble: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (keyboardType == KeyboardType.Number || isDouble) {
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    onValueChange(newValue)
                }
            } else {
                onValueChange(newValue)
            }
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
    Spacer(modifier = Modifier.height(12.dp))
}

// Initial information pop-up
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitialInfoDialog(
    onDismiss: () -> Unit,
    onInfoSubmitted: (UserInfo) -> Unit,
    initialUserInfo: UserInfo // Receives initial state
) {
    // dobInput is now TextFieldValue to control the cursor
    var dobInput by remember { mutableStateOf(TextFieldValue(initialUserInfo.dob)) }
    var weightInput by remember { mutableStateOf(initialUserInfo.weight) }
    var heightInput by remember { mutableStateOf(initialUserInfo.height) }
    var activityExpanded by remember { mutableStateOf(false) }
    val activityLevels = listOf("Sedentary", "Lightly Active", "Moderately Active", "Very Active", "Extra Active")
    var selectedActivityLevel by remember { mutableStateOf(initialUserInfo.activityLevel.ifEmpty { activityLevels[0] }) }

    val isDobValid = remember(dobInput.text) {
        try {
            if (dobInput.text.length == 10 && dobInput.text[2] == '/' && dobInput.text[5] == '/') {
                LocalDate.parse(dobInput.text, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                true
            } else {
                false
            }
        } catch (e: DateTimeParseException) {
            false
        }
    }
    val isWeightValid = remember(weightInput) { weightInput.toIntOrNull() != null && weightInput.toInt() > 0 }
    val isHeightValid = remember(heightInput) { heightInput.toIntOrNull() != null && heightInput.toInt() > 0 }
    val isFormValid = isDobValid && isWeightValid && isHeightValid && selectedActivityLevel.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Tell us about yourself",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = dobInput,
                    onValueChange = { newTextFieldValue ->
                        val currentText = newTextFieldValue.text
                        val digitsOnly = currentText.filter { it.isDigit() }
                        var formattedText = ""
                        var newCursorPosition = newTextFieldValue.selection.start

                        // Logic to automatically add slashes and position the cursor
                        if (digitsOnly.length > 0) {
                            formattedText += digitsOnly.substring(0, minOf(digitsOnly.length, 2))
                            if (digitsOnly.length > 2) {
                                formattedText += "/" + digitsOnly.substring(2, minOf(digitsOnly.length, 4))
                            }
                            if (digitsOnly.length > 4) {
                                formattedText += "/" + digitsOnly.substring(4, minOf(digitsOnly.length, 8))
                            }
                        }

                        // Adjusts cursor position after formatting
                        // If the user is deleting, keeps the cursor position relative
                        if (newTextFieldValue.selection.start < dobInput.text.length) {
                            newCursorPosition = newTextFieldValue.selection.start
                        } else {
                            // Otherwise, puts the cursor at the end of the formatted text
                            newCursorPosition = formattedText.length
                            // But if a slash was inserted, moves the cursor after the slash
                            if (dobInput.text.length == 2 && formattedText.length == 3 && formattedText[2] == '/') {
                                newCursorPosition = 3
                            } else if (dobInput.text.length == 5 && formattedText.length == 6 && formattedText[5] == '/') {
                                newCursorPosition = 6
                            }
                        }


                        dobInput = TextFieldValue(
                            text = formattedText.take(10), // Limits to 10 characters (DD/MM/YYYY)
                            selection = TextRange(newCursorPosition.coerceIn(0, formattedText.take(10).length)) // Ensures cursor stays within bounds
                        )
                    },
                    label = { Text("Date of Birth (DD/MM/YYYY)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                if (dobInput.text.isNotBlank() && !isDobValid) {
                    Text("Invalid date format. Use DD/MM/YYYY.", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))

                MacroInputField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = "Weight (kg)",
                    keyboardType = KeyboardType.Number
                )
                if (weightInput.isNotBlank() && !isWeightValid) {
                    Text("Please enter a valid weight.", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))

                MacroInputField(
                    value = heightInput,
                    onValueChange = { heightInput = it },
                    label = "Height (cm)",
                    keyboardType = KeyboardType.Number
                )
                if (heightInput.isNotBlank() && !isHeightValid) {
                    Text("Please enter a valid height.", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = activityExpanded,
                    onExpandedChange = { activityExpanded = !activityExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedActivityLevel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Activity Level") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = activityExpanded,
                        onDismissRequest = { activityExpanded = false }
                    ) {
                        activityLevels.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    selectedActivityLevel = selectionOption
                                    activityExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onInfoSubmitted(UserInfo(dobInput.text, weightInput, heightInput, selectedActivityLevel))
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(containerColor = FadedBlue, contentColor = Color.White),
                enabled = isFormValid
            ) {
                Text("Continue")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

// Composable for BMI and weight goal dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BMIDialog(
    userInfo: UserInfo,
    bmi: Double?, // BMI already calculated is passed as a parameter
    recommendedCalories: Int?, // Recommended calories passed as a parameter
    selectedGoal: String, // Receive selected goal as a parameter
    onDismiss: () -> Unit,
    onGoalSelected: (String) -> Unit // Callback for selected goal
) {
    // selectedGoal is now received as a parameter, no local mutableStateOf needed
    val goals = listOf("Maintain Weight", "Lose Weight", "Gain Weight")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Your BMI & Goal",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                if (bmi != null) {
                    Text(
                        "Your BMI: ${String.format("%.2f", bmi)}", // Formats BMI to 2 decimal places
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        when {
                            bmi < 18.5 -> "Underweight"
                            bmi >= 18.5 && bmi < 24.9 -> "Normal weight"
                            bmi >= 25 && bmi < 29.9 -> "Overweight"
                            else -> "Obese"
                        },
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                } else {
                    Text(
                        "Could not calculate BMI. Please ensure weight and height are valid.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "What is your weight goal?",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                goals.forEach { goal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGoalSelected(goal) } // Passes the selected goal immediately
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedGoal == goal), // Now compares against the passed parameter
                            onClick = { onGoalSelected(goal) } // Passes the selected goal immediately
                        )
                        Text(goal, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                    }
                }

                if (recommendedCalories != null && selectedGoal.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Recommended daily calories for ${selectedGoal.lowercase()}: ${recommendedCalories} Kcal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss, // Just dismisses, goal is handled by radio button click
                colors = ButtonDefaults.textButtonColors(containerColor = FadedBlue, contentColor = Color.White),
                enabled = true // Always enabled to dismiss
            ) {
                Text("OK")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}


// Macro summary dialog
@Composable
fun MacroSummaryDialog(mealList: List<Meal>, onDismiss: () -> Unit) {
    val totalCalories = mealList.sumOf { it.calories }
    val totalProtein = mealList.sumOf { it.protein }
    val totalCarbs = mealList.sumOf { it.carbs }
    val totalFats = mealList.sumOf { it.fats }
    val totalSalt = mealList.sumOf { it.salt }
    val totalFiber = mealList.sumOf { it.fiber }
    val totalPolyols = mealList.sumOf { it.polyols }
    val totalStarch = mealList.sumOf { it.starch }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Daily Macro Summary",
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                Text(
                    "Total Calories: $totalCalories Kcal",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Total Protein: $totalProtein g",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Total Carbohydrates: $totalCarbs g",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Total Fats: $totalFats g",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Total Salt: ${String.format("%.1f", totalSalt)} g",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Total Fiber: $totalFiber g",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Total Polyols: $totalPolyols g",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Total Starch: $totalStarch g",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, colors = ButtonDefaults.textButtonColors(contentColor = FadedGreen)) {
                Text("OK")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
