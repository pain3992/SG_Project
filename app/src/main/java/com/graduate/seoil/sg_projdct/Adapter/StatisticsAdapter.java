package com.graduate.seoil.sg_projdct.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.graduate.seoil.sg_projdct.Model.Goal;
import com.graduate.seoil.sg_projdct.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by baejanghun on 03/05/2019.
 */
public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.ViewHolder> {
    private Context mContext;
    private List<Goal> mGoals;
    private String type;

    public StatisticsAdapter(Context mContext, List<Goal> mGoals, String type) {
        this.mContext = mContext;
        this.mGoals = mGoals;
        this.type = type;
    }


    @NonNull
    @Override
    public StatisticsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.statistics_item, viewGroup, false);
        return new StatisticsAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull StatisticsAdapter.ViewHolder viewHolder, int i) {
        Goal goal = mGoals.get(i);
        if (i == 0)
            viewHolder.rel.setBackgroundColor(Color.parseColor("#c12552"));
        else if (i == 1)
            viewHolder.rel.setBackgroundColor(Color.parseColor("#ff6600"));
        else
            viewHolder.rel.setBackgroundColor(Color.parseColor("#f5c700"));
        viewHolder.title.setText(goal.getTitle());
        if (type.equals("percentage")) {
            viewHolder.percentage.setText(goal.getPercent_status() + "%");
        } else {
            String processed_time = hmsTimeFormatter((goal.getPlan_time() * 60 * 1000 - (long)goal.getTime_status()));
            viewHolder.percentage.setText(processed_time);
        }


    }

    @Override
    public int getItemCount() {
        return mGoals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rel;
        private TextView title;
        private TextView percentage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rel = itemView.findViewById(R.id.statistics_item_rel);
            title = itemView.findViewById(R.id.statistics_item_tv);
            percentage = itemView.findViewById(R.id.statistics_item_percentage);
        }
    }

    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)));

        return hms;
    }
}
