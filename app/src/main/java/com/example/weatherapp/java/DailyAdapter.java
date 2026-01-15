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
 * 🇰🇭 Cambodia Weather App - Daily Forecast Adapter
 * 
 * RecyclerView adapter for displaying 5-day weather forecast
 */
public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.DailyViewHolder> {

    private List<FiveDayFragment.DailyData> dailyDataList;

    public DailyAdapter(List<FiveDayFragment.DailyData> dailyDataList) {
        this.dailyDataList = dailyDataList;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_forecast, parent, false);
        return new DailyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyViewHolder holder, int position) {
        FiveDayFragment.DailyData data = dailyDataList.get(position);
        
        holder.dayNameText.setText(data.dayName);
        holder.dateText.setText(data.date);
        holder.iconText.setText(data.icon);
        holder.highTempText.setText(data.highTemp + "°");
        holder.lowTempText.setText(data.lowTemp + "°");
        holder.rainChanceText.setText(data.rainChance + "%");
        
        // Highlight today
        if (position == 0) {
            holder.dayNameText.setTextColor(holder.itemView.getContext()
                    .getResources().getColor(R.color.angkor_gold, null));
        }
    }

    @Override
    public int getItemCount() {
        return dailyDataList != null ? dailyDataList.size() : 0;
    }

    public void updateData(List<FiveDayFragment.DailyData> newData) {
        this.dailyDataList = newData;
        notifyDataSetChanged();
    }

    // ═══════════════════════════════════════════════════════════════
    // ViewHolder
    // ═══════════════════════════════════════════════════════════════

    static class DailyViewHolder extends RecyclerView.ViewHolder {
        TextView dayNameText;
        TextView dateText;
        TextView iconText;
        TextView highTempText;
        TextView lowTempText;
        TextView rainChanceText;

        DailyViewHolder(@NonNull View itemView) {
            super(itemView);
            dayNameText = itemView.findViewById(R.id.dayName);
            dateText = itemView.findViewById(R.id.dayDate);
            iconText = itemView.findViewById(R.id.dayIcon);
            highTempText = itemView.findViewById(R.id.dayHighTemp);
            lowTempText = itemView.findViewById(R.id.dayLowTemp);
            rainChanceText = itemView.findViewById(R.id.rainChance);
        }
    }
}
