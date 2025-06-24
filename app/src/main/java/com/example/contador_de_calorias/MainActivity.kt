package com.example.contador_de_calorias

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.rememberDrawerState
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import kotlin.math.roundToInt

// Importações adicionais necessárias
import androidx.compose.material3.Divider
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch
import androidx.compose.material3.Surface
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import androidx.compose.material.icons.filled.Book

// Importar composables e funções de utilidade de Ui_Elements.kt
import com.example.contador_de_calorias.AddRecipeDialog
import com.example.contador_de_calorias.BMIDialog
import com.example.contador_de_calorias.CustomMealInputDialog
import com.example.contador_de_calorias.InitialInfoDialog
import com.example.contador_de_calorias.MacroInputField
import com.example.contador_de_calorias.MacroSummaryDialog
import com.example.contador_de_calorias.QuantityInputDialog
import com.example.contador_de_calorias.calculateBMR
import com.example.contador_de_calorias.calculateTDEE
import com.example.contador_de_calorias.getRecommendedCalories


 // Configura o tema da aplicação (modo claro/escuro) e o `CalorieHomeScreen`.

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Estado para controlar se o modo escuro está ativado ou desativado
            var isDarkMode by remember { mutableStateOf(true) }

            // Definição das cores para o tema claro
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

            // Definição das cores para o tema escuro
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

            // Aplica o tema (claro ou escuro) com base no estado `isDarkMode`
            MaterialTheme(
                colorScheme = if (isDarkMode) DarkColors else LightColors
            ) {
                // `Surface` é um contentor que aplica as cores do tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    // Chama o composable principal da aplicação, passando o estado do tema e a função para alternar o tema
                    CalorieHomeScreen(
                        isDarkMode = isDarkMode,
                        toggleTheme = { isDarkMode = !isDarkMode },
                        context = context
                    )
                }
            }
        }
    }
}

