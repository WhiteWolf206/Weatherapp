package com.example.weatherapp

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

const val API_KEY = "API_KEY"

sealed class WeatherUiState {
    object Initial : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val data: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

class WeatherViewModel : ViewModel() {

    private val _weatherState = mutableStateOf<WeatherUiState>(WeatherUiState.Initial)
    val weatherState: State<WeatherUiState> = _weatherState

    fun fetchWeather(city: String) {
        if (city.isBlank()) {
            _weatherState.value = WeatherUiState.Error("City name cannot be empty.")
            return
        }

        _weatherState.value = WeatherUiState.Loading
        viewModelScope.launch {
            try {
                val response = RetInstance.api.getCurrentWeather(city, API_KEY)
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.cod == 200) {
                        _weatherState.value = WeatherUiState.Success(response.body()!!)
                    } else {
                        _weatherState.value = WeatherUiState.Error("City not found or API error: ${response.body()!!.cod}")
                    }
                } else {
                    _weatherState.value = WeatherUiState.Error("${response.message()} (Code: ${response.code()})")
                }
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error("Network error or parsing error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }

}
