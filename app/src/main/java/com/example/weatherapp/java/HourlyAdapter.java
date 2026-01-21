package com.example.weatherapp.java;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherapp.R;
import java.util.List;

/**
 * 🇰🇭 Cambodia Weather App - Hourly Forecast Adapter
 * 
 * RecyclerView adapter for displaying hourly weather forecast
 */
public class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder> {

    private List<HomeFragment.HourlyData> hourlyDataList;

    public HourlyAdapter(List<HomeFragment.HourlyData> hourlyDataList) {
        this.hourlyDataList = hourlyDataList;
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hourly_forecast, parent, false);
        return new HourlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        HomeFragment.HourlyData data = hourlyDataList.get(position);
        
        holder.timeText.setText(data.time);
        holder.iconText.setText(data.icon);
        holder.tempText.setText(data.temp + "°");
        
        // Highlight current hour
        if (position == 0) {
            holder.itemView.setAlpha(1f);
        } else {
            holder.itemView.setAlpha(0.8f);
        }
    }

    @Override
    public int getItemCount() {
        return hourlyDataList != null ? hourlyDataList.size() : 0;
    }

    public void updateData(List<HomeFragment.HourlyData> newData) {
        this.hourlyDataList = newData;
        notifyDataSetChanged();
    }

    // ═══════════════════════════════════════════════════════════════
    // ViewHolder
    // ═══════════════════════════════════════════════════════════════

    static class HourlyViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        TextView iconText;
        TextView tempText;

        HourlyViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.hourText);
            iconText = itemView.findViewById(R.id.hourIcon);
            tempText = itemView.findViewById(R.id.hourTemp);
        }
    }
}
