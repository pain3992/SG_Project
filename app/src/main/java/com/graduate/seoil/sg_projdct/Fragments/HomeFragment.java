package com.graduate.seoil.sg_projdct.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.Index;
import com.graduate.seoil.sg_projdct.Adapter.GoalAdapter;
import com.graduate.seoil.sg_projdct.IndexActivity;
import com.graduate.seoil.sg_projdct.Model.EventDay;
import com.graduate.seoil.sg_projdct.TimerActivity;
import com.graduate.seoil.sg_projdct.GoalMaking;
import com.graduate.seoil.sg_projdct.Model.Goal;
import com.graduate.seoil.sg_projdct.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import in.co.ashclan.ashclanzcalendar.data.Day;
import in.co.ashclan.ashclanzcalendar.data.Event;
import in.co.ashclan.ashclanzcalendar.widget.CollapsibleCalendar;

import com.google.android.gms.tasks.Tasks;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private RecyclerView recyclerView;
    private List<Goal> listItems;
    private List<EventDay> eventDays;
    protected List<String> eventDayList;
    private GoalAdapter adapter;

    String str_userName, str_userImageURL;
    String key, getTime;
    String goalname, goaltext, str_year, str_month, str_day;
    long timestamp_start, timestamp_end;

    ProgressDialog dialog;
    TaskCompletionSource<List<Goal>> tcs;

    DatabaseReference reference;
    Query query;
    ValueEventListener listener;


    public in.co.ashclan.ashclanzcalendar.widget.CollapsibleCalendar collapsibleCalendar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (getArguments() != null) {
            str_userName = getArguments().getString("str_Username");
            str_userImageURL = getArguments().getString("str_userImageURL");
        }

        //로딩중 다이아로그 띄움
        dialog = new ProgressDialog(getContext());

        dialog.setTitle("데이터 로딩중");
        dialog.setMessage("잠시만 기다려주세요");
        dialog.show();

        collapsibleCalendar = view.findViewById(R.id.collapseCalendar);

        eventDayList = new ArrayList<>();
        eventDays = new ArrayList<>();
        recyclerView = view.findViewById(R.id.goal_list);
        recyclerView.setHasFixedSize(true); // item이 추가되거나 삭제될 때 RecyclerView의 크기가 변경될 수도 있고, 그렇게 되면 다른 View크기가 변경될 가능성이 있기 때문에 고정시켜버린다.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listItems = new ArrayList<>();
        adapter = new GoalAdapter(getContext(), listItems);
        recyclerView.setAdapter(adapter);

        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        getTime = sdf.format(date);
        final int from_idx_first = getTime.indexOf("-", 1);
        final int from_idx_second = getTime.indexOf("-", from_idx_first + 1);
        str_year = getTime.substring(0, from_idx_first);
        str_month = getTime.substring(from_idx_first + 1, from_idx_second);
        str_day = getTime.substring(from_idx_second + 1);


        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);

        fab =  view.findViewById(R.id.fab);
        fab1 = view.findViewById(R.id.fab1);
        fab2 = view.findViewById(R.id.fab2);

        fab.setOnClickListener(this);
        fab.setBackgroundColor(Color.parseColor("#386385"));
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        collapsibleCalendar.setCalendarListener(new in.co.ashclan.ashclanzcalendar.widget.CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

                Day day = collapsibleCalendar.getSelectedDay();
                Log.e("onDaySelect:--> ", "Selected Day: " + day.getYear() + "/" + (day.getMonth() + 1) + "/" + day.getDay());

                String click_month = "";
                String click_day = "";
                int year = day.getYear();

                int month = day.getMonth();
                if (month < 9)
                    click_month = "0" + String.valueOf(month + 1);
                else
                    click_month = String.valueOf(month + 1);

                int yoil = day.getDay();
                if (yoil < 10)
                    click_day = "0" + String.valueOf(yoil);
                else
                    click_day = String.valueOf(yoil);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Goal")
                        .child(fuser.getUid()).child(day.getYear() + "-" + click_month + "-" + click_day);

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listItems.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Goal goal = snapshot.getValue(Goal.class);
                            listItems.add(goal);
                        }
                        adapter = new GoalAdapter(getContext(), listItems);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onItemClick(View view) {
                Day day = collapsibleCalendar.getSelectedDay();
                Log.i(getClass().getName(), "The Day of Clicked View: "
                        + day.getYear() + "/" + (day.getMonth() + 1) + "/" + day.getDay());
            }

            @Override
            public void onDataUpdate() { }

            @Override
            public void onMonthChange() {
                Day day = collapsibleCalendar.getSelectedDay();
                getIntervalTimeStamp(day.getYear(), day.getMonth() + 1);
                fetchGoalList(timestamp_start, timestamp_end);
            }

            @Override
            public void onWeekChange(int i) { }
        });

        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getIntervalTimeStamp();
        fetchGoalList(timestamp_start, timestamp_end);
    }

    private void fetchGoalList(final long start_timestamp, final long ended_timestamp) {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println("start_timestamp : " + start_timestamp + ", ended_timestamp : " + ended_timestamp);
        query = FirebaseDatabase.getInstance().getReference("Goal")
                .child(fuser.getUid())
                .orderByChild("timestamp");

//        reference = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid());
        listener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final String date = snapshot.getKey();

                    final DatabaseReference goals = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).child(date);
                    Query goal_query = goals.orderByChild("timestamp").startAt(start_timestamp).endAt(ended_timestamp);

                    goal_query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String child_date = dataSnapshot.getKey();
                            for (DataSnapshot child_snapshot : dataSnapshot.getChildren()) {
                                int index_one = child_date.indexOf("-", 1);
                                int index_two = child_date.indexOf("-", index_one + 1);
                                final int year = Integer.parseInt(child_date.substring(0, index_one));
                                final int month = Integer.parseInt(child_date.substring(index_one + 1, index_two));
                                final int day = Integer.parseInt(child_date.substring(index_two + 1));
                                collapsibleCalendar.addEventTag(year, month - 1, day, Color.RED);

                                if (child_date.equals(str_year + "-" + str_month + "-" + str_day)) {
                                    Goal goal = child_snapshot.getValue(Goal.class);
                                    listItems.add(goal);
                                }
                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                adapter = new GoalAdapter(getContext(), listItems);
                recyclerView.setAdapter(adapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPaused");
        query.removeEventListener(listener);
    }

    //    private class addEventTag extends AsyncTask<Void, String, List<String>> {
//        @Override
//        protected List<String> doInBackground(Void... voids) {
//            FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid());
//            reference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    listItems.clear();
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        final String date = snapshot.getKey();
//                        int index_one = date.indexOf("-", 1);
//                        int index_two = date.indexOf("-", index_one + 1);
//                        final int year = Integer.parseInt(date.substring(0, index_one));
//                        final int month = Integer.parseInt(date.substring(index_one + 1, index_two));
//                        final int day = Integer.parseInt(date.substring(index_two + 1));
//
//                        onProgressUpdate(date);
//
//                        if (date.equals(str_year + "-" + str_month + "-" + str_day)) {
//                            for (DataSnapshot postshot : snapshot.getChildren()) {
//                                Goal goal = postshot.getValue(Goal.class);
//                                listItems.add(goal);
//                            }
//                        }
//                    }
//                    adapter = new GoalAdapter(getContext(), listItems);
//                    recyclerView.setAdapter(adapter);
//                    dialog.dismiss();
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(String... dates) {
////            System.out.println("dates :: " + Arrays.toString(dates));
//            int index_one = Arrays.toString(dates).indexOf("-", 1);
//            int index_two = Arrays.toString(dates).indexOf("-", index_one + 1);
//            int index_three = Arrays.toString(dates).indexOf("]", 1);
////            System.out.println("index_one : " + index_one + ", index_two : " + index_two);
//            int year = Integer.parseInt(Arrays.toString(dates).substring(1, index_one));
//            int month = Integer.parseInt(Arrays.toString(dates).substring(index_one+ 1, index_two));
//            int day = Integer.parseInt(Arrays.toString(dates).substring(index_two + 1, index_three));
////            System.out.println("year : " + year + ", month : " + month + ", day : " + day);
//            collapsibleCalendar.addEventTag(year, month - 1, day, Color.parseColor("#386385"));
//        }
//
//        @Override
//        protected void onPostExecute(List<String> dates) {
////            for (int i = 0; i < eventDayList.size(); i++) {
////                int index_one = eventDayList.get(i).indexOf("-", 1);
////                int index_two = eventDayList.get(i).indexOf("-", index_one + 1);
////                int year = Integer.parseInt(eventDayList.get(i).substring(0, index_one));
////                int month = Integer.parseInt(eventDayList.get(i).substring(index_one+ 1, index_two));
////                int day = Integer.parseInt(eventDayList.get(i).substring(index_two + 1));
////                collapsibleCalendar.addEventTag(year, month - 1, day, Color.parseColor("#386385"));
////
////            }
//
//        }
//    }

    public String[] weekCalendar(String yyyymmdd) throws Exception{

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
        inDay = inDay-yoil;
        for(int i = 0; i < 7;i++){
            cal.set(inYear, inMonth, inDay+i);  //
            String y = Integer.toString(cal.get(cal.YEAR));
            String m = Integer.toString(cal.get(cal.MONTH)+1);
            String d = Integer.toString(cal.get(cal.DAY_OF_MONTH));
            if(m.length() == 1) m = "0" + m;
            if(d.length() == 1) d = "0" + d;

            arrYMD[i] = y+m +d;

        }

        return arrYMD;
    }



    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                break;
            case R.id.fab1:
                anim();
                Intent intent = new Intent(getContext(), TimerActivity.class);
                startActivity(intent);
                break;
            case R.id.fab2:
                anim();
                Intent myintent = new Intent(getContext(), GoalMaking.class);
                startActivity(myintent);
                break;
        }

    }

    private void getIntervalTimeStamp() {
        DecimalFormat df = new DecimalFormat("00");
        Calendar currentCalendar = Calendar.getInstance();
        String year = df.format(currentCalendar.get(Calendar.YEAR));
        String month  = df.format(currentCalendar.get(Calendar.MONTH) + 1);
        String lastDay =  df.format(currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH ));

        String str_date = month + "-01-" + year;
        String end_date = month + "-" + lastDay + "-" + year;

        DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        Date start_date = null;
        Date ended_date = null;
        try {
            start_date = formatter.parse(str_date);
            ended_date = formatter.parse(end_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long output_start = start_date.getTime() / 1000L;
        long output_end = ended_date.getTime() / 1000L;
        String str_start = Long.toString(output_start);
        String str_end = Long.toString(output_end);

        timestamp_start = Long.parseLong(str_start) * 1000;
        timestamp_end = Long.parseLong(str_end) * 1000;
    }

    private void getIntervalTimeStamp(int y, int m) {
        DecimalFormat df = new DecimalFormat("00");
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(y, m, 1);

        String lastDay =  df.format(currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH ));

        String str_date = m + "-01-" + y;
        String end_date = m + "-" + lastDay + "-" + y;

        DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        Date start_date = null;
        Date ended_date = null;
        try {
            start_date = formatter.parse(str_date);
            ended_date = formatter.parse(end_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long output_start = start_date.getTime() / 1000L;
        long output_end = ended_date.getTime() / 1000L;
        String str_start = Long.toString(output_start);
        String str_end = Long.toString(output_end);

        timestamp_start = Long.parseLong(str_start) * 1000;
        timestamp_end = Long.parseLong(str_end) * 1000;
    }

    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }
}
