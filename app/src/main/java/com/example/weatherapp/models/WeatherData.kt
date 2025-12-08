package com.example.weatherapp.models

data class WeatherData(
    val location: String = "San Francisco",
    val currentTemp: Int = 72,
    val condition: String = "Partly Cloudy",
    val highTemp: Int = 75,
    val lowTemp: Int = 65,
    val feelsLike: Int = 70,
    val humidity: Int = 65,
    val windSpeed: Int = 12,
    val airQuality: AirQuality = AirQuality(),
    val uvIndex: UVIndex = UVIndex(),
    val sunriseSunset: SunriseSunset = SunriseSunset(),
    val hourlyForecast: List<HourlyForecast> = emptyList(),
    val fiveDayForecast: List<DailyForecast> = emptyList()
)

data class AirQuality(
    val aqi: Int = 42,
    val quality: String = "Good",
    val pm25: Double = 12.5,
    val pm10: Double = 18.3
)

data class UVIndex(
    val index: Int = 6,
    val level: String = "Moderate",
    val peakTime: String = "12:00 PM"
)

data class SunriseSunset(
    val sunrise: String = "6:45 AM",
    val sunset: String = "7:30 PM",
    val dayLength: String = "12h 45m"
)

data class HourlyForecast(
    val time: String,
    val temp: Int,
    val condition: String,
    val icon: String = "☀️"
)

data class DailyForecast(
    val day: String,
    val date: String,
    val highTemp: Int,
    val lowTemp: Int,
    val condition: String,
    val icon: String = "⛅",
    val precipChance: Int = 0
)
