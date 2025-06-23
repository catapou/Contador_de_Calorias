package com.example.contador_de_calorias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Person // Import para Icons.Filled.Person adicionado

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.LinearProgressIndicator // Import para a barra de progresso
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt
// import androidx.compose.ui.graphics.lerp // Removido: vamos fazer a interpolação manualmente


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkMode by remember { mutableStateOf(true) }

            val LightColors = lightColorScheme(
                primary = Color(0xFF6200EE),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFBB86FC),
                onPrimaryContainer = Color.Black,
                secondary = Color(0xFF03DAC6),
                onSecondary = Color.Black,
                secondaryContainer = Color(0xFF03DAC6),
                onSecondaryContainer = Color.Black,
                tertiary = Color(0xFF3700B3),
                onTertiary = Color.White,
                background = Color.White,
                onBackground = Color.Black,
                surface = Color.White,
                onSurface = Color.Black,
                error = Color(0xFFB00020),
                onError = Color.White
            )

            val DarkColors = darkColorScheme(
                primary = Color(0xFFBB86FC),
                onPrimary = Color.Black,
                primaryContainer = Color(0xFF3700B3),
                onPrimaryContainer = Color.White,
                secondary = Color(0xFF03DAC6),
                onSecondary = Color.Black,
                secondaryContainer = Color(0xFF03DAC6),
                onSecondaryContainer = Color.Black,
                tertiary = Color(0xFFBB86FC),
                onTertiary = Color.Black,
                background = Color(0xFF121212),
                onBackground = Color.White,
                surface = Color(0xFF1E1E1E),
                onSurface = Color.White,
                error = Color(0xFFCF6679),
                onError = Color.Black
            )

            MaterialTheme(
                colorScheme = if (isDarkMode) DarkColors else LightColors
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalorieHomeScreen(isDarkMode = isDarkMode, toggleTheme = { isDarkMode = !isDarkMode })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieHomeScreen(isDarkMode: Boolean, toggleTheme: () -> Unit) {
    var calorieInput by remember { mutableStateOf("") }
    var showMealDialog by remember { mutableStateOf(false) }
    var showRemoveMealDialog by remember { mutableStateOf(false) }
    var showEditMealDialog by remember { mutableStateOf(false) }
    var showMacroSummaryDialog by remember { mutableStateOf(false) }

    // Estado para controlar a exibição do diálogo de informações iniciais
    // Inicialmente verdadeiro para o primeiro carregamento, pode ser falso após a primeira entrada
    var showInitialInfoDialog by remember { mutableStateOf(true) }
    // Estado para controlar a exibição do diálogo de IMC
    var showBMIDialog by remember { mutableStateOf(false) }
    // Estado para armazenar as informações do utilizador
    var userInfo by remember { mutableStateOf(UserInfo()) } // ERRO: UserInfo não será reconhecido sem importação
    // Estado para controlar a exibição do diálogo de informações do utilizador através do menu
    var showUserInfoDialog by remember { mutableStateOf(false) }
    // Estado para armazenar o objetivo de peso do utilizador
    var userWeightGoal by remember { mutableStateOf("") }
    // Estado para armazenar as calorias recomendadas
    var recommendedCalories by remember { mutableStateOf<Int?>(null) }


    var mealName by remember { mutableStateOf("") }
    var mealCalories by remember { mutableStateOf("") }
    var mealProtein by remember { mutableStateOf("") }
    var mealCarbs by remember { mutableStateOf("") }
    var mealFats by remember { mutableStateOf("") }
    var mealSalt by remember { mutableStateOf("") }
    var mealFiber by remember { mutableStateOf("") }
    var mealPolyols by remember { mutableStateOf("") }
    var mealStarch by remember { mutableStateOf("") }

    var selectedMealToEdit by remember { mutableStateOf<Meal?>(null) } // ERRO: Meal não será reconhecido sem importação
    var editedMealName by remember { mutableStateOf("") }
    var editedMealCalories by remember { mutableStateOf("") }
    var editedMealProtein by remember { mutableStateOf("") }
    var editedMealCarbs by remember { mutableStateOf("") }
    var editedMealFats by remember { mutableStateOf("") }
    var editedMealSalt by remember { mutableStateOf("") }
    var editedMealFiber by remember { mutableStateOf("") }
    var editedMealPolyols by remember { mutableStateOf("") }
    var editedMealStarch by remember { mutableStateOf("") }

    val mealList = remember { mutableStateListOf<Meal>() } // ERRO: Meal não será reconhecido sem importação

    val totalMealCalories = mealList.sumOf { it.calories }

    val dailyLimit = calorieInput.toIntOrNull() ?: 0
    val remainingCalories = dailyLimit - totalMealCalories

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val topSpacing = screenHeight * 0.075f
    val lineSpacing = screenHeight * 0.05f

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Definindo as cores FadedGreen, FadedRed e FadedBlue aqui para uso na MainActivity
    // Isso é feito para evitar o erro de "Unresolved reference" já que o import de Ui_Elements.* foi removido.
    val FadedGreen = Color(0xFF6C9E6C)
    val FadedRed = Color(0xFFB36B6B)
    val FadedBlue = Color(0xFF6A8EAE)

    // Função manual para interpolação de cores, substituindo Color.lerp
    fun manualColorLerp(start: Color, end: Color, fraction: Float): Color {
        val inverseFraction = 1 - fraction
        val red = start.red * inverseFraction + end.red * fraction
        val green = start.green * inverseFraction + end.green * fraction
        val blue = start.blue * inverseFraction + end.blue * fraction
        val alpha = start.alpha * inverseFraction + end.alpha * fraction
        return Color(red, green, blue, alpha)
    }

    // Função para calcular a TMB (Taxa Metabólica Basal) - Equação de Mifflin-St Jeor (assumindo masculino por agora)
    // O input de género seria necessário para um cálculo mais preciso
    fun calculateBMR(weightKg: Double, heightCm: Double, ageYears: Int): Double {
        // Equação de Mifflin-St Jeor para Homens
        return (10 * weightKg) + (6.25 * heightCm) - (5 * ageYears) + 5
    }

    // Função para calcular o GET (Gasto Energético Total Diário) com base no nível de atividade
    fun calculateTDEE(bmr: Double, activityLevel: String): Int {
        val activityFactor = when (activityLevel) {
            "Sedentary" -> 1.2
            "Lightly Active" -> 1.375
            "Moderately Active" -> 1.55
            "Very Active" -> 1.725
            "Extra Active" -> 1.9
            else -> 1.2 // Padrão para sedentário
        }
        return (bmr * activityFactor).roundToInt()
    }

    // Função para calcular as calorias recomendadas com base no objetivo de peso
    fun getRecommendedCalories(tdee: Int, goal: String): Int {
        return when (goal) {
            "Maintain Weight" -> tdee
            "Lose Weight" -> tdee - 500 // Objetivo de perder aprox. 0.5 kg/semana
            "Gain Weight" -> tdee + 500 // Objetivo de ganhar aprox. 0.5 kg/semana
            else -> tdee
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(screenWidth * 0.7f)) {
                // Substituído o texto "Menu" pelo logo
                Image(
                    painter = painterResource(
                        id = if (isDarkMode) R.drawable.logo_conta_calorias_sem_fundo_dark
                        else R.drawable.logo_conta_calorias_sem_fundo_light
                    ),
                    contentDescription = "Calorie Log Logo",
                    modifier = Modifier
                        .size(100.dp) // Ajuste o tamanho conforme necessário
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally) // Centraliza o logo
                )
                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { toggleTheme() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isDarkMode) "Dark Mode" else "Light Mode",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = Icons.Filled.NightsStay,
                        contentDescription = if (isDarkMode) "Dark Mode" else "Light Mode",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showMacroSummaryDialog = true
                            scope.launch { drawerState.close() }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Full Macro View",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = Icons.Filled.Fastfood,
                        contentDescription = "Full Macro View",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                // Menu para Informações do Utilizador
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showInitialInfoDialog = true
                            showBMIDialog = false
                            scope.launch { drawerState.close() }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "User Info",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "User Info",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = topSpacing / 2)
                        .align(Alignment.Start)
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        },
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Image(
                        painter = painterResource(
                            id = if (isDarkMode) R.drawable.logo_conta_calorias_sem_fundo_dark
                            else R.drawable.logo_conta_calorias_sem_fundo_light
                        ),
                        contentDescription = "Calorie Log Logo",
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.Center)
                            .padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(lineSpacing / 32))

                OutlinedTextField(
                    value = calorieInput,
                    onValueChange = { calorieInput = it },
                    label = {
                        Text(
                            "Calorie intake for today",
                            style = LocalTextStyle.current.copy(lineHeight = 24.sp)
                        )
                    },
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

                Spacer(modifier = Modifier.height(lineSpacing / 4))

                Text(
                    text = "Your Meals Today!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 32.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(lineSpacing / 4)) // Espaçamento depois do título "Your Meals Today!"

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { showMealDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Add a Meal", lineHeight = 20.sp)
                    }

                    Button(
                        onClick = {
                            showRemoveMealDialog = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Remove a Meal", lineHeight = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.height(lineSpacing / 2))

                if (mealList.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 450.dp)
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(mealList) { meal ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            selectedMealToEdit = meal
                                            editedMealName = meal.name
                                            editedMealCalories = meal.calories.toString()
                                            editedMealProtein = meal.protein.toString()
                                            editedMealCarbs = meal.carbs.toString()
                                            editedMealFats = meal.fats.toString()
                                            editedMealSalt = meal.salt.toString()
                                            editedMealFiber = meal.fiber.toString()
                                            editedMealPolyols = meal.polyols.toString()
                                            editedMealStarch = meal.starch.toString()
                                            showEditMealDialog = true
                                        }
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = meal.name,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        lineHeight = 24.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${meal.calories} Kcal",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        lineHeight = 22.sp
                                    )
                                    var macrosText = ""
                                    if (meal.protein > 0) macrosText += "P: ${meal.protein}g "
                                    if (meal.carbs > 0) macrosText += "C: ${meal.carbs}g "
                                    if (meal.fats > 0) macrosText += "G: ${meal.fats}g "
                                    if (meal.salt > 0.0) macrosText += "Salt: ${String.format("%.1f", meal.salt)}g "
                                    if (meal.fiber > 0) macrosText += "Fiber: ${meal.fiber}g "
                                    if (meal.polyols > 0) macrosText += "Polyols: ${meal.polyols}g "
                                    if (meal.starch > 0) macrosText += "Starch: ${meal.starch}g"

                                    if (macrosText.isNotBlank()) {
                                        Text(
                                            text = macrosText.trim(),
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(lineSpacing / 2))

                Text(
                    text = if (remainingCalories < 0) {
                        val exceededCalories = totalMealCalories - dailyLimit
                        "Calories exceeded by: $exceededCalories Kcal"
                    } else {
                        "Calories left to consume: ${remainingCalories.coerceAtLeast(0)} Kcal"
                    },
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 36.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Barra de progresso posicionada aqui, por baixo do texto "Calories left to consume..."
                Spacer(modifier = Modifier.height(8.dp)) // Espaçamento antes da barra de progresso

                val progress = if (dailyLimit > 0) {
                    (totalMealCalories.toFloat() / dailyLimit.toFloat()).coerceIn(0f, 1f)
                } else {
                    0f // Se o limite diário for 0, o progresso é 0
                }

                val progressBarColor = if (dailyLimit == 0) {
                    if (totalMealCalories > 0) FadedRed else Color.LightGray // Se não há limite e já comeu, vermelho; senão, cinza claro
                } else if (totalMealCalories <= dailyLimit) {
                    // Usando a função manual de interpolação
                    manualColorLerp(Color.White, FadedGreen, progress)
                } else {
                    FadedRed // Se excedeu o limite, fica vermelho
                }

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp)), // Cantos arredondados para a barra
                    color = progressBarColor,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f) // Cor de fundo da barra
                )

                Spacer(modifier = Modifier.height(lineSpacing / 4)) // Espaçamento depois da barra de progresso
            }
        }
    }


    // Exibe o pop-up de informações iniciais se showInitialInfoDialog for verdadeiro
    if (showInitialInfoDialog) {
        InitialInfoDialog( // ERRO: InitialInfoDialog não será reconhecido sem importação
            onDismiss = { showInitialInfoDialog = false },
            onInfoSubmitted = { info ->
                userInfo = info

                // Lógica de cálculo do IMC
                val weight = userInfo.weight.toDoubleOrNull()
                val heightCm = userInfo.height.toDoubleOrNull()
                val calculatedBmi = if (weight != null && heightCm != null && heightCm > 0) {
                    val heightMeters = heightCm / 100.0
                    weight / (heightMeters * heightMeters)
                } else {
                    null
                }

                showInitialInfoDialog = false
                if (calculatedBmi != null) {
                    val weightDouble = userInfo.weight.toDoubleOrNull() ?: 0.0
                    val heightDouble = userInfo.height.toDoubleOrNull() ?: 0.0
                    val dobParts = userInfo.dob.split("/")
                    val age = if (dobParts.size == 3) {
                        val birthYear = dobParts[2].toIntOrNull()
                        if (birthYear != null) LocalDate.now().year - birthYear else 0
                    } else 0

                    if (userWeightGoal.isNotBlank() && weightDouble > 0 && heightDouble > 0 && age > 0) {
                        val bmr = calculateBMR(weightDouble, heightDouble, age)
                        val tdee = calculateTDEE(bmr, userInfo.activityLevel)
                        recommendedCalories = getRecommendedCalories(tdee, userWeightGoal)
                        calorieInput = recommendedCalories.toString()
                    } else {
                        recommendedCalories = null
                    }
                    showBMIDialog = true // ERRO: BMIDialog não será reconhecido sem importação
                }
            },
            initialUserInfo = userInfo
        )
    }

    // Exibe o pop-up de IMC se showBMIDialog for verdadeiro
    if (showBMIDialog) {
        val weight = userInfo.weight.toDoubleOrNull()
        val heightCm = userInfo.height.toDoubleOrNull()
        val currentBmi = if (weight != null && heightCm != null && heightCm > 0) {
            val heightMeters = heightCm / 100.0
            weight / (heightMeters * heightMeters)
        } else {
            null
        }

        BMIDialog( // ERRO: BMIDialog não será reconhecido sem importação
            userInfo = userInfo,
            bmi = currentBmi,
            recommendedCalories = recommendedCalories,
            selectedGoal = userWeightGoal,
            onDismiss = { showBMIDialog = false },
            onGoalSelected = { goal ->
                userWeightGoal = goal

                val weightDouble = userInfo.weight.toDoubleOrNull() ?: 0.0
                val heightDouble = userInfo.height.toDoubleOrNull() ?: 0.0
                val dobParts = userInfo.dob.split("/")
                val age = if (dobParts.size == 3) {
                    val birthYear = dobParts[2].toIntOrNull()
                    if (birthYear != null) LocalDate.now().year - birthYear else 0
                } else 0

                if (weightDouble > 0 && heightDouble > 0 && age > 0) {
                    val bmr = calculateBMR(weightDouble, heightDouble, age)
                    val tdee = calculateTDEE(bmr, userInfo.activityLevel)
                    recommendedCalories = getRecommendedCalories(tdee, goal)
                    calorieInput = recommendedCalories.toString()
                } else {
                    recommendedCalories = null
                }
            }
        )
    }

    if (showMealDialog) {
        AlertDialog(
            onDismissRequest = {
                showMealDialog = false
                mealName = ""
                mealCalories = ""
                mealProtein = ""
                mealCarbs = ""
                mealFats = ""
                mealSalt = ""
                mealFiber = ""
                mealPolyols = ""
                mealStarch = ""
            },
            confirmButton = {
                val isConfirmEnabled = mealName.isNotBlank() && (mealCalories.toIntOrNull() ?: 0) > 0
                TextButton(
                    onClick = {
                        val name = mealName.trim()
                        val calories = mealCalories.toIntOrNull() ?: 0
                        val protein = mealProtein.toIntOrNull() ?: 0
                        val carbs = mealCarbs.toIntOrNull() ?: 0
                        val fats = mealFats.toIntOrNull() ?: 0
                        val salt = mealSalt.toDoubleOrNull() ?: 0.0
                        val fiber = mealFiber.toIntOrNull() ?: 0
                        val polyols = mealPolyols.toIntOrNull() ?: 0
                        val starch = mealStarch.toIntOrNull() ?: 0

                        if (name.isNotBlank() && calories > 0) {
                            mealList.add(Meal(name, calories, protein, carbs, fats, salt, fiber, polyols, starch)) // ERRO: Meal não será reconhecido
                        }
                        showMealDialog = false
                        mealName = ""
                        mealCalories = ""
                        mealProtein = ""
                        mealCarbs = ""
                        mealFats = ""
                        mealSalt = ""
                        mealFiber = ""
                        mealPolyols = ""
                        mealStarch = ""
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = FadedGreen),
                    enabled = isConfirmEnabled
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showMealDialog = false
                        mealName = ""
                        mealCalories = ""
                        mealProtein = ""
                        mealCarbs = ""
                        mealFats = ""
                        mealSalt = ""
                        mealFiber = ""
                        mealPolyols = ""
                        mealStarch = ""
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = FadedRed)
                ) {
                    Text("Cancel")
                }
            },
            title = {
                Text("What was your meal?", color = MaterialTheme.colorScheme.onSurface)
            },
            text = {
                Column {
                    MacroInputField(value = mealName, onValueChange = { mealName = it }, label = "Meal name", keyboardType = KeyboardType.Text) // ERRO: MacroInputField não será reconhecido
                    MacroInputField(value = mealCalories, onValueChange = { mealCalories = it }, label = "calories (Kcal)")
                    MacroInputField(value = mealProtein, onValueChange = { mealProtein = it }, label = "Protein (g)")
                    MacroInputField(value = mealCarbs, onValueChange = { mealCarbs = it }, label = "Carbohydrates (g)")
                    MacroInputField(value = mealFats, onValueChange = { mealFats = it }, label = "Fats (g)")
                    MacroInputField(value = mealSalt, onValueChange = { mealSalt = it }, label = "Salt (g)", isDouble = true)
                    MacroInputField(value = mealFiber, onValueChange = { mealFiber = it }, label = "Fiber (g)")
                    MacroInputField(value = mealPolyols, onValueChange = { mealPolyols = it }, label = "Polyols (g)")
                    MacroInputField(value = mealStarch, onValueChange = { mealStarch = it }, label = "Starch (g)")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    if (showRemoveMealDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveMealDialog = false },
            title = { Text("Select meal to remove", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                if (mealList.isEmpty()) {
                    Text("No meals to remove.", color = MaterialTheme.colorScheme.onSurface)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        mealList.forEach { meal ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            mealList.remove(meal)
                                            showRemoveMealDialog = false
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${meal.name}: ${meal.calories} Kcal",
                                    fontSize = 18.sp,
                                    lineHeight = 24.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showRemoveMealDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = FadedRed)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    if (showEditMealDialog) {
        AlertDialog(
            onDismissRequest = {
                showEditMealDialog = false
                selectedMealToEdit = null
                editedMealName = ""
                editedMealCalories = ""
                editedMealProtein = ""
                editedMealCarbs = ""
                editedMealFats = ""
                editedMealSalt = ""
                editedMealFiber = ""
                editedMealPolyols = ""
                editedMealStarch = ""
            },
            title = { Text("Edit Meal", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column {
                    MacroInputField(value = editedMealName, onValueChange = { editedMealName = it }, label = "Meal name", keyboardType = KeyboardType.Text) // ERRO: MacroInputField não será reconhecido
                    MacroInputField(value = editedMealCalories, onValueChange = { editedMealCalories = it }, label = "Calories")
                    MacroInputField(value = editedMealProtein, onValueChange = { editedMealProtein = it }, label = "Protein (g)")
                    MacroInputField(value = editedMealCarbs, onValueChange = { editedMealCarbs = it }, label = "Carbohydrates (g)")
                    MacroInputField(value = editedMealFats, onValueChange = { editedMealFats = it }, label = "Fats (g)")
                    MacroInputField(value = editedMealSalt, onValueChange = { editedMealSalt = it }, label = "Salt (g)", isDouble = true)
                    MacroInputField(value = editedMealFiber, onValueChange = { editedMealFiber = it }, label = "Fiber (g)")
                    MacroInputField(value = editedMealPolyols, onValueChange = { editedMealPolyols = it }, label = "Polyols (g)")
                    MacroInputField(value = editedMealStarch, onValueChange = { editedMealStarch = it }, label = "Starch (g)")
                }
            },
            confirmButton = {
                val isSaveEnabled = editedMealName.isNotBlank() && (editedMealCalories.toIntOrNull() ?: 0) > 0
                if (selectedMealToEdit != null) {
                    TextButton(
                        onClick = {
                            val index = mealList.indexOf(selectedMealToEdit)
                            if (index != -1) {
                                val newName = editedMealName.trim()
                                val newCalories = editedMealCalories.toIntOrNull() ?: 0
                                val newProtein = editedMealProtein.toIntOrNull() ?: 0
                                val newCarbs = editedMealCarbs.toIntOrNull() ?: 0
                                val newFats = editedMealFats.toIntOrNull() ?: 0
                                val newSalt = editedMealSalt.toDoubleOrNull() ?: 0.0
                                val newFiber = mealList[index].fiber // Mantém os valores existentes se não forem editados
                                val newPolyols = mealList[index].polyols
                                val newStarch = mealList[index].starch

                                if (newName.isNotBlank() && newCalories > 0) {
                                    mealList[index] = Meal(newName, newCalories, newProtein, newCarbs, newFats, newSalt, newFiber, newPolyols, newStarch) // ERRO: Meal não será reconhecido
                                }
                            }
                            showEditMealDialog = false
                            selectedMealToEdit = null
                            editedMealName = ""
                            editedMealCalories = ""
                            editedMealProtein = ""
                            editedMealCarbs = ""
                            editedMealFats = ""
                            editedMealSalt = ""
                            editedMealFiber = ""
                            editedMealPolyols = ""
                            editedMealStarch = ""
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = FadedGreen),
                        enabled = isSaveEnabled
                    ) {
                        Text("Save")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditMealDialog = false
                    selectedMealToEdit = null
                    editedMealName = ""
                    editedMealCalories = ""
                    editedMealProtein = ""
                    editedMealCarbs = ""
                    editedMealFats = ""
                    editedMealSalt = ""
                    editedMealFiber = ""
                    editedMealPolyols = ""
                    editedMealStarch = ""
                },
                    colors = ButtonDefaults.textButtonColors(contentColor = FadedRed)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    if (showMacroSummaryDialog) {
        MacroSummaryDialog(mealList = mealList, onDismiss = { showMacroSummaryDialog = false }) // ERRO: MacroSummaryDialog não será reconhecido
    }
}
