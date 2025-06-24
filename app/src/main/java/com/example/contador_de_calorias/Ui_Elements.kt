package com.example.contador_de_calorias

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.foundation.layout.Arrangement
import kotlin.math.roundToInt

// Definição de cores personalizadas para a UI
val FadedGreen = Color(0xFF6C9E6C)
val FadedRed = Color(0xFFB36B6B)
val FadedBlue = Color(0xFF6A8EAE)


 // Data class que representa uma refeição, incluindo seus valores nutricionais.

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


 // Data class que representa uma receita, com os mesmos valores nutricionais de uma refeição.

data class Recipe(
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

 //Data class que armazena as informações do perfil do utilizador.

data class UserInfo(
    val dob: String = "",
    val weight: String = "",
    val height: String = "",
    val activityLevel: String = "",
    val gender: String = ""
)


 //Um campo de input estilizado para valores nutricionais e outros dados.

@Composable
fun MacroInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Number,
    isDouble: Boolean = false,
    isRequired: Boolean = false
) {
    val displayLabel = if (isRequired) "$label *" else label // Adiciona "*" se for obrigatório
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            // Validação de input para números e decimais
            if (keyboardType == KeyboardType.Number || isDouble) {
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    onValueChange(newValue)
                }
            } else {
                onValueChange(newValue)
            }
        },
        label = { Text(displayLabel) }, // Usa o rótulo com ou sem asterisco
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

/**
 * Diálogo para coletar as informações iniciais do utilizador (data de nascimento, peso, altura,
 * nível de atividade e género).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitialInfoDialog(
    onDismiss: () -> Unit,
    onInfoSubmitted: (UserInfo) -> Unit,
    initialUserInfo: UserInfo
) {
    // Estados para os campos de input do utilizador
    var dobInput by remember { mutableStateOf(TextFieldValue(initialUserInfo.dob)) }
    var weightInput by remember { mutableStateOf(initialUserInfo.weight) }
    var heightInput by remember { mutableStateOf(initialUserInfo.height) }
    var activityExpanded by remember { mutableStateOf(false) }
    val activityLevels = listOf("Sedentary", "Lightly Active", "Moderately Active", "Very Active", "Extra Active")
    var selectedActivityLevel by remember { mutableStateOf(initialUserInfo.activityLevel.ifEmpty { activityLevels[0] }) }
    var selectedGender by remember { mutableStateOf(initialUserInfo.gender) }

    // Validação dos campos do formulário
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
    val isFormValid = isDobValid && isWeightValid && isHeightValid && selectedActivityLevel.isNotBlank() && selectedGender.isNotBlank()

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
            // Conteúdo do diálogo com campos de input e validações
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Campo de input para Data de Nascimento com formatação
                OutlinedTextField(
                    value = dobInput,
                    onValueChange = { newTextFieldValue ->
                        val currentText = newTextFieldValue.text
                        val digitsOnly = currentText.filter { it.isDigit() }
                        var formattedText = ""
                        var newCursorPosition = newTextFieldValue.selection.start

                        if (digitsOnly.length > 0) {
                            formattedText += digitsOnly.substring(0, minOf(digitsOnly.length, 2))
                            if (digitsOnly.length > 2) {
                                formattedText += "/" + digitsOnly.substring(2, minOf(digitsOnly.length, 4))
                            }
                            if (digitsOnly.length > 4) {
                                formattedText += "/" + digitsOnly.substring(4, minOf(digitsOnly.length, 8))
                            }
                        }

                        if (newTextFieldValue.selection.start < dobInput.text.length) {
                            newCursorPosition = newTextFieldValue.selection.start
                        } else {
                            newCursorPosition = formattedText.length
                            if (dobInput.text.length == 2 && formattedText.length == 3 && formattedText[2] == '/') {
                                newCursorPosition = 3
                            } else if (dobInput.text.length == 5 && formattedText.length == 6 && formattedText[5] == '/') {
                                newCursorPosition = 6
                            }
                        }

                        dobInput = TextFieldValue(
                            text = formattedText.take(10),
                            selection = TextRange(newCursorPosition.coerceIn(0, formattedText.take(10).length))
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

                // Campos de input para Peso e Altura
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

                // Dropdown para Nível de Atividade
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

                // Seleção de Género com RadioButtons
                Text(
                    "Gender",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedGender = "Man" }) {
                        RadioButton(
                            selected = (selectedGender == "Man"),
                            onClick = { selectedGender = "Man" }
                        )
                        Text("Man", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedGender = "Woman" }) {
                        RadioButton(
                            selected = (selectedGender == "Woman"),
                            onClick = { selectedGender = "Woman" }
                        )
                        Text("Woman", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                    }
                }
                if (selectedGender.isBlank()) {
                    Text("Please select your gender.", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        },
        confirmButton = {
            // Botão de Confirmação, habilitado apenas se o formulário for válido
            TextButton(
                onClick = {
                    onInfoSubmitted(UserInfo(dobInput.text, weightInput, heightInput, selectedActivityLevel, selectedGender))
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

/**
 * Diálogo para exibir o IMC do utilizador e permitir a seleção de uma meta de peso,
 * com cálculo de calorias recomendadas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BMIDialog(
    userInfo: UserInfo,
    bmi: Double?,
    recommendedCalories: Int?,
    selectedGoal: String,
    onDismiss: () -> Unit,
    onGoalSelected: (String) -> Unit
) {
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
                // Exibição do IMC e sua classificação
                if (bmi != null) {
                    Text(
                        "Your BMI: ${String.format("%.2f", bmi)}",
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

                // Seleção da meta de peso
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
                            .clickable { onGoalSelected(goal) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedGoal == goal),
                            onClick = { onGoalSelected(goal) }
                        )
                        Text(goal, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
                    }
                }

                // Exibição de calorias diárias recomendadas
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
            // Botão de Confirmação para fechar o diálogo
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(containerColor = FadedBlue, contentColor = Color.White),
                enabled = selectedGoal.isNotBlank()
            ) {
                Text("OK")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

//Diálogo para exibir um resumo dos valores nutricionais totais de todas as refeições.

@Composable
fun MacroSummaryDialog(mealList: List<Meal>, onDismiss: () -> Unit) {
    // Cálculo dos totais de calorias e macronutrientes
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
            // Exibição dos totais de cada macronutriente
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

/**
 * Diálogo para adicionar uma nova receita, permitindo ao utilizador inserir o nome
 * e os valores nutricionais por 100g.
 */
