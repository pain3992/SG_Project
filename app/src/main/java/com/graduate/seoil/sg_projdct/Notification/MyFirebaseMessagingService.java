package com.graduate.seoil.sg_projdct.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.graduate.seoil.sg_projdct.MainActivity;
import com.graduate.seoil.sg_projdct.R;

/**
 * Created by baejanghun on 22/05/2019.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
//    private static final String TAG="FirebaseMessageService";
//
//    //start receive_message
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        String messageBody = remoteMessage.getData().get("message");
//        String title = remoteMessage.getData().get("title");
//        sendNotification(messageBody, title);
//    }
//
//    private void sendNotification(String messageBody,String title) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(this, 0 /*request code*/, intent, PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(title+"님 의 메시지입니다.")
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager)
//                getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0,notificationBuilder.build());
//    }
}
