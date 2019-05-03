package com.graduate.seoil.sg_projdct.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Model.Goal;
import com.graduate.seoil.sg_projdct.Model.Group;
import com.graduate.seoil.sg_projdct.Model.GroupUserList;
import com.graduate.seoil.sg_projdct.R;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Created by baejanghun on 28/03/2019.
 */
public class StatisticsFragment extends Fragment {
    TextView tv_week;
    ImageView prev_week, next_week;
    String nowTime, getTime, str_date, end_date, str_start, str_end;
    String[] this_week;
    int from_idx_first, from_idx_second, from_year, from_month, from_day;
    int to_idx_first, to_idx_second, to_year, to_month, to_day;
    long output_start, output_end, timestamp_start, timestamp_end;
    Date start_date, ended_date;

    ProgressDialog dialog;

    List<Goal> mGoals;

    DateFormat formatter;
    SimpleDateFormat sdf;

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        sdf = new SimpleDateFormat("yyyyMMdd");
        getTime = sdf.format(date);
        nowTime = getTime;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        tv_week = view.findViewById(R.id.statistics_week);
        prev_week = view.findViewById(R.id.prev_week);
        next_week = view.findViewById(R.id.next_week);
        mGoals = new ArrayList<>();


        this_week = new String[7];
        try { this_week = weekCalendar(getTime); } catch (Exception e) { e.printStackTrace(); }
        tv_week.setText(this_week[0] + " ~ " + this_week[6]);

        // 저번 주
        prev_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                int year = Integer.parseInt(getTime.substring(0, 4));
                int month  = Integer.parseInt(getTime.substring(4, 6));
                int day = Integer.parseInt(getTime.substring(6, 8));  // 현재 시간을 일주일 전으로.
                calendar.set(year, month - 1, day);

                calendar.add(Calendar.DAY_OF_MONTH, - 7);
                Date date = calendar.getTime();
                getTime = sdf.format(date);

                try { this_week = weekCalendar(getTime); } catch (Exception e) { e.printStackTrace(); }
                tv_week.setText(this_week[0] + " ~ " + this_week[6]);
                setTimeStamp(this_week[0], this_week[6]);