@Composable
fun AddRecipeDialog(
    onDismiss: () -> Unit,
    onRecipeAdded: (Recipe) -> Unit
) {
    // Estados para os campos de input da receita
    var recipeName by remember { mutableStateOf("") }
    var recipeCalories by remember { mutableStateOf("") }
    var recipeProtein by remember { mutableStateOf("") }
    var recipeCarbs by remember { mutableStateOf("") }
    var recipeFats by remember { mutableStateOf("") }
    var recipeSalt by remember { mutableStateOf("") }
    var recipeFiber by remember { mutableStateOf("") }
    var recipePolyols by remember { mutableStateOf("") }
    var recipeStarch by remember { mutableStateOf("") }

    // Validação do formulário: nome e calorias são obrigatórios
    val isFormValid = recipeName.isNotBlank() && (recipeCalories.toIntOrNull() ?: 0) > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Add a New Recipe",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            // Campos de input para os detalhes da receita
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    "Values are for 100g",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                MacroInputField(value = recipeName, onValueChange = { recipeName = it }, label = "Recipe Name", keyboardType = KeyboardType.Text, isRequired = true)
                MacroInputField(value = recipeCalories, onValueChange = { recipeCalories = it }, label = "Calories (Kcal)", isRequired = true)
                MacroInputField(value = recipeProtein, onValueChange = { recipeProtein = it }, label = "Protein (g)")
                MacroInputField(value = recipeCarbs, onValueChange = { recipeCarbs = it }, label = "Carbohydrates (g)")
                MacroInputField(value = recipeFats, onValueChange = { recipeFats = it }, label = "Fats (g)")
                MacroInputField(value = recipeSalt, onValueChange = { recipeSalt = it }, label = "Salt (g)", isDouble = true)
                MacroInputField(value = recipeFiber, onValueChange = { recipeFiber = it }, label = "Fiber (g)")
                MacroInputField(value = recipePolyols, onValueChange = { recipePolyols = it }, label = "Polyols (g)")
                MacroInputField(value = recipeStarch, onValueChange = { recipeStarch = it }, label = "Starch (g)")
            }
        },
        confirmButton = {
            // Botão "Add Recipe", habilitado se o formulário for válido
            TextButton(
                onClick = {
                    // Cria uma nova receita com os dados inseridos
                    val newRecipe = Recipe(
                        name = recipeName.trim(),
                        calories = recipeCalories.toIntOrNull() ?: 0,
                        protein = recipeProtein.toIntOrNull() ?: 0,
                        carbs = recipeCarbs.toIntOrNull() ?: 0,
                        fats = recipeFats.toIntOrNull() ?: 0,
                        salt = recipeSalt.toDoubleOrNull() ?: 0.0,
                        fiber = recipeFiber.toIntOrNull() ?: 0,
                        polyols = recipePolyols.toIntOrNull() ?: 0,
                        starch = recipeStarch.toIntOrNull() ?: 0
                    )
                    onRecipeAdded(newRecipe) // Envia a nova receita para a lógica principal
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(containerColor = FadedGreen, contentColor = Color.White),
                enabled = isFormValid
            ) {
                Text("Add Recipe")
            }
        },
        dismissButton = {
            // Botão "Cancel" para descartar o diálogo
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = FadedRed)
            ) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

