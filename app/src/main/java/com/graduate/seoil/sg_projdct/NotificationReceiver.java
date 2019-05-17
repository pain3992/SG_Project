package com.graduate.seoil.sg_projdct;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        String message = intent.getStringExtra("toastMessage");
//        Toast.makeText(context,"goooood",Toast.LENGTH_LONG).show();

        //NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        //notificationManagerCompat.cancel(1);
        ((PlanInformationActivity)PlanInformationActivity.mContext).startStop();
    }
}
