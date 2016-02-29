package com.example.shon.boosttask8_navigation;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * @author Artem Samoshkin
 * @since 24/02/16
 *
 * Initialize Firebase with the application context.
 * This must happen before the client is used.
 */
public class AccApplication extends Application  {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}