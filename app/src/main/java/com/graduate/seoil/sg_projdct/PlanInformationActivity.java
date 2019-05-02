package com.graduate.seoil.sg_projdct;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
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
    DatabaseReference reference,reference2,reference3;
    FirebaseDatabase firebaseDatabase;
    TextView text,percent,toolbar1,solute,delete,toggle_titlte,toggle_start,toggle_end;
    String str_title;
    RecyclerView recyclerView;
    ImageButton openExpand;
    ExpandableRelativeLayout plancontent;
    private Context mContext;
    private int goaltime,time_status;
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
        int percentage2 = intent.getIntExtra("percent_status",0);
        goaltime = intent.getIntExtra("goal_time",0);
        time_status = intent.getIntExtra("time_status",0);
        final String title = intent.getStringExtra("goal_title");
        mTimeLeft = time_status;
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
        reference2 = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).child(date).child(title).child("percent_status");
        reference3 = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).child(date).child(title).child("time_status");


        rate();

        text.setTextColor(Color.parseColor("#313F47"));
        text.setText((Integer.toString(intent.getIntExtra("goal_time",0)/60)+"시간  "
                +(Integer.toString(intent.getIntExtra("goal_time",0)%60))+"분"));


        toolbar1 = findViewById(R.id.goalList_toolbar_title);
        toolbar1.setText(intent.getStringExtra("goal_title"));

        toggle_titlte=findViewById(R.id.text);
        toggle_titlte.setText(title);
        toggle_start=findViewById(R.id.start_text);
        toggle_end=findViewById(R.id.end_text);
        toggle_start.setText(date);
        toggle_end.setText(enddate);
        openExpand = findViewById(R.id.imageButton);
        plancontent = (ExpandableRelativeLayout) findViewById(R.id.plancontent);

        plancontent.collapse();
        openExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plancontent.toggle();
                openExpand.setImageResource(R.drawable.arrow_greyup);
            }
        });

        back = findViewById(R.id.goal_backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTimerRunning==true){
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlanInformationActivity.this);
                    builder.setTitle(R.string.app_name);
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setMessage("기록이 중지 됩니다. 정말 종료하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    pauseTimer();
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
                    finish();
                }
            }
        });

        solute = findViewById(R.id.goal_solute);
        solute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fm  = getSupportFragmentManager();
//                HomeFragment fragment = new HomeFragment();
//                fm.beginTransaction().replace(R.id.container,fragment).commit();
                reference2.setValue(100);
                reference3.setValue(0);
                progressBar.setProgress(100);
                finish();
            }
        });
        reference = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).child(date).child(title);

        delete = findViewById(R.id.goal_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.removeValue();
                finish();

            }
        });

    }

    private void startTimer() {
        mCoutDown = new CountDownTimer(mTimeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time_status();
                i++;
                progressBar.setProgress((int)i*100/((goaltime*60000)/1000));
                mTimeLeft = millisUntilFinished;
                updateCountDownText();
                rate();

            }

            @Override
            public void onFinish() {
                time_status();
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
        time_status();
        mCoutDown.cancel();
        mTimerRunning = false;
        mStart.setText("Start");
        mReset.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        mTimeLeft = goaltime*60000;
        updateCountDownText();
        rate();
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
    @Override
    public void onBackPressed() {
        if(mTimerRunning==true){
            AlertDialog.Builder builder = new AlertDialog.Builder(PlanInformationActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage("기록이 중지 됩니다. 정말 종료하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            pauseTimer();
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
            finish();
        }

    }
    public void rate(){
//        Intent intent = getIntent();
//        int percentage = intent.getIntExtra("percent_status",0);
        int percentage = 100-(int)((mTimeLeft*100/(goaltime*60000)));
        String ratefor = String.format(Locale.getDefault(), "%01d", percentage);
        percent.setText(ratefor+"%");
        System.out.println(percentage);
        reference2.setValue(percentage);
    }
    public void time_status(){
        reference3.setValue(mTimeLeft);
    }
    public void firsttime_status(){
        if(time_status==0){
            reference3.setValue(goaltime);
        }

    }
}
