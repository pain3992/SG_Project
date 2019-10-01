package com.graduate.seoil.sg_projdct;

import android.app.Notification;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.session.MediaSessionCompat;
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
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableLayout;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Fragments.APIService;
import com.graduate.seoil.sg_projdct.Fragments.HomeFragment;
import com.graduate.seoil.sg_projdct.Model.Goal;
import com.graduate.seoil.sg_projdct.Notification.Client;
import com.graduate.seoil.sg_projdct.Notification.Data;
import com.graduate.seoil.sg_projdct.Notification.MyResponse;
import com.graduate.seoil.sg_projdct.Notification.Sender;
import com.graduate.seoil.sg_projdct.Notification.Token;
import com.kinda.alert.KAlertDialog;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.graduate.seoil.sg_projdct.App.CHANNEL_11_ID;
import static com.graduate.seoil.sg_projdct.App.CHANNEL_4_ID;

public class PlanInformationActivity extends AppCompatActivity {

    public static Context mContext;
    private NotificationManagerCompat notificationManager;
    private MediaSessionCompat mediaSession;
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
    AsyncUpdateGoal asyncUpdateGoal;
    // Timer
    private long timeCountInMilliSeconds = 1 * 60000;

    private KAlertDialog pDialog;

    private boolean onFinish_calledTime = false;

    APIService apiService;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    ProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private String str_userName, str_userImageURL;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_information);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        str_userName =  IndexActivity.spref.getString("str_userName", "default");
        str_userImageURL = IndexActivity.spref.getString("str_userImageURL", "default");

        mContext = this;
        notificationManager = NotificationManagerCompat.from(this);
        mediaSession = new MediaSessionCompat(this,"tag");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        pDialog = new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE);

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
                                    notificationManager.cancel(4);
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
            public void onClick(View v) {
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading");
                pDialog.setCancelable(false);
                pDialog.show();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("percent_status", 100);
                hashMap.put("processed_time_status", 0);
                hashMap.put("time_status", 0);
                reference.updateChildren(hashMap);
                progressBar.setProgress(100);
                notificationManager.cancel(4);
                finish();
            }
        });

        // 삭제
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.removeValue();
                notificationManager.cancel(4);
                finish();
            }
        });

    }

    public void sendOnChannel4_count(){
        Intent intent = getIntent();
        int no_plantime = intent.getIntExtra("goal_time",0);
        RemoteViews collapse = new RemoteViews(getPackageName(),R.layout.notification_sub);
        RemoteViews expand = new RemoteViews(getPackageName(),R.layout.notification_planinformation);
        Intent clickIntent = new Intent(this,NotificationReceiver.class);
        PendingIntent clickPendingIntent = PendingIntent.getBroadcast(this,0,clickIntent,0);
        expand.setTextViewText(R.id.text,tv_count_time.getText().toString());
        expand.setTextViewText(R.id.text2,hmsTimeFormatter(no_plantime*60000));
        expand.setOnClickPendingIntent(R.id.image2,clickPendingIntent);

        Intent activityIntent = new Intent(this,PlanInformationActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,activityIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_4_ID)

                .setSmallIcon(R.drawable.logo)
                .setCustomContentView(collapse)
                .setCustomBigContentView(expand)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .build();

        notificationManager.notify(4,notification);
    }

    public void sendOnChannel11(){
        Intent intent = getIntent();
        String tt = intent.getStringExtra("goal_title");

        Intent activityIntent = new Intent(this,IndexActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,activityIntent,PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_11_ID)

                .setSmallIcon(R.drawable.logo)
                .setContentTitle("목표가 완료 !")
                .setContentText(tt+"의 목표가 완료 되었어요")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();

        notificationManager.notify(11,notification);
    }
    public void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {
            sendOnChannel4_count();
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
            notificationManager.cancel(4);
        }
    }

    private void setConcentrate(int cnt) {
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
                sendOnChannel4_count();
            }

            @Override
            public void onFinish() {
                if (!onFinish_calledTime) {
                    onFinish_calledTime = true;
                    System.out.println("onFinished called?");
                    sendOnChannel11();
                    pDialog.setTitleText("계획을 완료했어요!")
                            .setContentText("확인 버튼을 눌러주세요!")
                            .setConfirmClickListener(new KAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(KAlertDialog kAlertDialog) {
                                    pDialog.dismissWithAnimation();
                                    finish();
                                }
                            }).show();

                    reference = FirebaseDatabase.getInstance().getReference("PushNotifications");
                    HashMap<String, Object> hashMap_push = new HashMap<>();
                    hashMap_push.put("uid", fuser.getUid());
                    hashMap_push.put("title", "'" + title + "' 실천!");
                    hashMap_push.put("content", "집중도 : '" + grade + "'");
                    hashMap_push.put("category", "목표");
                    hashMap_push.put("sender_name", str_userName);
                    hashMap_push.put("sender_url", str_userImageURL);
                    hashMap_push.put("timestamp", System.currentTimeMillis());

                    reference.child(fuser.getUid()).push().updateChildren(hashMap_push);

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
                    reference = FirebaseDatabase.getInstance().getReference("Goal").child(fuser.getUid()).child(start_date).child(title);
                    reference.updateChildren(hashMap);
                    notificationManager.cancel(4);
                }

//                finish();
            }
        }.start();
        countDownTimer.start();
    }

    private class AsyncUpdateGoal extends AsyncTask<Void, Void, Void> {
        private WeakReference<PlanInformationActivity> activityReference;

        AsyncUpdateGoal(PlanInformationActivity activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            PlanInformationActivity activity = activityReference.get();
            if (activity == null | activity.isFinishing())
                return;
        }

        @Override
        protected Void doInBackground(Void... voids) {


            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            PlanInformationActivity activity = activityReference.get();
            if (activity == null | activity.isFinishing())
                return;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            PlanInformationActivity activity = activityReference.get();
            if (activity == null | activity.isFinishing())
                return;

        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (asyncUpdateGoal.getStatus() == AsyncTask.Status.RUNNING) {
                asyncUpdateGoal.cancel(true);
            }
        } catch (Exception e) {
            Log.d("onDestory", e.toString());
        }
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
