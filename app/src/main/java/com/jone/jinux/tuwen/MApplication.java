package com.jone.jinux.tuwen;

import android.app.Application;

/**
 * Created by Jinux on 16/12/14.
 */

public class MApplication extends Application {
    private static MApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static MApplication getInstance() {
        return sInstance;
    }
}
