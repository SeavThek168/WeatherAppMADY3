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
 * 🇰🇭 Cambodia Weather App - Hourly Forecast Fragment
 * 
 * Shows 24-hour weather forecast with detailed view
 */
public class HourlyFragment extends Fragment {

    private RecyclerView hourlyRecyclerView;
    private HourlyAdapter hourlyAdapter;
    private List<HomeFragment.HourlyData> hourlyDataList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hourly, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        hourlyRecyclerView = view.findViewById(R.id.hourlyRecyclerView);
        setupHourlyForecast();
    }

    private void setupHourlyForecast() {
        hourlyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // 24 hours of forecast data
        hourlyDataList.clear();
        hourlyDataList.add(new HomeFragment.HourlyData("12 PM", "☀️", 32));
        hourlyDataList.add(new HomeFragment.HourlyData("1 PM", "☀️", 33));
        hourlyDataList.add(new HomeFragment.HourlyData("2 PM", "☀️", 34));
        hourlyDataList.add(new HomeFragment.HourlyData("3 PM", "⛅", 33));
        hourlyDataList.add(new HomeFragment.HourlyData("4 PM", "⛅", 32));
        hourlyDataList.add(new HomeFragment.HourlyData("5 PM", "☁️", 30));
        hourlyDataList.add(new HomeFragment.HourlyData("6 PM", "🌤️", 28));
        hourlyDataList.add(new HomeFragment.HourlyData("7 PM", "🌙", 27));
        hourlyDataList.add(new HomeFragment.HourlyData("8 PM", "🌙", 27));
        hourlyDataList.add(new HomeFragment.HourlyData("9 PM", "🌙", 26));
        hourlyDataList.add(new HomeFragment.HourlyData("10 PM", "🌙", 26));
        hourlyDataList.add(new HomeFragment.HourlyData("11 PM", "🌙", 25));
        hourlyDataList.add(new HomeFragment.HourlyData("12 AM", "🌙", 25));
        hourlyDataList.add(new HomeFragment.HourlyData("1 AM", "🌙", 25));
        hourlyDataList.add(new HomeFragment.HourlyData("2 AM", "🌙", 24));
        hourlyDataList.add(new HomeFragment.HourlyData("3 AM", "🌙", 24));
        hourlyDataList.add(new HomeFragment.HourlyData("4 AM", "🌙", 24));
        hourlyDataList.add(new HomeFragment.HourlyData("5 AM", "🌅", 25));
        hourlyDataList.add(new HomeFragment.HourlyData("6 AM", "🌅", 26));
        hourlyDataList.add(new HomeFragment.HourlyData("7 AM", "☀️", 27));
        hourlyDataList.add(new HomeFragment.HourlyData("8 AM", "☀️", 28));
        hourlyDataList.add(new HomeFragment.HourlyData("9 AM", "☀️", 29));
        hourlyDataList.add(new HomeFragment.HourlyData("10 AM", "☀️", 30));
        hourlyDataList.add(new HomeFragment.HourlyData("11 AM", "☀️", 31));
        
        hourlyAdapter = new HourlyAdapter(hourlyDataList);
        hourlyRecyclerView.setAdapter(hourlyAdapter);
    }
}