                fetchStatistics(timestamp_start, timestamp_end);
            }
        });

        // 다음 주
        next_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getTime.equals(nowTime))
                    return;
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                int year = Integer.parseInt(getTime.substring(0, 4));
                int month  = Integer.parseInt(getTime.substring(4, 6));
                int day = Integer.parseInt(getTime.substring(6, 8));  // 현재 시간을 일주일 전으로.
                calendar.set(year, month - 1, day);

                calendar.add(Calendar.DAY_OF_MONTH, + 7);
                Date date = calendar.getTime();
                getTime = sdf.format(date);

                try { this_week = weekCalendar(getTime); } catch (Exception e) { e.printStackTrace(); }
                tv_week.setText(this_week[0] + " ~ " + this_week[6]);

                setTimeStamp(this_week[0], this_week[6]);
                fetchStatistics(timestamp_start, timestamp_end);
            }
        });

        return view;
    }

    private void fetchStatistics(final long timestamp_start, final long timestamp_end) {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Goal")
                .child(fuser.getUid())
                .orderByChild("timestamp");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mGoals.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final DatabaseReference goals = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).child(snapshot.getKey());
                    Query goal_query = goals.orderByChild("timestamp").startAt(timestamp_start).endAt(timestamp_end);

                    goal_query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot goalSnapshot) {
                            for (DataSnapshot goalShot : goalSnapshot.getChildren()) {
                                System.out.println("snapshot --> " + goalShot.getValue());
                                Goal goal = goalShot.getValue(Goal.class);
                                mGoals.add(goal);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                new AsyncFetchGoal(mGoals).execute();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private class AsyncFetchGoal extends AsyncTask<Void, Void, List<Goal>> {
        List<Goal> mGoals;

        private AsyncFetchGoal(List<Goal> mGoals) {
            this.mGoals = mGoals;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            dialog = new ProgressDialog(getContext());
//
//            dialog.setTitle("데이터 로딩중");
//            dialog.setMessage("잠시만 기다려주세요");
//            dialog.show();
        }

        @Override
        protected List<Goal> doInBackground(Void... voids) {
            System.out.println("doInBackground size --> " + mGoals.size());
            return mGoals;
        }

        @Override
        protected void onPostExecute(List<Goal> goals) {
            super.onPostExecute(goals);
            System.out.println("onPostExecute size --> " + mGoals.size());
            for (int i = 0; i < mGoals.size(); i++) {
//                Arrays.sort()
                System.out.println("timestamp --> " + mGoals.get(i).getTimestamp());
            }
//            dialog.dismiss();
        }
    }

    private void setTimeStamp(String start_week, String end_week) {
        from_idx_first = start_week.indexOf("-", 1);
        from_idx_second = start_week.indexOf("-", from_idx_first + 1);
        from_year = Integer.parseInt(start_week.substring(0, from_idx_first));
        from_month  = Integer.parseInt(start_week.substring(from_idx_first + 1, from_idx_second));
        from_day = Integer.parseInt(start_week.substring(from_idx_second + 1));

        to_idx_first = end_week.indexOf("-", 1);
        to_idx_second = end_week.indexOf("-", from_idx_first + 1);
        to_year = Integer.parseInt(end_week.substring(0, from_idx_first));
        to_month  = Integer.parseInt(end_week.substring(from_idx_first + 1, from_idx_second));
        to_day = Integer.parseInt(end_week.substring(from_idx_second + 1));

        str_date = from_month + "-" + from_day + "-" + from_year;
        end_date = to_month + "-" + to_day + "-" + to_year;
        formatter = new SimpleDateFormat("MM-dd-yyyy");
        start_date = null;
        ended_date = null;
        try {
            start_date = formatter.parse(str_date);
            ended_date = formatter.parse(end_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        output_start = start_date.getTime() / 1000L;
        output_end = ended_date.getTime() / 1000L;
        str_start = Long.toString(output_start);
        str_end = Long.toString(output_end);
        timestamp_start = Long.parseLong(str_start) * 1000;
        timestamp_end = Long.parseLong(str_end) * 1000;
    }

    public String[] weekCalendar(String yyyymmdd) throws Exception {

        Calendar cal = Calendar.getInstance();
        int toYear = 0;
        int toMonth = 0;
        int toDay = 0;

        if(yyyymmdd == null || yyyymmdd.equals("")){   //파라메타값이 없을경우 오늘날짜
            toYear = cal.get(cal.YEAR);
            toMonth = cal.get(cal.MONTH)+1;
            toDay = cal.get(cal.DAY_OF_MONTH);

            int yoil = cal.get(cal.DAY_OF_WEEK); //요일나오게하기(숫자로)

            if(yoil != 1){   //해당요일이 일요일이 아닌경우
                yoil = yoil-2;
            }else{           //해당요일이 일요일인경우
                yoil = 7;
            }
            cal.set(toYear, toMonth-1, toDay-yoil);  //해당주월요일로 세팅
        }else{
            int yy =Integer.parseInt(yyyymmdd.substring(0, 4));
            int mm =Integer.parseInt(yyyymmdd.substring(4, 6))-1;
            int dd =Integer.parseInt(yyyymmdd.substring(6, 8));
            cal.set(yy, mm,dd);
        }
        String[] arrYMD = new String[7];

        int inYear = cal.get(cal.YEAR);
        int inMonth = cal.get(cal.MONTH);
        int inDay = cal.get(cal.DAY_OF_MONTH);
        int yoil = cal.get(cal.DAY_OF_WEEK); //요일나오게하기(숫자로)
        if(yoil != 1){   //해당요일이 일요일이 아닌경우
            yoil = yoil-2;
        }else{           //해당요일이 일요일인경우
            yoil = 7;
        }
        inDay = inDay - (yoil + 1);
        for(int i = 0; i < 7;i++){
            cal.set(inYear, inMonth, inDay+i);  //
            String y = Integer.toString(cal.get(cal.YEAR));
            String m = Integer.toString(cal.get(cal.MONTH)+1);
            String d = Integer.toString(cal.get(cal.DAY_OF_MONTH));
            if(m.length() == 1) m = "0" + m;
            if(d.length() == 1) d = "0" + d;

            arrYMD[i] = y + "-" + m + "-" + d;
        }

        return arrYMD;
    }
}
