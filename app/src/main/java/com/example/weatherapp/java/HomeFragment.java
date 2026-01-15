package com.example.weatherapp.java;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 🇰🇭 Cambodia Weather App - Home Fragment
 * 
 * Main weather display with:
 * - Search bar with popular cities
 * - Current weather card
 * - Weather details (wind, humidity, etc.)
 * - Hourly forecast
 * - Air quality & UV index
 * - Sunrise/Sunset times
 */
public class HomeFragment extends Fragment {

    // Views
    private EditText searchEditText;
    private ImageButton locationButton;
    private LinearLayout popularCitiesLayout;
    private TextView cityNameText, countryText, dateTimeText;
    private TextView weatherIcon, temperatureText, conditionText, feelsLikeText;
    private TextView highTempText, lowTempText;
    private TextView aqiValue, aqiStatus, uvValue, uvStatus;
    private TextView sunriseTime, sunsetTime;
    private RecyclerView hourlyRecyclerView;

    // Data
    private HourlyAdapter hourlyAdapter;
    private List<HourlyData> hourlyDataList = new ArrayList<>();

    // Popular cities in Cambodia
    private String[] popularCities = {"Phnom Penh", "Siem Reap", "Battambang", "Sihanoukville", "Kampot"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupPopularCities();
        setupHourlyForecast();
        loadWeatherData();
        updateDateTime();
    }

    private void initViews(View view) {
        searchEditText = view.findViewById(R.id.searchEditText);
        locationButton = view.findViewById(R.id.locationButton);
        popularCitiesLayout = view.findViewById(R.id.popularCitiesLayout);
        
        cityNameText = view.findViewById(R.id.cityNameText);
        countryText = view.findViewById(R.id.countryText);
        dateTimeText = view.findViewById(R.id.dateTimeText);
        
        weatherIcon = view.findViewById(R.id.weatherIcon);
        temperatureText = view.findViewById(R.id.temperatureText);
        conditionText = view.findViewById(R.id.conditionText);
        feelsLikeText = view.findViewById(R.id.feelsLikeText);
        highTempText = view.findViewById(R.id.highTempText);
        lowTempText = view.findViewById(R.id.lowTempText);
        
        aqiValue = view.findViewById(R.id.aqiValue);
        aqiStatus = view.findViewById(R.id.aqiStatus);
        uvValue = view.findViewById(R.id.uvValue);
        uvStatus = view.findViewById(R.id.uvStatus);
        
        sunriseTime = view.findViewById(R.id.sunriseTime);
        sunsetTime = view.findViewById(R.id.sunsetTime);
        
        hourlyRecyclerView = view.findViewById(R.id.hourlyRecyclerView);

        // Location button click
        locationButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "📍 Getting current location...", Toast.LENGTH_SHORT).show();
            loadWeatherData();
        });
    }

    private void setupPopularCities() {
        popularCitiesLayout.removeAllViews();
        
        for (String city : popularCities) {
            TextView chip = new TextView(getContext());
            chip.setText(city);
            chip.setTextColor(getResources().getColor(R.color.white, null));
            chip.setTextSize(13);
            chip.setBackgroundResource(R.drawable.chip_background);
            chip.setPadding(32, 16, 32, 16);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMarginEnd(12);
            chip.setLayoutParams(params);
            
            chip.setOnClickListener(v -> {
                cityNameText.setText(city);
                Toast.makeText(getContext(), "Loading weather for " + city, Toast.LENGTH_SHORT).show();
                loadWeatherData();
            });
            
            popularCitiesLayout.addView(chip);
        }
    }

    private void setupHourlyForecast() {
        hourlyRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        
        // Sample hourly data
        hourlyDataList.clear();
        hourlyDataList.add(new HourlyData("Now", "☀️", 32));
        hourlyDataList.add(new HourlyData("1 PM", "☀️", 33));
        hourlyDataList.add(new HourlyData("2 PM", "☀️", 34));
        hourlyDataList.add(new HourlyData("3 PM", "⛅", 33));
        hourlyDataList.add(new HourlyData("4 PM", "⛅", 32));
        hourlyDataList.add(new HourlyData("5 PM", "☁️", 30));
        hourlyDataList.add(new HourlyData("6 PM", "🌤️", 28));
        hourlyDataList.add(new HourlyData("7 PM", "🌙", 27));
        
        hourlyAdapter = new HourlyAdapter(hourlyDataList);
        hourlyRecyclerView.setAdapter(hourlyAdapter);
    }

    private void loadWeatherData() {
        // Current weather (sample data for Phnom Penh)
        weatherIcon.setText("☀️");
        temperatureText.setText("32°");
        conditionText.setText("Partly Cloudy");
        feelsLikeText.setText("Feels like 36°");
        highTempText.setText("H: 34°");
        lowTempText.setText("L: 26°");
        
        // Air Quality
        aqiValue.setText("65");
        aqiStatus.setText("Moderate");
        
        // UV Index
        uvValue.setText("9");
        uvStatus.setText("Very High");
        
        // Sunrise/Sunset
        sunriseTime.setText("5:45 AM");
        sunsetTime.setText("5:50 PM");
    }

    private void updateDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d • h:mm a", Locale.getDefault());
        dateTimeText.setText(sdf.format(new Date()));
    }

    // ═══════════════════════════════════════════════════════════════
    // Data Classes
    // ═══════════════════════════════════════════════════════════════

    public static class HourlyData {
        public String time;
        public String icon;
        public int temp;

        public HourlyData(String time, String icon, int temp) {
            this.time = time;
            this.icon = icon;
            this.temp = temp;
        }
    }
}
