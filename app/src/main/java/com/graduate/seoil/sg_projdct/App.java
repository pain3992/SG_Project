package com.graduate.seoil.sg_projdct;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final  String CHANNEL_1_ID = "channel1";
    public static final  String CHANNEL_2_ID = "channel2";
    public static final  String CHANNEL_3_ID = "channel3";
    public static final  String CHANNEL_4_ID = "channel4";
    public static final  String CHANNEL_11_ID = "channel11";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "목표실행 알림",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("목표실행 알림");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "공지사항 알림",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel2.setDescription("공지사항 알림");

            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_3_ID,
                    "피드 알림",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel3.setDescription("피드 알림");

            NotificationChannel channel4 = new NotificationChannel(
                    CHANNEL_4_ID,
                    "목표실행중 알림",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel4.setDescription("목표 실행중 알림 입니다.");

            NotificationChannel channel11 = new NotificationChannel(
                    CHANNEL_11_ID,
                    "목표완료 알림",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel11.setDescription("목표 완료 알림 입니다.");

            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel1);
//            manager.createNotificationChannel(channel2);
//            manager.createNotificationChannel(channel3);
            manager.createNotificationChannel(channel4);
            manager.createNotificationChannel(channel11);
        }
    }
}
