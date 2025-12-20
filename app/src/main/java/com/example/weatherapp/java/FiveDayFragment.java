package com.example.weatherapp.java;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import java.util.ArrayList;
import java.util.List;

/**
 * 🇰🇭 Cambodia Weather App - 5-Day Forecast Fragment
 * 
 * Shows extended weather forecast for the next 5 days
 */
public class FiveDayFragment extends Fragment {

    private RecyclerView dailyRecyclerView;
    private DailyAdapter dailyAdapter;
    private List<DailyData> dailyDataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_five_day, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        dailyRecyclerView = view.findViewById(R.id.dailyRecyclerView);
        setupDailyForecast();
    }

    private void setupDailyForecast() {
        dailyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // 5-day forecast data for Cambodia
        dailyDataList.clear();
        dailyDataList.add(new DailyData("Today", "Dec 20", "☀️", 34, 26, 5));
        dailyDataList.add(new DailyData("Saturday", "Dec 21", "☀️", 34, 26, 10));
        dailyDataList.add(new DailyData("Sunday", "Dec 22", "⛅", 33, 25, 15));
        dailyDataList.add(new DailyData("Monday", "Dec 23", "☁️", 32, 25, 30));
        dailyDataList.add(new DailyData("Tuesday", "Dec 24", "🌧️", 31, 24, 70));
        dailyDataList.add(new DailyData("Wednesday", "Dec 25", "🌧️", 30, 24, 60));
        dailyDataList.add(new DailyData("Thursday", "Dec 26", "⛅", 31, 25, 25));
        
        dailyAdapter = new DailyAdapter(dailyDataList);
        dailyRecyclerView.setAdapter(dailyAdapter);
    }

    // ═══════════════════════════════════════════════════════════════
    // Data Class
    // ═══════════════════════════════════════════════════════════════

    public static class DailyData {
        public String dayName;
        public String date;
        public String icon;
        public int highTemp;
        public int lowTemp;
        public int rainChance;

        public DailyData(String dayName, String date, String icon, int highTemp, int lowTemp, int rainChance) {
            this.dayName = dayName;
            this.date = date;
            this.icon = icon;
            this.highTemp = highTemp;
            this.lowTemp = lowTemp;
            this.rainChance = rainChance;
        }
    }
}
