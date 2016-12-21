package com.jone.jinux.tuwen.report;

import android.content.Context;

/**
 * Created by jinux on 16-12-20.
 */

public class ReportManager {
    private final Context mContext;
    private static ReportManager sInstance;

    public static ReportManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ReportManager.class) {
                if (sInstance == null) {
                    sInstance = new ReportManager(context);
                }
            }
        }
        return sInstance;
    }

    private ReportManager(Context context) {
        // init
        this.mContext = context;
    }


}
