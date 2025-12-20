package com.example.weatherapp.models

data class WeatherData(
    val location: String = "Phnom Penh",
    val country: String = "Cambodia",
    val currentTemp: Int = 32,
    val condition: String = "Partly Cloudy",
    val highTemp: Int = 34,
    val lowTemp: Int = 26,
    val feelsLike: Int = 36,
    val humidity: Int = 75,
    val windSpeed: Int = 12,
    val pressure: Int = 1011,
    val visibility: Double = 10.0,
    val airQuality: AirQuality = AirQuality(),
    val uvIndex: UVIndex = UVIndex(),
    val sunriseSunset: SunriseSunset = SunriseSunset(),
    val hourlyForecast: List<HourlyForecast> = emptyList(),
    val fiveDayForecast: List<DailyForecast> = emptyList()
)

data class AirQuality(
    val aqi: Int = 2,
    val quality: String = "Fair",
    val pm25: Double = 23.0,
    val pm10: Double = 29.0,
    val ozone: Double = 62.0,
    val no2: Double = 1.0
)

data class UVIndex(
    val index: Int = 2,
    val level: String = "Low",
    val peakTime: String = "12:00 PM"
)

data class SunriseSunset(
    val sunrise: String = "5:45 AM",
    val sunset: String = "5:50 PM",
    val dayLength: String = "11h 26m"
)

data class HourlyForecast(
    val time: String,
    val temp: Int,
    val condition: String,
    val icon: String = "☀️",
    val humidity: Int = 50,
    val windSpeed: Double = 5.0
)

data class DailyForecast(
    val day: String,
    val date: String,
    val highTemp: Int,
    val lowTemp: Int,
    val condition: String,
    val icon: String = "⛅",
    val precipChance: Int = 0,
    val windSpeed: Double = 5.0
)
