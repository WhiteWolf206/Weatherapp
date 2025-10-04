package com.example.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val API_KEY = "e29767706e74cff86d019f8319e9a0fe"

sealed class WeatherUiState {
    object Initial : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val data: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

class WeatherViewModel : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Initial)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    fun resetState() {
        _weatherState.value = WeatherUiState.Initial
    }


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