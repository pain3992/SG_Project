package com.graduate.seoil.sg_projdct;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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

public class PlanInformationActivity extends AppCompatActivity {
    int i=0;
    ProgressBar progressBar;
    Button btn_start, btn_reset;
    CountDownTimer mCoutDown;
    FirebaseUser fuser;
    boolean mTimerRunning = false;
    long mTimeLeft;
    TextView toggle_titlte,toggle_start,toggle_end;
    TextView tv_title, tv_count_time, tv_early_accomplish, tv_delete;
    TextView tv_goaltime, tv_accomplish_rate;
    ImageButton btn_expand;
    ExpandableRelativeLayout plancontent;
    private int goaltime,time_status, percent, processed_time_status;
    ImageView iv_back;

    String start_date, end_date, title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_information);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        title = intent.getStringExtra("goal_title");
        goaltime = intent.getIntExtra("goal_time",0);
        time_status = intent.getIntExtra("time_status",0);
        start_date = intent.getStringExtra("start_date");
        end_date = intent.getStringExtra("end_date");
        percent = intent.getIntExtra("percent", 0);
        processed_time_status = intent.getIntExtra("processed_time_status", 0);

        mTimeLeft = time_status;

        tv_goaltime = findViewById(R.id.pi_goalTime);
        tv_accomplish_rate = findViewById(R.id.pi_accomplish_rate);
        tv_count_time = findViewById(R.id.pi_count_time);
        iv_back = findViewById(R.id.pi_backButton);
        tv_early_accomplish = findViewById(R.id.pi_early_accomplish);
        tv_delete = findViewById(R.id.pi_delete);
        tv_title = findViewById(R.id.pi_title);
        progressBar = findViewById(R.id.bar_Timer);
        btn_start = findViewById(R.id.pi_start);
        btn_reset = findViewById(R.id.pi_reset);

        toggle_titlte = findViewById(R.id.text);
        toggle_start = findViewById(R.id.start_text);
        toggle_end = findViewById(R.id.end_text);
        btn_expand = findViewById(R.id.pi_expand_button);
        plancontent = findViewById(R.id.plancontent);

        String ratefor = String.format(Locale.getDefault(), "%01d", percent);
        tv_accomplish_rate.setText(ratefor+"%");
        tv_goaltime.setTextColor(Color.parseColor("#313F47"));
        tv_goaltime.setText((Integer.toString(intent.getIntExtra("goal_time",0)/60)+"시간  " +(Integer.toString(intent.getIntExtra("goal_time",0)%60))+"분"));
        tv_title.setText(intent.getStringExtra("goal_title"));
        updateCountDownText();

        toggle_titlte.setText(title);
        toggle_start.setText(start_date);
        toggle_end.setText(end_date);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).child(start_date).child(title);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                percent = (int) (long)dataSnapshot.child("percent_status").getValue();
                progressBar.setProgress(percent);
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
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    new AsyncTimer().execute(mTimeLeft);
                    btn_reset.setVisibility(View.VISIBLE);
                    i++;
                }
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
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
                else
                    finish();
            }
        });


        // 라셋 버튼
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        // 조기 달성
        tv_early_accomplish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //
//                FragmentManager fm  = getSupportFragmentManager();
//                HomeFragment fragment = new HomeFragment();
//                fm.beginTransaction().replace(R.id.container,fragment).commit();
//                reference2.setValue(100);
//                reference3.setValue(0);
                progressBar.setProgress(100);
                finish();
            }
        });

        // 삭제
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                reference.removeValue();
                finish();
            }
        });

    }

    private class AsyncTimer extends AsyncTask<Long, Long, String> {
        @Override
        protected void onPreExecute() {
            i = percent;
            progressBar.setMax(100);
        }

        @Override
        protected String doInBackground(final Long... longs) {
            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCoutDown = new CountDownTimer(longs[0], 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            i++;
                            publishProgress(millisUntilFinished);
                        }

                        @Override
                        public void onFinish() {
                            time_status();
                            mTimerRunning = false;
                            btn_start.setText("Start");
                            btn_start.setVisibility(View.INVISIBLE);
                            btn_reset.setVisibility(View.VISIBLE);
                            i++;
                            progressBar.setProgress(100);
                        }
                    }.start();
                }
            }, 0);

            btn_reset.setVisibility(View.INVISIBLE);
            return "Start";
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            mTimerRunning = true;
            btn_start.setText("Pause");

            mTimeLeft = values[0];
            int percentage = 100-(int)((mTimeLeft*100/(goaltime*60000)));
            progressBar.setProgress(percentage);

            int hours = (int) (mTimeLeft/(1000*60*60))%24;
            int minutes = (int) (mTimeLeft / (1000*60)) % 60;
            int seconds = (int) (mTimeLeft / 1000) % 60;

            String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours,minutes, seconds);

            tv_count_time.setText(timeLeftFormatted);
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }

    private void pauseTimer() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).child(start_date).child(title);

        int percentage = 100-(int)((mTimeLeft*100/(goaltime*60000)));
        progressBar.setProgress(percentage);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("time_status", mTimeLeft);
        hashMap.put("percent_status", percentage);

        String ratefor = String.format(Locale.getDefault(), "%01d", percentage);
        tv_accomplish_rate.setText(ratefor+"%");

        reference.updateChildren(hashMap);

        i-=2;
        mCoutDown.cancel();
        mTimerRunning = false;
        btn_start.setText("Start");
        btn_reset.setVisibility(View.VISIBLE);
    }

    private void resetTimer() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).child(start_date).child(title);
        mTimeLeft = goaltime*60000;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("time_status", mTimeLeft);
        hashMap.put("processed_time_status", mTimeLeft);
        hashMap.put("percent_status", 0);

        String ratefor = String.format(Locale.getDefault(), "%01d", 0);
        tv_accomplish_rate.setText(ratefor+"%");

        reference.updateChildren(hashMap);

        percent = 0;
        i=0;
        updateCountDownText();
        progressBar.setProgress(0);
        mTimerRunning = false;
        btn_reset.setVisibility(View.INVISIBLE);
        btn_start.setVisibility(View.VISIBLE);
    }

    public void updateCountDownText() {
        int hours = (int) (mTimeLeft/(1000*60*60))%24;
        int minutes = (int) (mTimeLeft / (1000*60)) % 60;
        int seconds = (int) (mTimeLeft / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours,minutes, seconds);

        tv_count_time.setText(timeLeftFormatted);
    }

    @Override
    public void onBackPressed() {
        if(mTimerRunning){
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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).child(start_date).child(title);

        processed_time_status = (int)mTimeLeft;

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("processed_time_status", processed_time_status);

        reference.updateChildren(hashMap);
    }
    public void rate(){



//        reference2.setValue(percentage);
    }

    public void time_status() { // 남은 시간.
//        reference3.setValue(mTimeLeft);
    }
}
