package com.jone.jinux.tuwen.base;

import android.app.Activity;
import android.os.Bundle;

import com.jone.jinux.tuwen.report.ReportManager;

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
        ReportManager.getInstance().reportScreen(getReportScreenName());
    }

    protected abstract String getReportScreenName();

    @Override
    public void onPause() {
        super.onPause();
        ReportManager.getInstance().reportScreenEnd(getReportScreenName());
    }
}
