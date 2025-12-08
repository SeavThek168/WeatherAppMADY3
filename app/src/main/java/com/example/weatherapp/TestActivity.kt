package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TestScreen()
            }
        }
    }
}

@Composable
fun TestScreen() {
    var result by remember { mutableStateOf("Click button to test") }
    val scope = rememberCoroutineScope()
    
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("API Connection Test", style = MaterialTheme.typography.headlineMedium)
            
            Button(
                onClick = {
                    scope.launch {
                        result = testApiConnection()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Test API Connection")
            }
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = result,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

suspend fun testApiConnection(): String {
    return withContext(Dispatchers.IO) {
        try {
            val apiKey = "63030200ba49f825a3bd4ab30b8aad49"
            val testUrl = "https://api.openweathermap.org/data/2.5/weather?q=London&appid=$apiKey&units=metric"
            
            Log.d("API_TEST", "Testing URL: $testUrl")
            
            val connection = URL(testUrl).openConnection()
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            val response = connection.getInputStream().bufferedReader().use { it.readText() }
            
            "✅ SUCCESS!\n\nAPI is working!\n\nResponse preview:\n${response.take(200)}..."
            
        } catch (e: Exception) {
            "❌ ERROR:\n\n${e.javaClass.simpleName}\n\n${e.message}\n\nCheck:\n1. Internet connection\n2. Firewall settings\n3. Emulator network"
        }
    }
}
