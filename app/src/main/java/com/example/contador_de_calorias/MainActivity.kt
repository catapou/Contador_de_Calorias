package com.example.contador_de_calorias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    var calorieInput by remember { mutableStateOf("") } // Total diário dado pelo utilizador
    var showMealDialog by remember { mutableStateOf(false) }
    var mealName by remember { mutableStateOf("") }
    var mealCalories by remember { mutableStateOf("") }

    val mealList = remember { mutableStateListOf<Meal>() }

    // Soma de todas as calorias das refeições
    val totalMealCalories = mealList.sumOf { it.calories }

    // Valor total convertido em Int de forma segura
    val dailyLimit = calorieInput.toIntOrNull() ?: 0
    val remainingCalories = dailyLimit - totalMealCalories

    // Altura do ecrã para espaçamentos dinâmicos
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val topSpacing = screenHeight * 0.05f
    val lineSpacing = screenHeight * 0.05f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(topSpacing))

        Text(
            text = "Calorie counter for a day",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(lineSpacing))

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

        Spacer(modifier = Modifier.height(lineSpacing))

        Text(
            text = "Your Meals Today!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(lineSpacing))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { showMealDialog = true },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Add a Meal", lineHeight = 20.sp)
            }

            Button(
                onClick = {
                    if (mealList.isNotEmpty()) mealList.removeLast()
                },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Remove a Meal", lineHeight = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(lineSpacing / 2))

        // Lista de refeições
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mealList) { meal ->
                Text(
                    text = "${meal.name}: ${meal.calories} Kcal",
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(lineSpacing / 2))

        Text(
            text = "Calories left to consume: ${remainingCalories.coerceAtLeast(0)} Kcal",
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 36.sp
        )
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
}
