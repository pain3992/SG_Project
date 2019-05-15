package com.graduate.seoil.sg_projdct;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaTimestamp;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Fragments.HomeFragment;
import com.graduate.seoil.sg_projdct.Model.Goal;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PlanInformationActivity extends AppCompatActivity {

    Button btn_start, btn_reset;
    CountDownTimer mCoutDown;
    FirebaseUser fuser;
    boolean mTimerRunning = false;
    long mTimeLeft;
    TextView tv_goaltime, tv_accomplish_rate, toggle_titlte,toggle_start,toggle_end, tv_concentrate;
    TextView tv_title, tv_count_time, tv_remain_time, tv_rest_count, tv_early_accomplish, tv_delete;
    ImageButton btn_expand;
    ExpandableRelativeLayout plancontent;
    private int plan_time, time_status, percent, rest_count, processed_time_status, i = 0;
    ImageView iv_StartStop, iv_Reset, iv_back;

    private String start_date, end_date, title, grade;

    DatabaseReference reference;
    // Timer
    private long timeCountInMilliSeconds = 1 * 60000;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    ProgressBar progressBar;
    private CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_information);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        title = intent.getStringExtra("goal_title");
        plan_time = intent.getIntExtra("goal_time",0);
        time_status = intent.getIntExtra("time_status",0);
        start_date = intent.getStringExtra("start_date");
        end_date = intent.getStringExtra("end_date");
        percent = intent.getIntExtra("percent", 0);
        processed_time_status = intent.getIntExtra("processed_time_status", 0);

        reference = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).child(start_date).child(title);

        tv_goaltime = findViewById(R.id.pi_goalTime);
        tv_accomplish_rate = findViewById(R.id.pi_accomplish_rate);
        tv_count_time = findViewById(R.id.pi_count_time);
        tv_remain_time = findViewById(R.id.pi_remain_time);
        tv_rest_count = findViewById(R.id.pi_rest_count);
        iv_back = findViewById(R.id.pi_backButton);
        iv_StartStop = findViewById(R.id.pi_StartStop);
        iv_Reset = findViewById(R.id.pi_reset);
        tv_early_accomplish = findViewById(R.id.pi_early_accomplish);
        tv_delete = findViewById(R.id.pi_delete);
        tv_title = findViewById(R.id.pi_title);
        progressBar = findViewById(R.id.bar_Timer);
//        btn_start = findViewById(R.id.pi_start);
////        btn_reset = findViewById(R.id.pi_reset);

        toggle_titlte = findViewById(R.id.text);
        toggle_start = findViewById(R.id.start_text);
        toggle_end = findViewById(R.id.end_text);
        btn_expand = findViewById(R.id.pi_expand_button);
        plancontent = findViewById(R.id.plancontent);
        tv_concentrate = findViewById(R.id.goal_name);

        String ratefor = String.format(Locale.getDefault(), "%01d", percent);
        tv_accomplish_rate.setText(ratefor+"%");
        mTimeLeft = time_status;
        tv_goaltime.setText((Integer.toString(intent.getIntExtra("goal_time",0)/60)+"시간  " +(Integer.toString(intent.getIntExtra("goal_time",0)%60))+"분"));
        tv_title.setText(intent.getStringExtra("goal_title"));
