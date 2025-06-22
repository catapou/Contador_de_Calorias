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
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalorieHomeScreen()
                }
            }
        }
    }
}

data class Meal(val name: String, val calories: Int)

@Composable
fun CalorieHomeScreen() {
    var calorieInput by remember { mutableStateOf("") }
    var showMealDialog by remember { mutableStateOf(false) }
    var showRemoveMealDialog by remember { mutableStateOf(false) }
    var showEditMealDialog by remember { mutableStateOf(false) }
    var mealName by remember { mutableStateOf("") }
    var mealCalories by remember { mutableStateOf("") }
    var selectedMealToEdit by remember { mutableStateOf<Meal?>(null) }
    var editedMealName by remember { mutableStateOf("") }
    var editedMealCalories by remember { mutableStateOf("") }

    val mealList = remember { mutableStateListOf<Meal>() }

    val totalMealCalories = mealList.sumOf { it.calories }

    val dailyLimit = calorieInput.toIntOrNull() ?: 0
    val remainingCalories = dailyLimit - totalMealCalories

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val topSpacing = screenHeight * 0.075f
    val lineSpacing = screenHeight * 0.05f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(topSpacing))

        Text(
            text = "Daily Calorie Log",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(lineSpacing / 4))

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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(lineSpacing / 4))

        Text(
            text = "Your Meals Today!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(lineSpacing / 4))

        // Lista de refeições
        if (mealList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE0E0E0))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mealList) { meal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = meal.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 24.sp
                        )
                        Text(
                            text = "${meal.calories} Kcal",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(lineSpacing / 2)) // Spacer between meal list and buttons

        // Row com os botões "Add" e "Remove"
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { showMealDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                Text("Add a Meal", lineHeight = 20.sp)
            }

            Button(
                onClick = {
                    showRemoveMealDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Text("Remove a Meal", lineHeight = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp)) // Espaço entre o grupo de botões e o botão "Edit"

        // Botão "Edit a Meal" - Agora separado e abaixo dos outros dois
        Button(
            onClick = {
                showEditMealDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Text("Edit a Meal", lineHeight = 20.sp)
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
            lineHeight = 36.sp
        )
        Spacer(modifier = Modifier.height(lineSpacing))
    }

    // Diálogo de adicionar refeição
    if (showMealDialog) {
        AlertDialog(
            onDismissRequest = {
                showMealDialog = false
                mealName = ""
                mealCalories = ""
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = mealName.trim()
                        val calories = mealCalories.toIntOrNull() ?: 0
                        if (name.isNotBlank() && calories > 0) {
                            mealList.add(Meal(name, calories))
                        }
                        showMealDialog = false
                        mealName = ""
                        mealCalories = ""
                    }
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
                    }
                ) {
                    Text("Cancel")
                }
            },
            title = {
                Text("What was your meal?")
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = mealName,
                        onValueChange = { mealName = it },
                        placeholder = { Text("Meal name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = mealCalories,
                        onValueChange = { mealCalories = it },
                        label = { Text("and how many calories did it have?") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }

    // Dialogo para remover uma refeição específica
    if (showRemoveMealDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveMealDialog = false },
            title = { Text("Select meal to remove") },
            text = {
                if (mealList.isEmpty()) {
                    Text("No meals to remove.")
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
                                    .shadow(4.dp, RoundedCornerShape(4.dp))
                                    .background(Color.White, RoundedCornerShape(4.dp))
                                    .clickable {
                                        mealList.remove(meal)
                                        showRemoveMealDialog = false
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${meal.name}: ${meal.calories} Kcal",
                                    fontSize = 18.sp,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showRemoveMealDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Novo Diálogo para editar uma refeição específica
    if (showEditMealDialog) {
        AlertDialog(
            onDismissRequest = {
                showEditMealDialog = false
                selectedMealToEdit = null
                editedMealName = ""
                editedMealCalories = ""
            },
            title = { Text(if (selectedMealToEdit == null) "Select meal to edit" else "Edit Meal") },
            text = {
                if (mealList.isEmpty()) {
                    Text("No meals to edit.")
                } else if (selectedMealToEdit == null) {
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
                                    .shadow(4.dp, RoundedCornerShape(4.dp))
                                    .background(Color.White, RoundedCornerShape(4.dp))
                                    .clickable {
                                        selectedMealToEdit = meal
                                        editedMealName = meal.name
                                        editedMealCalories = meal.calories.toString()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${meal.name}: ${meal.calories} Kcal",
                                    fontSize = 18.sp,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                    }
                } else {
                    Column {
                        OutlinedTextField(
                            value = editedMealName,
                            onValueChange = { editedMealName = it },
                            label = { Text("Meal name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = editedMealCalories,
                            onValueChange = { editedMealCalories = it },
                            label = { Text("Calories") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (selectedMealToEdit != null) {
                    TextButton(
                        onClick = {
                            val index = mealList.indexOf(selectedMealToEdit)
                            if (index != -1) {
                                val newName = editedMealName.trim()
                                val newCalories = editedMealCalories.toIntOrNull() ?: 0
                                if (newName.isNotBlank() && newCalories > 0) {
                                    mealList[index] = Meal(newName, newCalories)
                                }
                            }
                            showEditMealDialog = false
                            selectedMealToEdit = null
                            editedMealName = ""
                            editedMealCalories = ""
                        }
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
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
