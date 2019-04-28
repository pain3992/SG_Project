package com.graduate.seoil.sg_projdct.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
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
import com.graduate.seoil.sg_projdct.TimerActivity;
import com.graduate.seoil.sg_projdct.GoalMaking;
import com.graduate.seoil.sg_projdct.Model.Goal;
import com.graduate.seoil.sg_projdct.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import in.co.ashclan.ashclanzcalendar.data.Day;
import in.co.ashclan.ashclanzcalendar.widget.CollapsibleCalendar;


public class HomeFragment extends Fragment implements View.OnClickListener {
    DatabaseReference mDatabase;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser fuser;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private RecyclerView recyclerView;
    private List<Goal> listItems = new ArrayList<>();
    private ListView database_listview;
    private GoalAdapter adapter;
    long timestamp, timestamp2;
    String str_userName, str_userImageURL;
    String key, getTime;
    Goal goal;
    int clickCounter = 0;

    String goalname;
    String goaltext;
    String str_year, str_day;
    int str_month;

    ProgressDialog dialog;

    public in.co.ashclan.ashclanzcalendar.widget.CollapsibleCalendar collapsibleCalendar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //로딩중 다이아로그 띄움
        dialog = new ProgressDialog(getContext());

        dialog.setTitle("데이터 로딩중");
        dialog.setMessage("잠시만 기다려주세요");

        dialog.show();

        collapsibleCalendar = view.findViewById(R.id.collapseCalendar);
        recyclerView = view.findViewById(R.id.goal_list);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setHasFixedSize(true); // item이 추가되거나 삭제될 때 RecyclerView의 크기가 변경될 수도 있고, 그렇게 되면 다른 View크기가 변경될 가능성이 있기 때문에 고정시켜버린다.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (getArguments() != null) {
            str_userName = getArguments().getString("str_Username");
            str_userImageURL = getArguments().getString("str_userImageURL");
        }

        collapsibleCalendar.setCalendarListener(new in.co.ashclan.ashclanzcalendar.widget.CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                Day day = collapsibleCalendar.getSelectedDay();
//                Calendar cal = Calendar.getInstance();
//                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
//                Log.e("무슨 요일?", String.valueOf(cal.getTime()));

                Log.e("onDaySelect:--> ", "Selected Day: "
                        + day.getYear() + "/" + (day.getMonth() + 1) + "/" + day.getDay());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Goal")
                        .child(fuser.getUid()).child(day.getYear() + "-" + (day.getMonth() + 1) + "-" + day.getDay());
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
            public void onDataUpdate() {

            }

            @Override
            public void onMonthChange() {

            }

            @Override
            public void onWeekChange(int i) {

            }
        });

        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);

        fab =  view.findViewById(R.id.fab);
        fab1 = view.findViewById(R.id.fab1);
        fab2 = view.findViewById(R.id.fab2);

        fab.setOnClickListener(this);
        fab.setBackgroundColor(Color.parseColor("#386385"));
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        getTime = sdf.format(date);
        final int from_idx_first = getTime.indexOf("-", 1);
        final int from_idx_second = getTime.indexOf("-", from_idx_first + 1);
        str_year = getTime.substring(0, from_idx_first);
        str_month = Integer.parseInt(getTime.substring(from_idx_first + 1, from_idx_second));
        str_day = getTime.substring(from_idx_second + 1);

//        Calendar cal = Calendar.getInstance();
//        Calendar cal2 = Calendar.getInstance();
//        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
//        cal.set(Integer.parseInt(str_year), str_month, Integer.parseInt(str_day));
//        cal2.set(Integer.parseInt(str_year), str_month, Integer.parseInt(str_day));
//        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
//        cal2.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
//        System.out.println("그주 일욜, 토욜 : " + sdf.format(cal.getTime()) + ", " + sdf.format(cal2.getTime()));
//        Date dateun = null;
//        Date dateun2 = null;
//        try {
//            dateun = formatter.parse(formatter.format(cal.getTime()));
//            dateun2 = formatter.parse(formatter.format(cal2.getTime()));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        long output = Objects.requireNonNull(dateun).getTime() / 1000L;
//        long output2 = Objects.requireNonNull(dateun2).getTime() / 1000L;
//        String str = Long.toString(output);
//        String str2 = Long.toString(output2);
//        timestamp = Long.parseLong(str) * 1000;
//        timestamp2 = Long.parseLong(str2) * 1000;

        readGoalThread thread_1 = new readGoalThread();
        readGoalAfterThread thread_2 = new readGoalAfterThread();
        thread_1.run();
        try {
            thread_1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return  view;
    }

    private class readGoalThread extends Thread {
        public readGoalThread() {
            super();
        }

        @Override
        public void run() {
            super.run();
            Query query = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).orderByChild("timestamp")
                    .limitToFirst(7);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listItems.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        System.out.println("snapshot --> " + snapshot.getValue());
                        String date = snapshot.getKey();
                        int index_one = date.indexOf("-", 1);
                        int index_two = date.indexOf("-", index_one + 1);
                        int year = Integer.parseInt(date.substring(0, index_one));
                        int month = Integer.parseInt(date.substring(index_one+ 1, index_two));
                        int day = Integer.parseInt(date.substring(index_two + 1));

                        // 색깔 칠하기.
                        collapsibleCalendar.addEventTag(year, month - 1, day, Color.parseColor("#386385"));
                        if (date.equals(str_year + "-" + str_month + "-" + str_day)) {
                            for (DataSnapshot postshot : snapshot.getChildren()) {
                                Goal goal = postshot.getValue(Goal.class);
                                listItems.add(goal);
                            }
                        }
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
    }

    private class readGoalAfterThread extends Thread {
        public readGoalAfterThread() {
            super();
        }

        @Override
        public void run() {
            super.run();
            Query query = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).orderByChild("timestamp");

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listItems.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        System.out.println("snapshot --> " + snapshot.getValue());
                        String date = snapshot.getKey();
                        int index_one = date.indexOf("-", 1);
                        int index_two = date.indexOf("-", index_one + 1);
                        int year = Integer.parseInt(date.substring(0, index_one));
                        int month = Integer.parseInt(date.substring(index_one+ 1, index_two));
                        int day = Integer.parseInt(date.substring(index_two + 1));

                        // 색깔 칠하기.
                        collapsibleCalendar.addEventTag(year, month - 1, day, Color.parseColor("#386385"));
                        if (date.equals(str_year + "-" + str_month + "-" + str_day)) {
                            for (DataSnapshot postshot : snapshot.getChildren()) {
                                Goal goal = postshot.getValue(Goal.class);
                                listItems.add(goal);
                            }
                        }
                    }

                    adapter = new GoalAdapter(getContext(), listItems);
                    recyclerView.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
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
