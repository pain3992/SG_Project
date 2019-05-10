package com.graduate.seoil.sg_projdct.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.graduate.seoil.sg_projdct.Fragments.HomeFragment;
import com.graduate.seoil.sg_projdct.PlanInformationActivity;
import com.graduate.seoil.sg_projdct.Model.Goal;
import com.graduate.seoil.sg_projdct.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.ViewHolder> {

    private Context mContext;
    private List<Goal> mGoals;

    private JSONArray jsonArray;
    private JSONObject jsonObject;

    private Fragment HomeFragment;

    public GoalAdapter(Context mContext, List<Goal> mGoals) {
        this.mGoals = mGoals;
        this.mContext = mContext;
        HomeFragment = new HomeFragment();
    }

    @NonNull
    @Override
    public GoalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.goallist_item, viewGroup, false);
        return new GoalAdapter.ViewHolder(view);
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GoalAdapter.ViewHolder viewHolder, int i) {
        final Goal goal = mGoals.get(i);
        viewHolder.goal_title.setText(goal.getTitle());
        viewHolder.goal_percentage.setText(String.valueOf(goal.getPercent_status()) + "%");
        viewHolder.goal_days.setText("#" + goal.getDay_cycle());

        if (goal.getPercent_status() == 0)
            viewHolder.goal_play_time.setText("00:00");
        else if (goal.getPercent_status() != 100)
            viewHolder.goal_play_time.setText(hmsTimeFormatter(goal.getTime_status()));
        else {
            viewHolder.goal_play_time.setText(hmsTimeFormatter(goal.getPlan_time() * 60 * 1000));
            viewHolder.goal_complete.setBackgroundResource(R.drawable.grade_complete);
            viewHolder.goal_complete.setVisibility(View.VISIBLE);
        }

        switch (goal.getGrade()) {
            case "?":
                viewHolder.goal_grade.setBackgroundResource(R.drawable.grade_q);
                break;
            case "A":
                viewHolder.goal_grade.setBackgroundResource(R.drawable.grade_a);
                break;
            case "B":
                viewHolder.goal_grade.setBackgroundResource(R.drawable.grade_b);
                break;
            case "C":
                viewHolder.goal_grade.setBackgroundResource(R.drawable.grade_c);
                break;
            case "D":
                viewHolder.goal_grade.setBackgroundResource(R.drawable.grade_d);
                break;
            case "F":
                viewHolder.goal_grade.setBackgroundResource(R.drawable.grade_f);
                break;
        }


        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String getTime = sdf.format(date);


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getTime.equals(goal.getStart_date())) {
                    Intent intent = new Intent(mContext, PlanInformationActivity.class);
                    Bundle bundle = new Bundle();

                    bundle.putString("goal_title", goal.getTitle());
                    bundle.putInt("goal_time", goal.getPlan_time());
                    bundle.putString("start_date", goal.getStart_date());
                    bundle.putString("end_date",goal.getEnd_date());
                    bundle.putInt("time_status",goal.getTime_status());
                    bundle.putInt("percent", goal.getPercent_status());
                    bundle.putInt("processed_time_status", goal.getProcessed_time_status());
                    intent.putExtras(bundle);

                    ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction().remove(HomeFragment).commit();

                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, "오늘 목표만 측정 가능합니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mGoals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView goal_title;
        private TextView goal_percentage;
        private TextView goal_play_time;
        private TextView goal_days;

        private ImageView goal_grade;
        private ImageView goal_complete;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            goal_title= itemView.findViewById(R.id.goalList_title);
            goal_percentage = itemView.findViewById(R.id.plan_percentage);
            goal_play_time = itemView.findViewById(R.id.goalItem_plan_time);
            goal_days = itemView.findViewById(R.id.goalItem_days);

            goal_grade = itemView.findViewById(R.id.goalItem_grade);
            goal_complete = itemView.findViewById(R.id.goalItem_complete);
        }
    }

    private String setConcentrate(int rest_cnt) {
        String grade = "";
        switch (rest_cnt) {
            case 0:
                grade = "grade_a";
                break;
            case 1:
                grade = "grade_b";
                break;
            case 2:
                grade = "grade_c";
                break;
            case 3:
                grade = "grade_d";
                break;
            default:
                grade = "grade_f";
                break;
        }
        return grade;
    }

    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;
    }
}