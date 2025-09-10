package com.example.weatherapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.WeatherAppTheme

class MainActivity : ComponentActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherScreen(weatherViewModel)
                }
            }
        }
    }
}

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    var cityInput by remember { mutableStateOf("London") }
    val weatherState = viewModel.weatherState.value

    LaunchedEffect(Unit) {
        if (weatherState is WeatherUiState.Initial) {
            viewModel.fetchWeather(cityInput)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Batata",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = cityInput,
            onValueChange = { cityInput = it },
            label = { Text("Enter City Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.fetchWeather(cityInput.trim()) },
            modifier = Modifier.fillMaxWidth(),
            enabled = weatherState !is WeatherUiState.Loading
        ) {
            Text("Get Weather")
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (weatherState) {
            is WeatherUiState.Initial -> {
                Text("Enter a city and press 'Get Weather'.")
            }
            is WeatherUiState.Loading -> {
                CircularProgressIndicator()
                Text("Loading weather...", modifier = Modifier.padding(top = 8.dp))
            }
            is WeatherUiState.Success -> {
                WeatherDetails(weatherData = weatherState.data)
            }
            is WeatherUiState.Error -> {
                Text(
                    text = "Error: ${weatherState.message}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun WeatherDetails(weatherData: WeatherResponse) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = weatherData.cityName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        weatherData.weather.firstOrNull()?.let {
            Text(
                text = it.main,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "(${it.description})", // e.g., "broken clouds"
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${String.format("%.1f", weatherData.main.temp)}°C",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Feels like: ${String.format("%.1f", weatherData.main.feelsLike)}°C",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Humidity: ${weatherData.main.humidity}%",
            fontSize = 16.sp
        )
        Text(
            text = "Pressure: ${weatherData.main.pressure} hPa",
            fontSize = 16.sp
        )
    }
}