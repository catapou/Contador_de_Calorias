package com.example.contador_de_calorias

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle // Importa TextStyle
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

@Composable
fun CalorieHomeScreen() {
    var calorieInput by remember { mutableStateOf("") }

    // Altura do ecrã para calcular percentagens dinâmicas
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val topSpacing = screenHeight * 0.05f     // 5% do topo
    val lineSpacing = screenHeight * 0.05f    // 5% entre blocos

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

        // Secção: Your Meals Today + Botões
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
                onClick = {  },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Add a Meal", lineHeight = 20.sp)
            }

            Button(
                onClick = {  },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Remove a Meal", lineHeight = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(lineSpacing))

        Text(
            text = "Calories left to consume: ${calorieInput.ifBlank { "0" }} Kcal",
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 36.sp
        )
    }
}