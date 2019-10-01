package com.graduate.seoil.sg_projdct;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.graduate.seoil.sg_projdct.Fragments.DatePickerFragment;
import com.graduate.seoil.sg_projdct.Fragments.HomeFragment;
import com.graduate.seoil.sg_projdct.Fragments.TimePickerFragment;
import com.graduate.seoil.sg_projdct.Model.Goal;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.RequiresApi;

public class GoalMaking extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    public final int GOAL_MAKING_ACTIVITY = 0;
    FirebaseUser fuser;

    private Button plan_make, plan_cancel;
    private EditText etTitle, etContent;
    private TextView tv_plan_hour, tv_start_hour, tv_start_date, tv_end_date;

    private ImageView calendar_before, calendar_after;
    private TextView tv_calendar_start, tv_calender_end;
    private Calendar startDate, endDate;
    static final int DATE_DIALOG_ID = 0;
    private TextView activeDateDisplay;
    private Calendar activeDate;
    private Calendar cal;
    private String date;
    private long stamp;

    // 10/01 작업했음.

    HashMap<String, Object> jData;
    HashMap<String, Object> jTitle;
    HashMap<String, Object> jDays;
    HashMap<String, Object> jMonth;
    HashMap<String, Object> jYear;

    private TextView categorys;
    private RelativeLayout category_rel;

    int categoryCountTotal;

    CheckBox[] chkBoxs;
    Integer[] chkBoxIds = {R.id.ckbox_monday, R.id.ckbox_tuesday, R.id.ckbox_wednesday, R.id.ckbox_thursday, R.id.ckbox_friday, R.id.ckbox_saturday, R.id.ckbox_sunday};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_making);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        etTitle = findViewById(R.id.et_plan_title);
        tv_plan_hour = findViewById(R.id.tv_plan_hour);
