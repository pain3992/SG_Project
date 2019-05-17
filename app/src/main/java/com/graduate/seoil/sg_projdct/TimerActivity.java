package com.graduate.seoil.sg_projdct;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.graduate.seoil.sg_projdct.App.CHANNEL_1_ID;
import static com.graduate.seoil.sg_projdct.App.CHANNEL_2_ID;

public class TimerActivity extends AppCompatActivity {
    private NotificationManagerCompat notificationManager;
    private MediaSessionCompat mediaSession;
    TextView timer ;
    Button start, pause, reset;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler handler;
    int Seconds, Minutes, MilliSeconds ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        notificationManager = NotificationManagerCompat.from(this);
        mediaSession = new MediaSessionCompat(this,"tag");
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            NotificationChannel channel = new NotificationChannel("My","My",NotificationManager.IMPORTANCE_DEFAULT);
//
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel);
//        }
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"My")
//                .setContentTitle("This is my title")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setAutoCancel(true)
//                .setContentText("text");
//
//        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
//        manager.notify(999,builder.build());
        Intent intent = new Intent(this.getIntent());
        Intent push = new  Intent(this.getIntent());
        timer = (TextView)findViewById(R.id.tvTimer);
        start = (Button)findViewById(R.id.btStart);
        pause = (Button)findViewById(R.id.btPause);
        reset = (Button)findViewById(R.id.btReset);

        handler = new Handler() ;

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                sendOnChannel1();
                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);

                reset.setEnabled(false);

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendOnChannel2();
                TimeBuff += MillisecondTime;

                handler.removeCallbacks(runnable);

                reset.setEnabled(true);

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MillisecondTime = 0L ;
                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;
                MilliSeconds = 0 ;

                timer.setText("00:00:00");

            }
        });

    }
//    public void sendOnChannel1(){
//        Intent activityIntent = new Intent(this,TimerActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this,0,activityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Intent broadcastIntent = new Intent(this,NotificationReceiver.class);
//        broadcastIntent.putExtra("toastMessage","message");
//        PendingIntent actionintent = PendingIntent.getBroadcast(this,0,broadcastIntent,PendingIntent.FLAG_NO_CREATE);
//
//        Bitmap picture = BitmapFactory.decodeResource(getResources(),R.drawable.day_fri_none);
//
//        Notification notification = new NotificationCompat.Builder(this,CHANNEL_1_ID)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("Title")
//                .setContentText("message")
//                .setLargeIcon(picture)
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(picture)
//                        .bigLargeIcon(null))
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setColor(Color.BLUE)
//                .setContentIntent(contentIntent)
//                .setOnlyAlertOnce(true)
////                .setAutoCancel(true)
//                .setOngoing(true)
//                .addAction(R.mipmap.ic_launcher,"Toast",actionintent)
//                .build();
//
//        notificationManager.notify(1,notification);
//
//    }
    public void sendOnChannel2(){
        Bitmap artwork = BitmapFactory.decodeResource(getResources(),R.drawable.day_fri_none);
        notificationManager.cancel(1); //채널1 종료
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_2_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Title")
                .setContentText("message")
                .setLargeIcon(artwork)
                .addAction(R.mipmap.ic_launcher_round,"dislike1",null)
                .addAction(R.mipmap.ic_launcher,"dislike2",null)
                .addAction(R.mipmap.ic_launcher_round,"dislike3",null)
                .addAction(R.mipmap.ic_launcher,"dislike4",null)
                .addAction(R.mipmap.ic_launcher_round,"dislike5",null)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1, 2, 3)
                        .setMediaSession(mediaSession.getSessionToken()))
                .setSubText("sub text")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(2,notification);

    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            MilliSeconds = (int) (UpdateTime % 1000);

            timer.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", MilliSeconds));

            handler.postDelayed(this, 0);
        }

    };

}