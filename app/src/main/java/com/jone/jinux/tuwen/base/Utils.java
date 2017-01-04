package com.jone.jinux.tuwen.base;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.jone.jinux.tuwen.App;

import java.io.File;

/**
 * Created by Jinux on 16/12/14.
 */

public class Utils {
    private static final String SUFFIX_PNG = ".png";
    private static final String SAVE_DIR = "tuwen";

    public static int getScreenWidth(Context c) {
        WindowManager mWm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        mWm.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getScreenHeight(Context c) {
        WindowManager mWm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        mWm.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static String getNewName() {
        String name = "" + System.currentTimeMillis() + SUFFIX_PNG;
        return name;
    }

    public static String getSavePath(String name) {
        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!picDir.isFile()) {
            picDir.mkdir();
        }

        if (!picDir.isDirectory()) {
            picDir.delete();
            picDir.mkdir();
        }

        return  picDir.getAbsolutePath() + File.separator + name;
    }

    public static void log(String s) {
        Log.d("JOne", s);
    }

    public static void toast(String s) {
        Toast.makeText(App.getInstance(), s, Toast.LENGTH_LONG).show();
    }

    public static void showSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) App.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    public static void hideSoftInput(IBinder windowToken) {
        InputMethodManager imm = (InputMethodManager) App.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(windowToken, 0);
        }
    }

    public static boolean isShowSoftInput(Activity activity) {
        InputMethodManager imm = (InputMethodManager) App.getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            return imm.isActive(activity.getCurrentFocus());
        }
        return false;
    }

}
