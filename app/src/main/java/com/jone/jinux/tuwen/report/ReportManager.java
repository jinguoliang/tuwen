package com.jone.jinux.tuwen.report;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jone.jinux.tuwen.App;
import com.jone.jinux.tuwen.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by jinux on 16-12-20.
 */

public class ReportManager {
    private final Context mContext;
    private static ReportManager sInstance;
    private final Tracker mTracker;

    public static ReportManager getInstance() {
        if (sInstance == null) {
            synchronized (ReportManager.class) {
                if (sInstance == null) {
                    sInstance = new ReportManager();
                }
            }
        }
        return sInstance;
    }

    private ReportManager() {
        // init
        this.mContext = App.getInstance();

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(mContext);
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        mTracker = analytics.newTracker(R.xml.global_tracker);
    }

    public void reportEvent(String category, String action) {
        reportEvent(category, action, null);
    }

    public void reportEvent(String category, String action, String label) {
        HitBuilders.EventBuilder builder =
                new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setNonInteraction(false);

        if (!TextUtils.isEmpty(label)) {
            builder.setLabel(label);
        }

        mTracker.send(builder.build());
    }


    public void reportScreen(String name) {
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        MobclickAgent.onPageStart(name);
        MobclickAgent.onResume(mContext);
    }
}