//        tv_start_hour = findViewById(R.id.tv_start_hour);
        tv_start_date = findViewById(R.id.tv_calendar_start);
        tv_end_date = findViewById(R.id.tv_calendar_end);
        chkBoxs = new CheckBox[chkBoxIds.length];
        plan_make = findViewById(R.id.plan_make);
        plan_cancel = findViewById(R.id.plan_cancel);

        tv_calendar_start = findViewById(R.id.tv_calendar_start);
        tv_calender_end = findViewById(R.id.tv_calendar_end);
        startDate = Calendar.getInstance();

        calendar_before = findViewById(R.id.iv_calendar_before);
        calendar_after = findViewById(R.id.iv_calendar_after);

        category_rel = findViewById(R.id.group_category);
        categorys = findViewById(R.id.et_category);
        jData = new HashMap<>();
        jTitle = new HashMap<>();
        jDays = new HashMap<>();
        jMonth = new HashMap<>();
        jYear = new HashMap<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryCounttt");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryCountTotal = (int)dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        calendar_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(tv_calendar_start, startDate);
            }
        });

        endDate = Calendar.getInstance();
        calendar_after.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(tv_calender_end, endDate);
            }
        });

        category_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoalMaking.this, CategoryActivity.class);
                startActivityForResult(intent, GOAL_MAKING_ACTIVITY);
            }
        });

        updateDisplay(tv_calendar_start, startDate);
        updateDisplay(tv_calender_end, endDate);

        tv_plan_hour = findViewById(R.id.tv_plan_hour);
        tv_plan_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        plan_make.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (etTitle.getText().toString().equals("") || tv_plan_hour.getText().toString().equals("00:00") || categorys.getText().toString().equals("")) {
                    Toast.makeText(GoalMaking.this, "항목을 모두 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    String category = categorys.getText().toString();
                    String title = etTitle.getText().toString();
                    String str_time = tv_plan_hour.getText().toString();
//                String start_hour = tv_start_hour.getText().toString();
                    String start_date = tv_start_date.getText().toString();
                    String end_date = tv_end_date.getText().toString();
                    String checked_days = "";

                    int from_idx_first = start_date.indexOf("-", 1);
                    int from_idx_second = start_date.indexOf("-", from_idx_first + 1);
                    int from_year = Integer.parseInt(start_date.substring(0, from_idx_first));
                    int from_month = Integer.parseInt(start_date.substring(from_idx_first + 1, from_idx_second));
                    int from_day = Integer.parseInt(start_date.substring(from_idx_second + 1));
                    int to_idx_first = end_date.indexOf("-", 1);
                    int to_idx_second = end_date.indexOf("-", to_idx_first + 1);
                    int to_year = Integer.parseInt(end_date.substring(0, to_idx_first));
                    int to_month = Integer.parseInt(end_date.substring(to_idx_first + 1, to_idx_second));
                    int to_day = Integer.parseInt(end_date.substring(to_idx_second + 1));

                    String str_date = 6 + "-" + 3 + "-" + 2019;
                    DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
                    Date dateun = null;
                    try {
                        dateun = (Date) formatter.parse(str_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long output = dateun.getTime() / 1000L;
                    String str = Long.toString(output);
                    long timestamp = Long.parseLong(str) * 1000;

                    int index = str_time.indexOf(":");
                    int hour = Integer.parseInt(str_time.substring(0, index)) * 60;
                    int minute = Integer.parseInt(str_time.substring(index + 1));
                    int time = hour + minute;

                    for (int i = 0; i < chkBoxIds.length; i++) {
                        chkBoxs[i] = findViewById(chkBoxIds[i]);
                        if (chkBoxs[i].isChecked()) {
                            switch (i) {
                                case 0:
                                    checked_days = "월";
                                    break;
                                case 1:
                                    checked_days += "화";
                                    break;
                                case 2:
                                    checked_days += "수";
                                    break;
                                case 3:
                                    checked_days += "목";
                                    break;
                                case 4:
                                    checked_days += "금";
                                    break;
                                case 5:
                                    checked_days += "토";
                                    break;
                                case 6:
                                    checked_days += "일";
                                    break;
                            }
                        }
                    }
                    String[] int_checkDays = new String[checked_days.length()];
                    for (int i = 0; i < checked_days.length(); i++) {
                        if (checked_days.substring(i, i + 1).equals("일"))
                            int_checkDays[i] = "1";
                        else if (checked_days.substring(i, i + 1).equals("월"))
                            int_checkDays[i] = "2";
                        else if (checked_days.substring(i, i + 1).equals("화"))
                            int_checkDays[i] = "3";
                        else if (checked_days.substring(i, i + 1).equals("수"))
                            int_checkDays[i] = "4";
                        else if (checked_days.substring(i, i + 1).equals("목"))
                            int_checkDays[i] = "5";
                        else if (checked_days.substring(i, i + 1).equals("금"))
                            int_checkDays[i] = "6";
                        else if (checked_days.substring(i, i + 1).equals("토"))
                            int_checkDays[i] = "7";
                    }

                    cntSetting(category);


                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid());
                    cal = Calendar.getInstance();
                    cal.set(from_year, from_month - 1, from_day); // 시작 요일 Calendar에 세팅
                    if (to_year == from_year) {       // 같은 년도 (2019 ~ 2019)
                        if (to_month == from_month) { // 같은 년도 같은 월 (2019/4/14 ~ 2019/4/30)
                            for (int d = from_day; d <= to_day; d++) {
                                cal.set(from_year, from_month - 1, d);
                                int dayNum = cal.get(Calendar.DAY_OF_WEEK);
                                for (String int_checkDay : int_checkDays) {
                                    if (dayNum == Integer.parseInt(int_checkDay)) {
                                        if (from_month < 10) {
                                            if (d < 10) {
                                                date = String.valueOf(from_year) + "-0" + String.valueOf(from_month) + "-0" + String.valueOf(d);
                                                stamp = getTimeStamp("0" + from_month, "0" + d, String.valueOf(from_year));
                                            } else {
                                                date = String.valueOf(from_year) + "-0" + String.valueOf(from_month) + "-" + String.valueOf(d);
                                                stamp = getTimeStamp("0" + from_month, String.valueOf(d), String.valueOf(from_year));
                                            }
                                        } else {
                                            if (d < 10)
                                                date = String.valueOf(from_year) + "-" + String.valueOf(from_month) + "-0" + String.valueOf(d);
                                            else
                                                date = String.valueOf(from_year) + "-" + String.valueOf(from_month) + "-" + String.valueOf(d);
                                        }

                                        if (to_month < 10) {
                                            if (to_day < 10)
                                                end_date = String.valueOf(to_year) + "-0" + String.valueOf(to_month) + "-0" + String.valueOf(to_day);
                                            else
                                                end_date = String.valueOf(to_year) + "-0" + String.valueOf(to_month) + "-" + String.valueOf(to_day);
                                        } else {
                                            if (to_day < 10)
                                                end_date = String.valueOf(to_year) + "-" + String.valueOf(to_month) + "-0" + String.valueOf(to_day);
                                            else
                                                end_date = String.valueOf(to_year) + "-" + String.valueOf(to_month) + "-" + String.valueOf(to_day);
                                        }

                                        str_date = to_month + "-" + d + "-" + from_year;
                                        formatter = new SimpleDateFormat("MM-dd-yyyy");
                                        dateun = null;
                                        try {
                                            dateun = (Date) formatter.parse(str_date);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        output = dateun.getTime() / 1000L;
                                        str = Long.toString(output);
                                        timestamp = Long.parseLong(str) * 1000; // timestamp


                                        String goal_id = reference.child(date).push().getKey();
                                        Goal goal = new Goal(title, date, end_date, checked_days, "?", time, 0, 0, time * 60000, time * 60000, 0, timestamp);
                                        assert goal_id != null;
                                        reference.child(date).child(title).setValue(goal);
//                                    reference.child(String.valueOf(timestamp)).child(title).setValue(goal);
                                    }
                                }
                            }
                        } else {                      // 같은 년도 다른 월 (2019/4/14 ~ 2019/7/29)
                            for (int m = from_month; m <= to_month; m++) {
                                if (m == from_month) {   // 4/14 ~ 9/28 인경우 4월 처리
                                    for (int d = from_day; d <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); d++) {
                                        cal.set(from_year, m - 1, d);
                                        int dayNum = cal.get(Calendar.DAY_OF_WEEK);
                                        for (String int_checkDay : int_checkDays) {
                                            if (dayNum == Integer.parseInt(int_checkDay)) {
                                                if (from_month < 10) {
                                                    if (d < 10)
                                                        date = String.valueOf(from_year) + "-0" + String.valueOf(m) + "-0" + String.valueOf(d);
                                                    else
                                                        date = String.valueOf(from_year) + "-0" + String.valueOf(m) + "-" + String.valueOf(d);
                                                } else {
                                                    if (d < 10)
                                                        date = String.valueOf(from_year) + "-" + String.valueOf(m) + "-0" + String.valueOf(d);
                                                    else
                                                        date = String.valueOf(from_year) + "-" + String.valueOf(m) + "-" + String.valueOf(d);
                                                }

                                                if (to_month < 10) {
                                                    if (to_day < 10)
                                                        end_date = String.valueOf(to_year) + "-0" + String.valueOf(to_month) + "-0" + String.valueOf(to_day);
                                                    else
                                                        end_date = String.valueOf(to_year) + "-0" + String.valueOf(to_month) + "-" + String.valueOf(to_day);
                                                } else {
                                                    if (to_day < 10)
                                                        end_date = String.valueOf(to_year) + "-" + String.valueOf(to_month) + "-0" + String.valueOf(to_day);
                                                    else
                                                        end_date = String.valueOf(to_year) + "-" + String.valueOf(to_month) + "-" + String.valueOf(to_day);
                                                }

                                                str_date = m + "-" + d + "-" + from_year;
                                                formatter = new SimpleDateFormat("MM-dd-yyyy");
                                                dateun = null;
                                                try {
                                                    dateun = (Date) formatter.parse(str_date);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                output = dateun.getTime() / 1000L;
                                                str = Long.toString(output);
                                                timestamp = Long.parseLong(str) * 1000; // timestamp
                                                String goal_id = reference.child(date).push().getKey();
                                                Goal goal = new Goal(title, date, end_date, checked_days, "?", time, 0, 0, time * 60000, time * 60000, 0, timestamp);
                                                assert goal_id != null;
                                                reference.child(date).child(title).setValue(goal);
//                                            reference.child(String.valueOf(timestamp)).child(title).setValue(goal); // TODO : 목ㅍ제목에 ., #, $, [ , ] 들어 가면 안됌.
                                            }
                                        }
                                    }
                                } else if (m < to_month) { // 4/14 ~ 9/28 인경우 5,6,7,8월 처리
                                    cal.set(from_year, m - 1, 1); // Calendar 2019/5/1 세팅
                                    for (int d = 1; d <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); d++) {
                                        cal.set(from_year, m - 1, d);
                                        int dayNum = cal.get(Calendar.DAY_OF_WEEK);
                                        for (String int_checkDay : int_checkDays) {
                                            if (dayNum == Integer.parseInt(int_checkDay)) {
                                                if (m < 10) {
                                                    if (d < 10) {
                                                        date = String.valueOf(from_year) + "-0" + String.valueOf(m) + "-0" + String.valueOf(d);
                                                        stamp = getTimeStamp("0" + m, "0" + d, String.valueOf(from_year));
                                                    } else {
                                                        date = String.valueOf(from_year) + "-0" + String.valueOf(m) + "-" + String.valueOf(d);
                                                        stamp = getTimeStamp("0" + m, String.valueOf(d), String.valueOf(from_year));
                                                    }
                                                } else {
                                                    if (d < 10) {
                                                        date = String.valueOf(from_year) + "-" + String.valueOf(m) + "-0" + String.valueOf(d);
                                                        stamp = getTimeStamp(String.valueOf(m), "0" + d, String.valueOf(from_year));
                                                    } else {
                                                        date = String.valueOf(from_year) + "-" + String.valueOf(m) + "-" + String.valueOf(d);
                                                        stamp = getTimeStamp(String.valueOf(m), String.valueOf(d), String.valueOf(from_year));
                                                    }
                                                }

//                                            if (to_month < 10) {
//                                                if (to_day < 10)
//                                                    end_date = String.valueOf(to_year) + "-0" + String.valueOf(to_month) + "-0" + String.valueOf(to_day);
//                                                else
//                                                    end_date = String.valueOf(to_year) + "-0" + String.valueOf(to_month) + "-" + String.valueOf(to_day);
//                                            } else {
//                                                if (to_day < 10)
//                                                    end_date = String.valueOf(to_year) + "-" + String.valueOf(to_month) + "-0" + String.valueOf(to_day);
//                                                else
//                                                    end_date = String.valueOf(to_year) + "-" + String.valueOf(to_month) + "-" + String.valueOf(to_day);
//                                            }

                                                str_date = m + "-" + d + "-" + from_year;
                                                formatter = new SimpleDateFormat("MM-dd-yyyy");
                                                dateun = null;
                                                try {
                                                    dateun = (Date) formatter.parse(str_date);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                output = dateun.getTime() / 1000L;
                                                str = Long.toString(output);
                                                timestamp = Long.parseLong(str) * 1000; // timestamp // timestamp
                                                String goal_id = reference.child(date).push().getKey();
                                                Goal goal = new Goal(title, date, end_date, checked_days, "?", time, 0, 0, time * 60000, time * 60000, 0, timestamp);
                                                assert goal_id != null;
                                                reference.child(date).child(title).setValue(goal);
//                                            reference.child(String.valueOf(timestamp)).child(title).setValue(goal);
                                            }
                                        }
                                    }
                                } else { // 4/14 ~ 9/28 인경우 9월 처리
                                    for (int d = 1; d <= to_day; d++) {
                                        cal.set(from_year, m - 1, d);
                                        int dayNum = cal.get(Calendar.DAY_OF_WEEK);
                                        for (String int_checkDay : int_checkDays) {
                                            if (dayNum == Integer.parseInt(int_checkDay)) {
                                                if (m < 10) {
                                                    if (d < 10)
                                                        date = String.valueOf(to_year) + "-0" + String.valueOf(m) + "-0" + String.valueOf(d);
                                                    else
                                                        date = String.valueOf(to_year) + "-0" + String.valueOf(m) + "-" + String.valueOf(d);
                                                } else {
                                                    if (d < 10)
                                                        date = String.valueOf(to_year) + "-" + String.valueOf(m) + "-0" + String.valueOf(d);
                                                    else
                                                        date = String.valueOf(to_year) + "-" + String.valueOf(m) + "-" + String.valueOf(d);
                                                }

//                                            if (to_month < 10) {
//                                                if (to_day < 10)
//                                                    end_date = String.valueOf(to_year) + "-0" + String.valueOf(to_month) + "-0" + String.valueOf(to_day);
//                                                else
//                                                    end_date = String.valueOf(to_year) + "-0" + String.valueOf(to_month) + "-" + String.valueOf(to_day);
//                                            } else {
//                                                if (to_day < 10)
//                                                    end_date = String.valueOf(to_year) + "-" + String.valueOf(to_month) + "-0" + String.valueOf(to_day);
//                                                else
//                                                    end_date = String.valueOf(to_year) + "-" + String.valueOf(to_month) + "-" + String.valueOf(to_day);
//                                            }

                                                str_date = m + "-" + d + "-" + from_year;
                                                formatter = new SimpleDateFormat("MM-dd-yyyy");
                                                dateun = null;
                                                try {
                                                    dateun = (Date) formatter.parse(str_date);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                output = dateun.getTime() / 1000L;
                                                str = Long.toString(output);
                                                timestamp = Long.parseLong(str) * 1000; // timestamp
                                                String goal_id = reference.child(date).push().getKey();
                                                Goal goal = new Goal(title, date, end_date, checked_days, "?", time, 0, 0, time * 60000, time * 60000, 0, timestamp);
                                                assert goal_id != null;
                                                reference.child(date).child(title).setValue(goal);
//                                            reference.child(String.valueOf(timestamp)).child(title).setValue(goal);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    finish();
                }


            }
        });

        plan_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOAL_MAKING_ACTIVITY) {
            if (resultCode != RESULT_OK) {
                categorys.setText("카테고리를 선택해주세요!");
            }
            if (resultCode == RESULT_OK) {
                String result = data.getExtras().getString(CategoryActivity.RESULT_DATA);
                categorys.setText(result);
            }
        }
    }

    private void updateDisplay(TextView dateDisplay, Calendar date) {
        dateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(date.get(Calendar.YEAR)).append("-")
                        .append(date.get(Calendar.MONTH) + 1).append("-")
                        .append(date.get(Calendar.DAY_OF_MONTH)).append(""));

    }

    public void showDateDialog(TextView dateDisplay, Calendar date) {
        activeDateDisplay = dateDisplay;
        activeDate = date;
        showDialog(DATE_DIALOG_ID);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            activeDate.set(Calendar.YEAR, year);
            activeDate.set(Calendar.MONTH, month);
            activeDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDisplay(activeDateDisplay, activeDate);
            unregisterDateDisplay();
        }
    };

    private void unregisterDateDisplay() {
        activeDateDisplay = null;
        activeDate = null;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, dateSetListener, activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(activeDate.get(Calendar.YEAR), activeDate.get(Calendar.MONTH), activeDate.get(Calendar.DAY_OF_MONTH));
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView textView = findViewById(R.id.tv_plan_hour);
        if (minute != 0)
            textView.setText(hourOfDay + ":" + minute);
        else
            textView.setText(hourOfDay + ":" + minute + "0");
    }

    private long getTimeStamp(String m, String d, String y) {
        String date = m + "-" + d+ "-" + y;
        DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        Date dateun = null;
        try {
            dateun = (Date) formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long output = dateun.getTime() / 1000L;
        String str = Long.toString(output);
        long timestamp = Long.parseLong(str) * 1000; // timestamp

        return timestamp;
    }

    private void cntSetting(final String category) {
        Query query = FirebaseDatabase.getInstance().getReference("CategoryCounttt").orderByChild("name").startAt(category).endAt(category + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryCounttt");
                final HashMap<String, Object> hashMap = new HashMap<>();
                if (dataSnapshot.getChildrenCount() == 0) {
                    hashMap.put("name", category);
                    hashMap.put("value", 1);
                    reference.child(String.valueOf(categoryCountTotal)).setValue(hashMap);
                } else {
                    System.out.println("dataSnapshot.key : " + dataSnapshot.getValue());
                    for (final DataSnapshot postShot : dataSnapshot.getChildren()) {
                        final String key = postShot.getKey();
                        DatabaseReference referenceVal = FirebaseDatabase.getInstance().getReference("CategoryCounttt").child(key).child("value");
                        referenceVal.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long val = (long)dataSnapshot.getValue() + 1;
                                hashMap.put("name", category);
                                hashMap.put("value", val);
                                reference.child(Objects.requireNonNull(key)).updateChildren(hashMap);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}