//        updateCountDownText();

        toggle_titlte.setText(title);
        toggle_start.setText(start_date);
        toggle_end.setText(end_date);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                percent = (int) (long)dataSnapshot.child("percent_status").getValue();
                timeCountInMilliSeconds = (long)dataSnapshot.child("plan_time").getValue() * 60 * 1000;
                rest_count = (int) (long)dataSnapshot.child("rest_count").getValue();
                mTimeLeft = (long) dataSnapshot.child("time_status").getValue();

                if (percent == 100) {
                    iv_StartStop.setVisibility(View.GONE);
                    tv_early_accomplish.setVisibility(View.GONE);

                }

                long progress_time = 0;
                if (percent != 0)
                    progress_time = (plan_time * 60 * 1000) - mTimeLeft + 1000;
                else
                    progress_time = (plan_time * 60 * 1000) - mTimeLeft;

                tv_remain_time.setText(hmsTimeFormatter(mTimeLeft));
                tv_count_time.setText(hmsTimeFormatter(progress_time));
                tv_rest_count.setText(String.valueOf(rest_count) + "회 휴식");
                setConcentrate(rest_count - 1);

                setProgressBarValues();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        plancontent.collapse();
        btn_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plancontent.toggle();
                btn_expand.setImageResource(R.drawable.arrow_greyup);
            }
        });

        // 시작 버튼
        iv_StartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });

        // 리셋 버튼
        iv_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        // 백버튼
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerStatus == TimerStatus.STARTED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlanInformationActivity.this);
                    builder.setMessage("기록이 중지 됩니다. 정말 종료하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    goalUpdate();
                                    finish();
                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    goalUpdate();
                    finish();
                }
            }
        });

        // 조기 달성
        tv_early_accomplish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("percent_status", 100);
                hashMap.put("processed_time_status", 0);
                hashMap.put("time_status", 0);
                reference.updateChildren(hashMap);
                progressBar.setProgress(100);
                finish();
            }
        });

        // 삭제
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.removeValue();
                finish();
            }
        });

    }

    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {
            setProgressBarValues();
//            iv_Reset.setVisibility(View.VISIBLE);
            iv_StartStop.setImageResource(R.drawable.pause_circle);
            timerStatus = TimerStatus.STARTED;
            startCountDownTimer();
        } else {
            int percentage = 100-(int)((mTimeLeft*100/(plan_time*60000)));
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("time_status", mTimeLeft);
            hashMap.put("percent_status", percentage);
            hashMap.put("rest_count", rest_count + 1);
            setConcentrate(rest_count);
            hashMap.put("grade", grade);

            reference.updateChildren(hashMap);

            String ratefor = String.format(Locale.getDefault(), "%01d", percentage);
            rest_count += 1;
            tv_rest_count.setText(String.valueOf(rest_count) + "회 휴식");
            tv_accomplish_rate.setText(ratefor+"%");
            timeCountInMilliSeconds = mTimeLeft;



            iv_Reset.setVisibility(View.GONE);
            iv_StartStop.setImageResource(R.drawable.play_circle);
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();
        }
    }

    private void setConcentrate(int cnt) {
        System.out.println("cnt : " + cnt);
        switch (cnt) {
            case -1:
            case 0:
                grade = "A";
                break;
            case 1:
                grade = "B";
                break;
            case 2:
                grade = "C";
                break;
            case 3:
                grade = "D";
                break;
            default:
                grade = "F";
                break;
        }
        tv_concentrate.setText(grade);
    }

    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeft = millisUntilFinished;

                long progress_time = (plan_time * 60 * 1000) - mTimeLeft + 1000;
                tv_count_time.setText(hmsTimeFormatter(progress_time));
                tv_remain_time.setText(hmsTimeFormatter(mTimeLeft));
                progressBar.setProgress((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                tv_count_time.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                setProgressBarValues();
                iv_Reset.setVisibility(View.GONE);
                iv_StartStop.setImageResource(R.drawable.play_circle);
                timerStatus = TimerStatus.STOPPED;

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("percent_status", 100);
                hashMap.put("processed_time_status", plan_time * 60 * 1000);
                hashMap.put("time_status", 0);
                hashMap.put("grade", grade);
                reference.updateChildren(hashMap);
                finish();


            }
        }.start();
        countDownTimer.start();
    }

    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        if(timerStatus == TimerStatus.STARTED){
            AlertDialog.Builder builder = new AlertDialog.Builder(PlanInformationActivity.this);
            builder.setTitle("이이이");
            builder.setMessage("기록이 중지 됩니다. 정말 종료하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        goalUpdate();
                        finish();
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else{
            goalUpdate();
            finish();
        }

    }
    private void goalUpdate() {
        processed_time_status = (int)mTimeLeft;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("processed_time_status", processed_time_status);

        reference.updateChildren(hashMap);
    }

    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }

    private void setProgressBarValues() {
        progressBar.setMax(plan_time * 60 * 1000 / 1000);
        progressBar.setProgress((int)mTimeLeft / 1000);
        timeCountInMilliSeconds = mTimeLeft;
    }

    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;
    }
}
