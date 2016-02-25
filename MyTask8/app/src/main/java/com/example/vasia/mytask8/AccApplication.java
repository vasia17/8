package com.example.vasia.mytask8;

import com.firebase.client.Firebase;

public class AccApplication extends android.app.Application  {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
