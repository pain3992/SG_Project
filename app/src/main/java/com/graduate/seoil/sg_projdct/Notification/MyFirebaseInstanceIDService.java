package com.graduate.seoil.sg_projdct.Notification;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by baejanghun on 22/05/2019.
 */
public class MyFirebaseInstanceIDService extends  FirebaseInstanceIDService{
    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }
}