/**
 * CalorieHomeScreen é o composable principal que exibe a interface do utilizador da aplicação.
 * Gerencia os estados da UI, interações do utilizador e a lógica de apresentação.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalorieHomeScreen(isDarkMode: Boolean, toggleTheme: () -> Unit, context: Context) {
    // Estados da UI e dados da aplicação
    var calorieInput by remember { mutableStateOf("") }
    var showMealDialog by remember { mutableStateOf(false) }
    var showRemoveMealDialog by remember { mutableStateOf(false) }
    var showEditMealDialog by remember { mutableStateOf(false) }
    var showMacroSummaryDialog by remember { mutableStateOf(false) }
    var showBMIDialog by remember { mutableStateOf(false) }
    var showAddRecipeDialog by remember { mutableStateOf(false) }
    var showQuantityDialog by remember { mutableStateOf(false) }
    var selectedRecipeForQuantity by remember { mutableStateOf<Recipe?>(null) }
    var showCustomMealInput by remember { mutableStateOf(false) }

    // Estado para controlar a data selecionada, iniciando com a data atual
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // Informações do utilizador e metas de peso, carregadas da persistência
    var userInfo by remember { mutableStateOf(loadUserInfo(context)) }
    var userWeightGoal by remember { mutableStateOf(loadUserWeightGoal(context)) }
    var recommendedCalories by remember { mutableStateOf<Int?>(loadRecommendedCalories(context)) }

    // Lista de refeições para a data selecionada, observando mudanças na data
    val mealList = remember(selectedDate) {
        mutableStateListOf<Meal>().apply { addAll(loadMealsForDate(context, selectedDate)) }
    }

    // Lista de receitas do utilizador
    val recipesList = remember { mutableStateListOf<Recipe>().apply { addAll(loadRecipes(context)) } }

    // Estado para mostrar o diálogo de informações iniciais se o perfil não estiver completo
    var showInitialInfoDialog by remember { mutableStateOf(userInfo.dob.isBlank()) }

    // Estados para inputs de refeição manual ou edição
    var mealName by remember { mutableStateOf("") }
    var mealCalories by remember { mutableStateOf("") }
    var mealProtein by remember { mutableStateOf("") }
    var mealCarbs by remember { mutableStateOf("") }
    var mealFats by remember { mutableStateOf("") }
    var mealSalt by remember { mutableStateOf("") }
    var mealFiber by remember { mutableStateOf("") }
    var mealPolyols by remember { mutableStateOf("") }
    var mealStarch by remember { mutableStateOf("") }

    // Estados para edição de refeição
    var selectedMealToEdit by remember { mutableStateOf<Meal?>(null) }
    var editedMealName by remember { mutableStateOf("") }
    var editedMealCalories by remember { mutableStateOf("") }
    var editedMealProtein by remember { mutableStateOf("") }
    var editedMealCarbs by remember { mutableStateOf("") }
    var editedMealFats by remember { mutableStateOf("") }
    var editedMealSalt by remember { mutableStateOf("") }
    var editedMealFiber by remember { mutableStateOf("") }
    var editedMealPolyols = remember { mutableStateOf("") }
    var editedMealStarch by remember { mutableStateOf("") }

    // Calorias totais consumidas, calculadas dinamicamente
    val totalMealCalories by remember(mealList) {
        derivedStateOf { mealList.sumOf { it.calories } }
    }

    // Efeito para atualizar o input de calorias quando as calorias recomendadas mudam
    LaunchedEffect(recommendedCalories) {
        recommendedCalories?.let {
            calorieInput = it.toString()
        }
    }

    // Cálculos de calorias restantes e limite diário
    val dailyLimit = calorieInput.toIntOrNull() ?: 0
    val remainingCalories = dailyLimit - totalMealCalories

    // Definições de layout e dimensões da tela
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val topSpacing = screenHeight * 0.075f
    val lineSpacing = screenHeight * 0.05f

    // Estado da gaveta de navegação (menu lateral)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope() // Coroutine scope para operações assíncronas

    // Definição de cores personalizadas
    val FadedGreen = Color(0xFF6C9E6C)
    val FadedRed = Color(0xFFB36B6B)
    val FadedBlue = Color(0xFF6A8EAE)

    // Configuração do DatePickerDialog para seleção de datas
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
        },
        selectedDate.year,
        selectedDate.monthValue - 1,
        selectedDate.dayOfMonth
    )

    // Função para interpolar cores manualmente para a barra de progresso
    fun manualColorLerp(start: Color, end: Color, fraction: Float): Color {
        val inverseFraction = 1 - fraction
        val red = start.red * inverseFraction + end.red * fraction
        val green = start.green * inverseFraction + end.green * fraction
        val blue = start.blue * inverseFraction + end.blue * fraction
        val alpha = start.alpha * inverseFraction + end.alpha * fraction
        return Color(red, green, blue, alpha)
    }

    // Modal Drawer para o menu lateral de navegação
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(screenWidth * 0.7f)) {
                // Logo da aplicação no menu lateral
                Image(
                    painter = painterResource(
                        id = if (isDarkMode) R.drawable.logo_conta_calorias_sem_fundo_dark
                        else R.drawable.logo_conta_calorias_sem_fundo_light
                    ),
                    contentDescription = "Calorie Log Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Divider()

                // Opção para alternar o tema (modo claro/escuro)
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
                        contentDescription = "Dark Mode",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Opção de calendário para selecionar a data das refeições
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            datePickerDialog.show()
                            scope.launch { drawerState.close() }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Calendar",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Calendar",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Opção para ver o resumo completo de macros
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

                // Opção para gerir as informações do utilizador
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

                // Opção para gerir as receitas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showAddRecipeDialog = true
                            scope.launch { drawerState.close() }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Recipes",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp
                    )
                    Icon(
                        imageVector = Icons.Filled.Book,
                        contentDescription = "Recipes",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        gesturesEnabled = drawerState.isOpen // Ativa ou desativa gestos para abrir a gaveta
    ) {
        // Conteúdo principal da tela
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cabeçalho com o botão de menu e o logo
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

                // Indicador de data atual
                Text(
                    text = if (selectedDate.isEqual(LocalDate.now())) "Today"
                    else selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(lineSpacing / 32))

                // Campo de input para o limite de calorias diário
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

                // Título dinâmico para a lista de refeições, baseado na data selecionada
                val mealsHeaderText = when (selectedDate) {
                    LocalDate.now() -> "Your Meals Today!"
                    LocalDate.now().minusDays(1) -> "Your Meals Yesterday!"
                    else -> "You had this on ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd"))}"
                }

                Text(
                    text = mealsHeaderText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 32.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(lineSpacing / 4))

                // Botões para adicionar e remover refeições
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

                // Lista de refeições diárias
                if (mealList.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(mealList) { meal ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp)) // Aplica bordas arredondadas ao item da lista
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
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
                                            editedMealPolyols.value = meal.polyols.toString()
                                            editedMealStarch = meal.starch.toString()
                                            showEditMealDialog = true
                                        }
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(lineSpacing / 2))

                // Exibição de calorias restantes ou excedidas
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

                Spacer(modifier = Modifier.height(8.dp))

                // Barra de progresso para o consumo de calorias
                val progress = if (dailyLimit > 0) {
                    (totalMealCalories.toFloat() / dailyLimit.toFloat()).coerceIn(0f, 1f)
                } else {
                    0f
                }

                // Cor da barra de progresso baseada nas calorias
                val progressBarColor = if (dailyLimit == 0) {
                    if (totalMealCalories > 0) FadedRed else Color.LightGray
                } else if (totalMealCalories <= dailyLimit) {
                    manualColorLerp(Color.White, FadedGreen, progress)
                } else {
                    FadedRed
                }

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = progressBarColor,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(lineSpacing / 4))
            }
        }
    }

    // Gerenciamento de exibição de diálogos
    if (showInitialInfoDialog) {
        InitialInfoDialog(
            onDismiss = { showInitialInfoDialog = false },
            onInfoSubmitted = { info ->
                userInfo = info
                saveUserInfo(context, userInfo)

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

                    if (userWeightGoal.isNotBlank() && weightDouble > 0 && heightDouble > 0 && age > 0 && userInfo.gender.isNotBlank()) {
                        // Usar funções movidas para Ui_Elements.kt
                        val bmr = calculateBMR(weightDouble, heightDouble, age, userInfo.gender)
                        val tdee = calculateTDEE(bmr, userInfo.activityLevel)
                        recommendedCalories = getRecommendedCalories(tdee, userWeightGoal)
                        calorieInput = recommendedCalories.toString()
                        saveRecommendedCalories(context, recommendedCalories)
                        saveUserWeightGoal(context, userWeightGoal)
                    } else {
                        recommendedCalories = null
                        saveRecommendedCalories(context, null)
                    }
                    showBMIDialog = true
                }
            },
            initialUserInfo = userInfo
        )
    }

    if (showBMIDialog) {
        val weight = userInfo.weight.toDoubleOrNull()
        val heightCm = userInfo.height.toDoubleOrNull()
        val currentBmi = if (weight != null && heightCm != null && heightCm > 0) {
            val heightMeters = heightCm / 100.0
            weight / (heightMeters * heightMeters)
        } else {
            null
        }

        BMIDialog(
            userInfo = userInfo,
            bmi = currentBmi,
            recommendedCalories = recommendedCalories,
            selectedGoal = userWeightGoal,
            onDismiss = { showBMIDialog = false },
            onGoalSelected = { goal ->
                userWeightGoal = goal
                saveUserWeightGoal(context, userWeightGoal)

                val weightDouble = userInfo.weight.toDoubleOrNull() ?: 0.0
                val heightDouble = userInfo.height.toDoubleOrNull() ?: 0.0
                val dobParts = userInfo.dob.split("/")
                val age = if (dobParts.size == 3) {
                    val birthYear = dobParts[2].toIntOrNull()
                    if (birthYear != null) LocalDate.now().year - birthYear else 0
                } else 0

                if (weightDouble > 0 && heightDouble > 0 && age > 0 && userInfo.gender.isNotBlank()) {
                    // Usar funções movidas para Ui_Elements.kt
                    val bmr = calculateBMR(weightDouble, heightDouble, age, userInfo.gender)
                    val tdee = calculateTDEE(bmr, goal)
                    recommendedCalories = getRecommendedCalories(tdee, goal)
                    calorieInput = recommendedCalories.toString()
                    saveRecommendedCalories(context, recommendedCalories)
                } else {
                    recommendedCalories = null
                    saveRecommendedCalories(context, null)
                }
            }
        )
    }

    if (showMealDialog) {
        AlertDialog(
            onDismissRequest = {
                showMealDialog = false
                selectedRecipeForQuantity = null
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
            title = {
                Text("Add a Meal", color = MaterialTheme.colorScheme.onSurface)
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    // Lista de receitas para seleção
                    if (recipesList.isNotEmpty()) {
                        Text(
                            "Select from your recipes:",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 150.dp)
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(recipesList) { recipe ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp)) // Aplica bordas arredondadas ao item da lista
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = {
                                                selectedRecipeForQuantity = recipe
                                                showQuantityDialog = true
                                            }
                                        )
                                        .padding(horizontal = 20.dp, vertical = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${recipe.name} (${recipe.calories} Kcal / 100g)",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Botão para adicionar refeição personalizada
                    Button(
                        onClick = {
                            showCustomMealInput = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FadedBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Custom Meal")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { showMealDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = FadedRed)
                ) {
                    Text("Cancel")
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
                // Mensagem se não houver refeições para remover
                if (mealList.isEmpty()) {
                    Text("No meals to remove.", color = MaterialTheme.colorScheme.onSurface)
                } else {
                    // Lista de refeições para remoção
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 350.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        mealList.forEach { meal ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp)) // Aplica bordas arredondadas ao item da lista
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {
                                            mealList.remove(meal)
                                            saveMealsForDate(context, selectedDate, mealList)
                                            showRemoveMealDialog = false
                                        }
                                    )
                                    .padding(start = 20.dp, top = 16.dp, bottom = 16.dp, end = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${meal.name}: ${meal.calories} Kcal",
                                    fontSize = 18.sp,
                                    lineHeight = 24.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
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

    // Diálogo para editar uma refeição existente
    if (showEditMealDialog) {
        AlertDialog(
            onDismissRequest = {
                showEditMealDialog = false
                selectedMealToEdit = null
            },
            title = { Text("Edit Meal", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    MacroInputField(value = editedMealName, onValueChange = { editedMealName = it }, label = "Meal name", keyboardType = KeyboardType.Text, isRequired = true)
                    MacroInputField(value = editedMealCalories, onValueChange = { editedMealCalories = it }, label = "Calories", isRequired = true)
                    MacroInputField(value = editedMealProtein, onValueChange = { editedMealProtein = it }, label = "Protein (g)")
                    MacroInputField(value = editedMealCarbs, onValueChange = { editedMealCarbs = it }, label = "Carbohydrates (g)")
                    MacroInputField(value = editedMealFats, onValueChange = { editedMealFats = it }, label = "Fats (g)")
                    MacroInputField(value = editedMealSalt, onValueChange = { editedMealSalt = it }, label = "Salt (g)", isDouble = true)
                    MacroInputField(value = editedMealFiber, onValueChange = { editedMealFiber = it }, label = "Fiber (g)")
                    MacroInputField(value = editedMealPolyols.value, onValueChange = { editedMealPolyols.value = it }, label = "Polyols (g)")
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
                                val newFiber = editedMealFiber.toIntOrNull() ?: 0
                                val newPolyols = editedMealPolyols.value.toIntOrNull() ?: 0
                                val newStarch = editedMealStarch.toIntOrNull() ?: 0

                                if (newName.isNotBlank() && newCalories > 0) {
                                    mealList[index] = Meal(newName, newCalories, newProtein, newCarbs, newFats, newSalt, newFiber, newPolyols, newStarch)
                                    saveMealsForDate(context, selectedDate, mealList)
                                }
                            }
                            showEditMealDialog = false
                            selectedMealToEdit = null
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
                },
                    colors = ButtonDefaults.textButtonColors(contentColor = FadedRed)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Diálogo para exibir o resumo de macros
    if (showMacroSummaryDialog) {
        MacroSummaryDialog(mealList = mealList, onDismiss = { showMacroSummaryDialog = false })
    }

    // Diálogo para adicionar novas receitas
    if (showAddRecipeDialog) {
        AddRecipeDialog(
            onDismiss = { showAddRecipeDialog = false },
            onRecipeAdded = { newRecipe ->
                recipesList.add(newRecipe)
                saveRecipes(context, recipesList) // Guarda a nova receita
            }
        )
    }

    // Diálogo para inserir a quantidade de uma receita selecionada
    if (showQuantityDialog && selectedRecipeForQuantity != null) {
        QuantityInputDialog(
            recipe = selectedRecipeForQuantity!!,
            onDismiss = { showQuantityDialog = false },
            onMealCalculated = { calculatedMeal ->
                mealList.add(calculatedMeal)
                saveMealsForDate(context, selectedDate, mealList)
                // Limpar campos após adicionar a refeição calculada
                mealName = ""
                mealCalories = ""
                mealProtein = ""
                mealCarbs = ""
                mealFats = ""
                mealSalt = ""
                mealFiber = ""
                mealPolyols = ""
                mealStarch = ""

                showQuantityDialog = false
                showMealDialog = false
            }
        )
    }

    // Diálogo para adicionar refeição personalizada
    if (showCustomMealInput) {
        CustomMealInputDialog(
            onDismiss = {
                showCustomMealInput = false
                // Opcional: Limpar campos quando o utilizador cancela a adição
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
            onMealAdded = { meal ->
                mealList.add(meal)
                saveMealsForDate(context, selectedDate, mealList)
                showCustomMealInput = false
                showMealDialog = false
                // Limpar campos após adição bem-sucedida
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
            mealName = mealName,
            mealCalories = mealCalories,
            mealProtein = mealProtein,
            mealCarbs = mealCarbs,
            mealFats = mealFats,
            mealSalt = mealSalt,
            mealFiber = mealFiber,
            mealPolyols = mealPolyols,
            mealStarch = mealStarch,
            onNameChange = { mealName = it },
            onCaloriesChange = { mealCalories = it },
            onProteinChange = { mealProtein = it },
            onCarbsChange = { mealCarbs = it },
            onFatsChange = { mealFats = it },
            onSaltChange = { mealSalt = it },
            onFiberChange = { mealFiber = it },
            onPolyolsChange = { mealPolyols = it },
            onStarchChange = { mealStarch = it }
        )
    }
}

// Constantes para as SharedPreferences
private const val PREFS_NAME = "calorie_app_prefs"
private const val USER_INFO_KEY = "user_info"
private const val USER_WEIGHT_GOAL_KEY = "user_weight_goal"
private const val RECOMMENDED_CALORIES_KEY = "recommended_calories"
private const val RECIPES_KEY = "recipes_key"


 // Salva as informações do utilizador nas SharedPreferences.

private fun saveUserInfo(context: Context, userInfo: UserInfo) {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()
    val json = Gson().toJson(userInfo)
    editor.putString(USER_INFO_KEY, json)
    editor.apply()
}


 // Carrega as informações do utilizador das SharedPreferences.

private fun loadUserInfo(context: Context): UserInfo {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = sharedPrefs.getString(USER_INFO_KEY, null)
    return if (json != null) {
        Gson().fromJson(json, UserInfo::class.java)
    } else {
        UserInfo()
    }
}


 // Salva a lista de refeições para uma data específica nas SharedPreferences.

private fun saveMealsForDate(context: Context, date: LocalDate, meals: List<Meal>) {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()
    val json = Gson().toJson(meals)
    val key = "MEALS_${date.toString()}" // Chave única para a data
    editor.putString(key, json)
    editor.apply()
}


 // Carrega a lista de refeições para uma data específica das SharedPreferences.

private fun loadMealsForDate(context: Context, date: LocalDate): List<Meal> {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val key = "MEALS_${date.toString()}" // Chave única para a data
    val json = sharedPrefs.getString(key, null)
    return if (json != null) {
        val type = object : TypeToken<List<Meal>>() {}.type
        Gson().fromJson(json, type)
    } else {
        emptyList()
    }
}


 // Salva a lista de receitas do utilizador nas SharedPreferences.

private fun saveRecipes(context: Context, recipes: List<Recipe>) {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()
    val json = Gson().toJson(recipes)
    editor.putString(RECIPES_KEY, json)
    editor.apply()
}


 // Carrega a lista de receitas do utilizador das SharedPreferences

private fun loadRecipes(context: Context): List<Recipe> {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = sharedPrefs.getString(RECIPES_KEY, null)
    return if (json != null) {
        val type = object : TypeToken<List<Recipe>>() {}.type
        Gson().fromJson(json, type)
    } else {
        emptyList()
    }
}


 // Salva a meta de peso do utilizador nas SharedPreferences.

private fun saveUserWeightGoal(context: Context, goal: String) {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()
    editor.putString(USER_WEIGHT_GOAL_KEY, goal)
    editor.apply()
}


 // Carrega a meta de peso do utilizador das SharedPreferences.

private fun loadUserWeightGoal(context: Context): String {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPrefs.getString(USER_WEIGHT_GOAL_KEY, "") ?: ""
}


 // Salva as calorias recomendadas nas SharedPreferences.

private fun saveRecommendedCalories(context: Context, calories: Int?) {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val editor = sharedPrefs.edit()
    if (calories != null) {
        editor.putInt(RECOMMENDED_CALORIES_KEY, calories)
    } else {
        editor.remove(RECOMMENDED_CALORIES_KEY)
    }
    editor.apply()
}


 // Carrega as calorias recomendadas das SharedPreferences.

private fun loadRecommendedCalories(context: Context): Int? {
    val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val calories = sharedPrefs.getInt(RECOMMENDED_CALORIES_KEY, -1)
    return if (calories != -1) calories else null
}
