package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Locale;

public class PlanInformationActivity extends AppCompatActivity {
    //timer
//    ProgressBar mProgressBar;
//    CountDownTimer mCountDownTimer;
//    int i=0;
    private static final long START_TIME_IN_MILLIS = 600000;
    private TextView mTextCount;
    private Button mStart;

    private Button mReset;

    private CountDownTimer mCoutDown;

    private boolean mTimerRunning;
    private long mTimeLeft = START_TIME_IN_MILLIS;

    FirebaseUser fuser;
    DatabaseReference reference;
    TextView title, text;
    String str_title;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_information);
        Intent intent = getIntent();

//        mProgressBar=(ProgressBar)findViewById(R.id.progressbar);
//        mProgressBar.setProgress(i);
//        mCountDownTimer=new CountDownTimer(5000,1000) {
//
//            @Override
//            public void onTick(long millisUntilFinished) {
//                Log.v("Log_tag", "Tick of Progress"+ i+ millisUntilFinished);
//                i++;
//                mProgressBar.setProgress((int)i*100/(5000/1000));
//
//            }
//
//            @Override
//            public void onFinish() {
//                //Do what you want
//                i++;
//                mProgressBar.setProgress(100);
//            }
//        };
//        mCountDownTimer.start();
        mTextCount = findViewById(R.id.tvTimer);
        mStart = findViewById(R.id.btStart);
        mReset = findViewById(R.id.btReset);

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

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        title = findViewById(R.id.goal_name);
        text = findViewById(R.id.goal_content);

        title.setText(intent.getStringExtra("goal_title"));
        text.setText(intent.getStringExtra("goal_text"));
    }

    private void startTimer() {
        mCoutDown = new CountDownTimer(mTimeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeft = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mStart.setText("Start");
                mStart.setVisibility(View.INVISIBLE);
                mReset.setVisibility(View.VISIBLE);
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
        mReset.setVisibility(View.INVISIBLE);
        mStart.setVisibility(View.VISIBLE);
    }

    public void updateCountDownText() {
        int minutes = (int) (mTimeLeft / 1000) / 60;
        int seconds = (int) (mTimeLeft / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextCount.setText(timeLeftFormatted);
    }
}
