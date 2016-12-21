package com.jone.jinux.tuwen;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by Jinux on 16/12/14.
 */

public class App extends Application {
    private static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    public static App getInstance() {
        return sInstance;
    }
}
