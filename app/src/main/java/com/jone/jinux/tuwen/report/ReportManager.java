package com.jone.jinux.tuwen.report;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.jone.jinux.tuwen.App;
import com.jone.jinux.tuwen.BuildConfig;
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
        analytics.enableAutoActivityReports(App.getInstance());
        analytics.setLocalDispatchPeriod(30);
        analytics.setDryRun(true);
        analytics.initialize();
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        mTracker = analytics.newTracker(R.xml.global_tracker);
        mTracker.setAppVersion(BuildConfig.VERSION_NAME + "_" + BuildConfig.BUILD_TYPE);
        mTracker.setAppId(BuildConfig.APPLICATION_ID);
        mTracker.setAppName(mContext.getString(R.string.app_name));
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

        if (label != null && !"".equals(label)) {
            builder.setLabel(label);
        }

        mTracker.send(builder.build());


        // umeng
        MobclickAgent.onEvent(mContext, action);
    }


    public void reportScreen(String name) {
        mTracker.setScreenName(name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        MobclickAgent.onPageStart(name);
        MobclickAgent.onResume(mContext);
    }

    public void reportScreenEnd(String name) {
        MobclickAgent.onPageEnd(name);
        MobclickAgent.onPause(mContext);
    }
}
