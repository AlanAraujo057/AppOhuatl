package com.example.ohuatl.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ohuatl.data.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(navController: NavController) {
    val totalCo2Kg = remember { mutableStateOf(100.0) }
    val ohua = remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        try {
            val resp = withContext(Dispatchers.IO) { ApiClient.service.getCarbonBalance() }
            totalCo2Kg.value = resp.totalCo2Kg
            ohua.value = resp.mintableTokens
        } catch (_: Throwable) {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

            Text(text = "Ohuatl", style = MaterialTheme.typography.headlineSmall)
            Icon(Icons.Default.Person, contentDescription = null)
        }
        Spacer(Modifier.height(16.dp))
        Text(text = "Tus ganancias", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total de ganancias", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.height(8.dp))
                Text(String.format("$%.2f", ohua.value), style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onPrimary)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { /* TODO: invertir / vender */ }) { Text("Invertir") }
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("Movimientos CO2", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) { Column(Modifier.padding(16.dp)) { Text("Cosecha de caña de azúcar", color = MaterialTheme.colorScheme.onPrimary); Text("100 kg", color = MaterialTheme.colorScheme.onPrimary) } }
        Spacer(Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) { Column(Modifier.padding(16.dp)) { Text("CO2 Tokenizado", color = MaterialTheme.colorScheme.onPrimary); Text(String.format("%.0f kg", totalCo2Kg.value - ohua.value), color = MaterialTheme.colorScheme.onPrimary) } }
        Spacer(Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)) { Column(Modifier.padding(16.dp)) { Text("CO2 Compensado", color = MaterialTheme.colorScheme.onPrimary); Text("25 kg", color = MaterialTheme.colorScheme.onPrimary) } }
    }
}
