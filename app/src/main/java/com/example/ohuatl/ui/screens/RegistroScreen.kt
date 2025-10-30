package com.example.ohuatl.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ohuatl.data.ApiClient
import com.example.ohuatl.data.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun RegistroScreen(navController: NavController) {
    val nombre = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirm = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Crear cuenta", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = nombre.value, onValueChange = { nombre.value = it }, label = { Text("Nombre") }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = email.value, onValueChange = { email.value = it }, label = { Text("Correo") }, singleLine = true)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = password.value, onValueChange = { password.value = it }, label = { Text("Contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = confirm.value, onValueChange = { confirm.value = it }, label = { Text("Confirmar contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            val full = nombre.value
            val mail = email.value
            val pass = password.value
            scope.launch {
                try {
                    withContext(Dispatchers.IO) { ApiClient.service.register(RegisterRequest(mail, pass, full)) }
                } catch (_: Throwable) { }
                navController.navigate("login")
            }
        }) { Text("Registrarme") }
    }
}
