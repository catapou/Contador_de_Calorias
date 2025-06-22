package com.example.contador_de_calorias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.res.painterResource
import com.example.contador_de_calorias.R
import androidx.compose.foundation.Image
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange


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

    var showInitialInfoDialog by remember { mutableStateOf(true) }
    var userInfo by remember { mutableStateOf(UserInfo()) }

    var mealName by remember { mutableStateOf("") }
    var mealCalories by remember { mutableStateOf("") }
    var mealProtein by remember { mutableStateOf("") }
    var mealCarbs by remember { mutableStateOf("") }
    var mealFats by remember { mutableStateOf("") }
    var mealSalt by remember { mutableStateOf("") }
    var mealFiber by remember { mutableStateOf("") }
    var mealPolyols by remember { mutableStateOf("") }
    var mealStarch by remember { mutableStateOf("") }

    var selectedMealToEdit by remember { mutableStateOf<Meal?>(null) }
    var editedMealName by remember { mutableStateOf("") }
    var editedMealCalories by remember { mutableStateOf("") }
    var editedMealProtein by remember { mutableStateOf("") }
    var editedMealCarbs by remember { mutableStateOf("") }
    var editedMealFats by remember { mutableStateOf("") }
    var editedMealSalt by remember { mutableStateOf("") }
    var editedMealFiber by remember { mutableStateOf("") }
    var editedMealPolyols by remember { mutableStateOf("") }
    var editedMealStarch by remember { mutableStateOf("") }

    val mealList = remember { mutableStateListOf<Meal>() }

    val totalMealCalories = mealList.sumOf { it.calories }

    val dailyLimit = calorieInput.toIntOrNull() ?: 0
    val remainingCalories = dailyLimit - totalMealCalories

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val topSpacing = screenHeight * 0.075f
    val lineSpacing = screenHeight * 0.05f

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(screenWidth * 0.7f)) {
                Text("Menu", modifier = Modifier.padding(16.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
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
                        imageVector = if (isDarkMode) Icons.Filled.NightsStay else Icons.Filled.WbSunny,
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

                Spacer(modifier = Modifier.height(lineSpacing / 4))

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
                Spacer(modifier = Modifier.height(lineSpacing))
            }
        }
    }

    val FadedGreen = Color(0xFF6C9E6C)
    val FadedRed = Color(0xFFB36B6B)
    val FadedBlue = Color(0xFF6A8EAE)

    // Exibe o pop-up de informações iniciais se showInitialInfoDialog for verdadeiro
    if (showInitialInfoDialog) {
        InitialInfoDialog(
            onDismiss = { /* Não permite fechar sem preencher */ },
            onInfoSubmitted = { info ->
                userInfo = info
                showInitialInfoDialog = false
            },
            // Passa os estados e eventos de atualização para o InitialInfoDialog
            // dobInput e onDobInputChange são removidos daqui pois agora são geridos internamente no InitialInfoDialog
            // para simplificar a passagem de estado, já que o InitialInfoDialog é self-contained para sua lógica de campos
            initialUserInfo = userInfo // Passa o userInfo inicial para popular os campos
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
                            mealList.add(Meal(name, calories, protein, carbs, fats, salt, fiber, polyols, starch))
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
                    MacroInputField(value = mealName, onValueChange = { mealName = it }, label = "Meal name", keyboardType = KeyboardType.Text)
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
                    MacroInputField(value = editedMealName, onValueChange = { editedMealName = it }, label = "Meal name", keyboardType = KeyboardType.Text)
                    MacroInputField(value = editedMealCalories, onValueChange = { editedMealCalories = it }, label = "Calories")
                    MacroInputField(value = editedMealProtein, onValueChange = { editedMealProtein = it }, label = "Protein (g)")
                    MacroInputField(value = editedMealCarbs, onValueChange = { editedMealCarbs = it }, label = "Carbohydrates (g)")
                    MacroInputField(value = editedMealFats, onValueChange = { editedMealFats = it }, label = "Fats (g)")
                    MacroInputField(value = editedMealSalt, onValueChange = { mealSalt = it }, label = "Salt (g)", isDouble = true)
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
                                val newFiber = editedMealFiber.toIntOrNull() ?: 0
                                val newPolyols = editedMealPolyols.toIntOrNull() ?: 0
                                val newStarch = editedMealStarch.toIntOrNull() ?: 0

                                if (newName.isNotBlank() && newCalories > 0) {
                                    mealList[index] = Meal(newName, newCalories, newProtein, newCarbs, newFats, newSalt, newFiber, newPolyols, newStarch)
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

    if (showMacroSummaryDialog) {
        MacroSummaryDialog(mealList = mealList, onDismiss = { showMacroSummaryDialog = false })
    }
}
