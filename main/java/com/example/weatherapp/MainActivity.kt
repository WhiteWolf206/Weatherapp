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
import androidx.compose.runtime.collectAsState
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons

class MainActivity : ComponentActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    var cityInput by remember { mutableStateOf("") }
    val weatherState by viewModel.weatherState.collectAsState()

    LaunchedEffect(Unit) {
        if (weatherState is WeatherUiState.Initial) {
            viewModel.fetchWeather(cityInput)
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .fillMaxSize()
        ) {
            when (val state = weatherState) {
                is WeatherUiState.Success -> {
                    WeatherDetails(
                        weatherData = state.data,
                        onBackClick = { viewModel.resetState() }
                    )
                }
                else -> {
                    SearchContent(
                        cityInput = cityInput,
                        onCityInputChange = { cityInput = it },
                        onSearchClick = { viewModel.fetchWeather(cityInput.trim()) },
                        isLoading = state is WeatherUiState.Loading
                    )

                    if (state is WeatherUiState.Error) {
                        //don't know later implementation maybe smth funny
                    }
                }
            }
        }
    }
}

@Composable
fun SearchContent(
    cityInput: String,
    onCityInputChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Batata",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = cityInput,
            onValueChange = onCityInputChange,
            label = { Text("Enter City Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSearchClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Get Weather")
        }

        if (isLoading) {
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator()
            Text("Loading weather...", modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun WeatherDetails(weatherData: WeatherResponse, onBackClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back to search"
            )
        }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                text = "(${it.description})",
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
}