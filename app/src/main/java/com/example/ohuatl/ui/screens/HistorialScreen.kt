package com.example.ohuatl.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ohuatl.data.ApiClient
import com.example.ohuatl.data.HistoryRow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class MovementItem(val date: String, val co2: String, val tokens: String)

@Composable
fun HistorialScreen(navController: NavController) {
    val items = remember { mutableStateListOf<MovementItem>() }

    LaunchedEffect(Unit) {
        try {
            val rows = withContext(Dispatchers.IO) { ApiClient.service.getHistory() }
            items.clear()
            items.addAll(rows.map { MovementItem(it.day, String.format("%.0f kg", it.co2Kg), String.format("%.2f Ohua", it.tokens)) })
        } catch (_: Throwable) {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Historial", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            Text("Dia", modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.primary)
            Text("Co2", modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.primary)
            Text("Tokens", modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(items) { it ->
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 8.dp)) {
                    Text(it.date, modifier = Modifier.weight(1f))
                    Text(it.co2, modifier = Modifier.weight(1f))
                    Text(it.tokens, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