/**
 * Diálogo para o utilizador inserir a quantidade (em gramas) de uma receita selecionada,
 * e ver os valores nutricionais calculados para essa quantidade.
 */
@Composable
fun QuantityInputDialog(
    recipe: Recipe,
    onDismiss: () -> Unit,
    onMealCalculated: (Meal) -> Unit
) {
    var quantityGrams by remember { mutableStateOf("") }
    val grams = quantityGrams.toDoubleOrNull() ?: 0.0

    // Cálculo dos valores nutricionais com base na quantidade inserida
    val calculatedCalories = remember(grams) { (recipe.calories * (grams / 100.0)).roundToInt() }
    val calculatedProtein = remember(grams) { (recipe.protein * (grams / 100.0)).roundToInt() }
    val calculatedCarbs = remember(grams) { (recipe.carbs * (grams / 100.0)).roundToInt() }
    val calculatedFats = remember(grams) { (recipe.fats * (grams / 100.0)).roundToInt() }
    val calculatedSalt = remember(grams) { recipe.salt * (grams / 100.0) }
    val calculatedFiber = remember(grams) { (recipe.fiber * (grams / 100.0)).roundToInt() }
    val calculatedPolyols = remember(grams) { (recipe.polyols * (grams / 100.0)).roundToInt() }
    val calculatedStarch = remember(grams) { (recipe.starch * (grams / 100.0)).roundToInt() }

    // Habilita o botão "Confirm" apenas se a quantidade for maior que zero
    val isConfirmEnabled = grams > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Enter Quantity for ${recipe.name}", color = MaterialTheme.colorScheme.onSurface)
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Campo de input para a quantidade em gramas
                MacroInputField(
                    value = quantityGrams,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            quantityGrams = newValue
                        }
                    },
                    label = "Quantity (grams)",
                    keyboardType = KeyboardType.Number,
                    isDouble = true,
                    isRequired = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Exibição dos valores nutricionais calculados
                Text("Calculated Nutritional Values:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("Calories: ${calculatedCalories} Kcal", color = MaterialTheme.colorScheme.onSurface)
                Text("Protein: ${calculatedProtein} g", color = MaterialTheme.colorScheme.onSurface)
                Text("Carbohydrates: ${calculatedCarbs} g", color = MaterialTheme.colorScheme.onSurface)
                Text("Fats: ${calculatedFats} g", color = MaterialTheme.colorScheme.onSurface)
                Text("Salt: ${String.format("%.1f", calculatedSalt)} g", color = MaterialTheme.colorScheme.onSurface)
                Text("Fiber: ${calculatedFiber} g", color = MaterialTheme.colorScheme.onSurface)
                Text("Polyols: ${calculatedPolyols} g", color = MaterialTheme.colorScheme.onSurface)
                Text("Starch: ${calculatedStarch} g", color = MaterialTheme.colorScheme.onSurface)
            }
        },
        confirmButton = {
            // Botão "Confirm", habilitado se a quantidade for válida
            TextButton(
                onClick = {
                    val meal = Meal(
                        name = "${recipe.name} (${grams.roundToInt()}g)",
                        calories = calculatedCalories,
                        protein = calculatedProtein,
                        carbs = calculatedCarbs,
                        fats = calculatedFats,
                        salt = calculatedSalt,
                        fiber = calculatedFiber,
                        polyols = calculatedPolyols,
                        starch = calculatedStarch
                    )
                    onMealCalculated(meal)
                },
                colors = ButtonDefaults.textButtonColors(contentColor = FadedGreen),
                enabled = isConfirmEnabled
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            // Botão "Cancel" para descartar o diálogo
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = FadedRed)
            ) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}


 // Diálogo para adicionar uma refeição personalizada, permitindo inserir todos os detalhes

