package com.jone.jinux.tuwen.base;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jone.jinux.tuwen.MApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by jinux on 16-12-19.
 */

public abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getReportScreenName());
        MApplication application = (MApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName(getReportScreenName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        MobclickAgent.onResume(this);
    }

    protected abstract String getReportScreenName();

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getReportScreenName());
        MobclickAgent.onPause(this);
    }
}
