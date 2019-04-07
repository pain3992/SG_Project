package com.graduate.seoil.sg_projdct;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Fragments.DatePickerFragment;
import com.graduate.seoil.sg_projdct.Fragments.TimePickerFragment;
import com.graduate.seoil.sg_projdct.Model.Goal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GoalMaking extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    DatabaseReference reference;
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

    CheckBox[] chkBoxs;
    Integer[] chkBoxIds = {R.id.ckbox_monday, R.id.ckbox_tuesday, R.id.ckbox_wednesday, R.id.ckbox_thursday, R.id.ckbox_friday, R.id.ckbox_saturday, R.id.ckbox_sunday};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_making);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        etTitle = findViewById(R.id.et_plan_title);
        tv_plan_hour = findViewById(R.id.tv_plan_hour);
        tv_start_hour = findViewById(R.id.tv_start_hour);
        tv_start_date = findViewById(R.id.tv_calendar_start);
        tv_end_date = findViewById(R.id.tv_calendar_end);
        chkBoxs = new CheckBox[chkBoxIds.length];
        plan_make = findViewById(R.id.plan_make);

        tv_calendar_start = findViewById(R.id.tv_calendar_start);
        tv_calender_end = findViewById(R.id.tv_calendar_end);
        startDate = Calendar.getInstance();

        calendar_before = findViewById(R.id.iv_calendar_before);
        calendar_after = findViewById(R.id.iv_calendar_after);

        calendar_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(tv_calendar_start, startDate);
//                DialogFragment datePicker = new DatePickerFragment();
//                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        endDate = Calendar.getInstance();
        calendar_after.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(tv_calender_end, endDate);
//                DialogFragment datePicker = new DatePickerFragment();
//                datePicker.show(getSupportFragmentManager(), "date picker");
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
            @Override
            public void onClick(View v) {
                String title = etTitle.getText().toString();
                String str_time = tv_plan_hour.getText().toString();
                String start_hour = tv_start_hour.getText().toString();
                String start_date = tv_start_date.getText().toString();
                String end_date = tv_end_date.getText().toString();
                String checked_days = "";

//                int from_idx_first = start_date.indexOf("-", 0);
//                int from_idx_second = start_date.indexOf("-", 1);
//                int from_year = Integer.parseInt(start_date.substring(0, from_idx_first));
//                int from_month  = Integer.parseInt(start_date.substring(from_idx_first + 1, from_idx_second));
//                int from_day = Integer.parseInt(start_date.substring(from_idx_second + 1));
//                int to_idx_first = end_date.indexOf("-", 0);
//                int to_idx_second = end_date.indexOf("-", 1);
//                int to_year = Integer.parseInt(start_date.substring(0, to_idx_first));
//                int to_month  = Integer.parseInt(start_date.substring(to_idx_first + 1, to_idx_second));
//                int to_day = Integer.parseInt(start_date.substring(to_idx_second + 1));



                int index = str_time.indexOf(":");
                int hour = Integer.parseInt(str_time.substring(0, index)) * 60;
                int minute = Integer.parseInt(str_time.substring(index + 1));
                int time = hour + minute;

                for(int i = 0; i < chkBoxIds.length; i++) {
                    chkBoxs[i] = findViewById(chkBoxIds[i]);
                    if (chkBoxs[i].isChecked()) {
                        checked_days += (chkBoxs[i].getText().toString());
                    }
                }

                Goal group = new Goal(title, start_date, end_date, checked_days, time, 0, 0, 0);
                reference = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).child(title);
                reference.setValue(group);
                finish();
            }
        });
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
}