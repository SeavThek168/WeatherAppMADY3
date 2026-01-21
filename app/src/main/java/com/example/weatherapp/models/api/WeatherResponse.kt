package com.example.weatherapp.models.api

/**
 * Current Weather Response
 * API: /weather
 * Docs: https://openweathermap.org/current
 */
data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val clouds: Clouds,
    val sys: Sys,
    val name: String,
    val dt: Long,
    val visibility: Int? = 10000,
    val timezone: Int? = 0  // Shift in seconds from UTC
)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,  // Note: This is min temp at the moment of calculation, not daily min
    val temp_max: Double,  // Note: This is max temp at the moment of calculation, not daily max
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int? = null,
    val grnd_level: Int? = null
)

data class Wind(
    val speed: Double,  // Wind speed. Units: meter/sec (metric), miles/hour (imperial)
    val deg: Int,       // Wind direction, degrees
    val gust: Double? = null  // Wind gust speed
)

data class Clouds(
    val all: Int  // Cloudiness percentage
)

data class Sys(
    val country: String,
    val sunrise: Long,  // Sunrise time, Unix, UTC
    val sunset: Long    // Sunset time, Unix, UTC
)

/**
 * 5-Day/3-Hour Forecast Response
 * API: /forecast
 * Docs: https://openweathermap.org/forecast5
 * Free tier provides 40 timestamps (5 days x 8 per day, every 3 hours)
 */
data class ForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,  // Number of timestamps (usually 40)
    val list: List<ForecastItem>,
    val city: City
)

data class ForecastItem(
    val dt: Long,  // Time of data forecasted, Unix, UTC
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int? = 10000,
    val pop: Double? = 0.0,  // Probability of precipitation (0-1)
    val rain: Rain? = null,
    val snow: Snow? = null,
    val sys: ForecastSys,
    val dt_txt: String  // Time of data forecasted, ISO format "2022-08-30 15:00:00"
)

data class ForecastSys(
    val pod: String  // Part of day (n = night, d = day)
)

data class Rain(
    val `3h`: Double? = null  // Rain volume for last 3 hours, mm
)

data class Snow(
    val `3h`: Double? = null  // Snow volume for last 3 hours, mm
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int? = 0,
    val timezone: Int,  // Shift in seconds from UTC
    val sunrise: Long,
    val sunset: Long
)

/**
 * Air Pollution Response
 * API: /air_pollution
 * Docs: https://openweathermap.org/api/air-pollution
 * Free tier: Yes
 */
data class AirPollutionResponse(
    val coord: Coord,
    val list: List<AirPollutionItem>
)

data class AirPollutionItem(
    val dt: Long,
    val main: AirQualityMain,
    val components: AirComponents
)

data class AirQualityMain(
    val aqi: Int  // Air Quality Index (1=Good, 2=Fair, 3=Moderate, 4=Poor, 5=Very Poor)
)

data class AirComponents(
    val co: Double,      // Carbon monoxide, μg/m³
    val no: Double,      // Nitrogen monoxide, μg/m³
    val no2: Double,     // Nitrogen dioxide, μg/m³
    val o3: Double,      // Ozone, μg/m³
    val so2: Double,     // Sulphur dioxide, μg/m³
    val pm2_5: Double,   // Fine particles (PM2.5), μg/m³
    val pm10: Double,    // Coarse particles (PM10), μg/m³
    val nh3: Double      // Ammonia, μg/m³
)