@Composable
fun CustomMealInputDialog(
    onDismiss: () -> Unit,
    onMealAdded: (Meal) -> Unit,
    mealName: String,
    mealCalories: String,
    mealProtein: String,
    mealCarbs: String,
    mealFats: String,
    mealSalt: String,
    mealFiber: String,
    mealPolyols: String,
    mealStarch: String,
    onNameChange: (String) -> Unit,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
    onFatsChange: (String) -> Unit,
    onSaltChange: (String) -> Unit,
    onFiberChange: (String) -> Unit,
    onPolyolsChange: (String) -> Unit,
    onStarchChange: (String) -> Unit
) {
    // Validação do formulário: nome e calorias são obrigatórios
    val isConfirmEnabled = mealName.isNotBlank() && (mealCalories.toIntOrNull() ?: 0) > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Custom Meal", color = MaterialTheme.colorScheme.onSurface)
        },
        text = {
            // Campos de input para os detalhes da refeição personalizada
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                MacroInputField(value = mealName, onValueChange = onNameChange, label = "Meal name", keyboardType = KeyboardType.Text, isRequired = true)
                MacroInputField(value = mealCalories, onValueChange = onCaloriesChange, label = "calories (Kcal)", isRequired = true)
                MacroInputField(value = mealProtein, onValueChange = onProteinChange, label = "Protein (g)")
                MacroInputField(value = mealCarbs, onValueChange = onCarbsChange, label = "Carbohydrates (g)")
                MacroInputField(value = mealFats, onValueChange = onFatsChange, label = "Fats (g)")
                MacroInputField(value = mealSalt, onValueChange = onSaltChange, label = "Salt (g)", isDouble = true)
                MacroInputField(value = mealFiber, onValueChange = onFiberChange, label = "Fiber (g)")
                MacroInputField(value = mealPolyols, onValueChange = onPolyolsChange, label = "Polyols (g)")
                MacroInputField(value = mealStarch, onValueChange = onStarchChange, label = "Starch (g)")
            }
        },
        confirmButton = {
            // Botão "Confirm", habilitado se o formulário for válido
            TextButton(
                onClick = {
                    // Cria o objeto Meal com os dados inseridos
                    val newMeal = Meal(
                        name = mealName.trim(),
                        calories = mealCalories.toIntOrNull() ?: 0,
                        protein = mealProtein.toIntOrNull() ?: 0,
                        carbs = mealCarbs.toIntOrNull() ?: 0,
                        fats = mealFats.toIntOrNull() ?: 0,
                        salt = mealSalt.toDoubleOrNull() ?: 0.0,
                        fiber = mealFiber.toIntOrNull() ?: 0,
                        polyols = mealPolyols.toIntOrNull() ?: 0,
                        starch = mealStarch.toIntOrNull() ?: 0
                    )
                    onMealAdded(newMeal)
                },
                colors = ButtonDefaults.textButtonColors(contentColor = FadedGreen),
                enabled = isConfirmEnabled
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            // Botão "Cancel" para descartar o diálogo
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = FadedRed)
            ) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}


 //Calcula a Taxa Metabólica Basal (BMR) usando a fórmula de Mifflin-St Jeor.

fun calculateBMR(weightKg: Double, heightCm: Double, ageYears: Int, gender: String): Double {
    return when (gender) {
        "Man" -> (10 * weightKg) + (6.25 * heightCm) - (5 * ageYears) + 5
        "Woman" -> (10 * weightKg) + (6.25 * heightCm) - (5 * ageYears) - 161
        else -> 0.0
    }
}


 //Calcula o Gasto Energético Diário Total (TDEE) com base na BMR e no nível de atividade.

fun calculateTDEE(bmr: Double, activityLevel: String): Int {
    val activityFactor = when (activityLevel) {
        "Sedentary" -> 1.2
        "Lightly Active" -> 1.375
        "Moderately Active" -> 1.55
        "Very Active" -> 1.725
        "Extra Active" -> 1.9
        else -> 1.2
    }
    return (bmr * activityFactor).roundToInt()
}

 // Calcula as calorias diárias recomendadas com base no TDEE e na meta de peso.

fun getRecommendedCalories(tdee: Int, goal: String): Int {
    return when (goal) {
        "Maintain Weight" -> tdee
        "Lose Weight" -> tdee - 500
        "Gain Weight" -> tdee + 500
        else -> tdee
    }
}
