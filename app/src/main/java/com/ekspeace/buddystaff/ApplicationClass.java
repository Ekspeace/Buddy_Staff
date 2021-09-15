package com.ekspeace.buddystaff;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.onesignal.OneSignal;


public class ApplicationClass extends Application {
    private static final String ONESIGNAL_APP_ID = "0aaead53-0dc0-4e59-a854-25779c6c3f58";
    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        FirebaseApp.initializeApp(this);
    }
}
