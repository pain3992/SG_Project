package com.graduate.seoil.sg_projdct;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.graduate.seoil.sg_projdct.Fragments.HomeFragment;
import com.graduate.seoil.sg_projdct.Model.Goal;

import java.util.Locale;

public class PlanInformationActivity extends AppCompatActivity {
    int i=0;
    ProgressBar progressBar;
    private TextView mTextCount;
    private Button mStart;
    private Button mReset;
    CountDownTimer mCoutDown;
    private boolean mTimerRunning = false;
    private long mTimeLeft;
    FirebaseUser fuser;
    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;
    TextView text,percent,toolbar1,solute,delete,toggle_titlte,toggle_start,toggle_end;
    String str_title;
    RecyclerView recyclerView;
    ImageButton openExpand;
    ExpandableRelativeLayout plancontent;
    private Context mContext;
    private int goaltime;
    ImageView back;


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
        int percentage = intent.getIntExtra("percent_status",0);
        goaltime = intent.getIntExtra("goal_time",0);
        final String title = intent.getStringExtra("goal_title");
        mTimeLeft = goaltime*60000;
        String date = intent.getStringExtra("date");
        String enddate = intent.getStringExtra("enddate");



        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                    mReset.setVisibility(View.VISIBLE);
                    i++;
                    progressBar.setProgress(100);
                };
            }
        });

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        updateCountDownText();

        text = findViewById(R.id.goal_content);
        percent = findViewById(R.id.goal_rate);

        text.setTextColor(Color.parseColor("#313F47"));
        text.setText((Integer.toString(intent.getIntExtra("goal_time",0)/60)+"시간  "
                +(Integer.toString(intent.getIntExtra("goal_time",0)%60))+"분"));
        percent.setTextColor(Color.parseColor("#313F47"));
        percent.setText(Integer.toString(percentage)+"%");

        toolbar1 = findViewById(R.id.goalList_toolbar_title);
        toolbar1.setText(intent.getStringExtra("goal_title"));

//        toggle_titlte=findViewById(R.id.text);
//        toggle_titlte.setText(title);
//        toggle_start=findViewById(R.id.start_text);
//        toggle_end=findViewById(R.id.end_text);
//        toggle_start.setText(date);
//        toggle_end.setText(enddate);
        openExpand = findViewById(R.id.imageButton);

//        openExpand.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                plancontent = (ExpandableRelativeLayout) findViewById(R.id.plancontent);
//                plancontent.toggle();
//                openExpand.setImageResource(R.drawable.arrow_greyup);
//            }
//        });

        back = findViewById(R.id.goal_backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        solute = findViewById(R.id.goal_solute);
        solute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fm  = getSupportFragmentManager();
//                HomeFragment fragment = new HomeFragment();
//                fm.beginTransaction().replace(R.id.container,fragment).commit();
                finish();
            }
        });
        reference = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).child(date).child(title);
        delete = findViewById(R.id.goal_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fm  = getSupportFragmentManager();
//                HomeFragment fragment = new HomeFragment();
//                fm.beginTransaction().replace(R.id.container,fragment).commit();
                reference.removeValue();
                finish();

            }
        });


    }

    private void startTimer() {
        mCoutDown = new CountDownTimer(mTimeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                i++;
                progressBar.setProgress((int)i*100/((goaltime*60000)/1000));
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
        mTimeLeft = goaltime*60000;
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
