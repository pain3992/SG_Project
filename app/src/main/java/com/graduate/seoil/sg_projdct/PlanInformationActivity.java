package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Locale;

public class PlanInformationActivity extends AppCompatActivity {
        private String subgoalname;
        private String subgoaltime;
        int i=0;
        ProgressBar progressBar;
        private static final long START_TIME_IN_MILLIS = 120000;
        private TextView mTextCount;
        private Button mStart;

        private Button mReset;

        CountDownTimer mCoutDown;

        private boolean mTimerRunning = false;
        private long mTimeLeft = START_TIME_IN_MILLIS;

        FirebaseUser fuser;
        DatabaseReference reference;
        TextView title, text,percent;
        String str_title;
        RecyclerView recyclerView;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_plan_information);
            fuser = FirebaseAuth.getInstance().getCurrentUser();
            Intent intent = getIntent();
            mTextCount = findViewById(R.id.tvTimer);
            mStart = findViewById(R.id.btStart);
            mReset = findViewById(R.id.btReset);
            progressBar = (ProgressBar)findViewById(R.id.bar_Timer);
            progressBar.setProgress(i);
            progressBar.setMax(100);


            mStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTimerRunning) {
                        pauseTimer();
                    } else {
                        startTimer();
                        mReset.setVisibility(View.VISIBLE);
                    }
                    ;
                }
            });

            mReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetTimer();
                }
            });

            updateCountDownText();

            title = findViewById(R.id.goal_name);
            text = findViewById(R.id.goal_content);
            percent = findViewById(R.id.goal_rate);
            title.setTypeface(null, Typeface.BOLD);
            text.setTypeface(null, Typeface.BOLD);
            percent.setTypeface(null, Typeface.BOLD);
            title.setTextColor(Color.parseColor("#313F47"));
            title.setText("목표 이름:  "+intent.getStringExtra("goal_title"));
            text.setTextColor(Color.parseColor("#313F47"));
            text.setText("목표 시간:  "+(Integer.toString(intent.getIntExtra("goal_time",0)/60)+"시간  "
                    +(Integer.toString(intent.getIntExtra("goal_time",0)%60))+"분"));
            percent.setTextColor(Color.parseColor("#313F47"));
            percent.setText("달성률:  "+Integer.toString(intent.getIntExtra("percent_status",0))+"%");
        }

        private void startTimer() {
            mCoutDown = new CountDownTimer(mTimeLeft, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    i++;
                    progressBar.setProgress((int)i*100/(120000/1000));
                    mTimeLeft = millisUntilFinished;
                    updateCountDownText();

                }

                @Override
                public void onFinish() {
                    mTimerRunning = false;
                    mStart.setText("Start");
                    mStart.setVisibility(View.INVISIBLE);
                    mReset.setVisibility(View.VISIBLE);
                    i++;
                    progressBar.setProgress(100);
                }
            }.start();

            mTimerRunning = true;
            mStart.setText("Pause");
            mReset.setVisibility(View.INVISIBLE);
        }

        private void pauseTimer() {
            mCoutDown.cancel();
            mTimerRunning = false;
            mStart.setText("Start");
            mReset.setVisibility(View.VISIBLE);
        }

        private void resetTimer() {
            mTimeLeft = START_TIME_IN_MILLIS;
            updateCountDownText();
            progressBar.setProgress(0);
            i=0;
            mTimerRunning = false;
            mReset.setVisibility(View.INVISIBLE);
            mStart.setVisibility(View.VISIBLE);
        }

        public void updateCountDownText() {
            int hours = (int) (mTimeLeft/(1000*60*60))%24;
            int minutes = (int) (mTimeLeft / (1000*60)) % 60;
            int seconds = (int) (mTimeLeft / 1000) % 60;

            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours,minutes, seconds);

            mTextCount.setText(timeLeftFormatted);
        }
    }