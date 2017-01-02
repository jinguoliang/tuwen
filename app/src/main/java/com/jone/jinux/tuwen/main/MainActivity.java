package com.jone.jinux.tuwen.main;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListPopupWindow;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jone.jinux.tuwen.R;
import com.jone.jinux.tuwen.base.BaseActivity;
import com.jone.jinux.tuwen.base.Utils;
import com.jone.jinux.tuwen.report.ReportConstants;
import com.jone.jinux.tuwen.report.ReportManager;

import java.io.IOException;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_GET_PIC = 923;
    int colorIndex = 0;
    private int[] colors = new int[]{
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.CYAN,
    };
    private JOneCanvas mCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initEdgeLenth();

        mCanvas = (JOneCanvas)findViewById(R.id.bg);
    }

    @Override
    protected String getReportScreenName() {
        return ReportConstants.SCREEN_MAIN;
    }

    private void initEdgeLenth() {
        final View v = findViewById(R.id.bg);
        v.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
                params.width = v.getWidth();
                params.height = v.getWidth();
                v.setLayoutParams(params);
            }
        });
    }

    public void onShareClick(View view) {
        // 获取bitmap
        ViewGroup v = (ViewGroup) findViewById(R.id.bg);
        final Bitmap bitmap = v.getDrawingCache();

        final ListPopupWindow popupWindow = new ListPopupWindow(this);
        popupWindow.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new String[]{"分享", "用作桌面", "用作屏保", "只保存"}));
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        share(bitmap);
                        popupWindow.dismiss();
                        reportClick(ReportConstants.ACTION_SHARE_CLICK, "share");
                        break;
                    case 1:
                        setToDesktop(bitmap);
                        popupWindow.dismiss();
                        reportClick(ReportConstants.ACTION_SHARE_CLICK, "desktop");
                        break;
                    case 2:
                        setToLockScreen(bitmap);
                        popupWindow.dismiss();
                        reportClick(ReportConstants.ACTION_SHARE_CLICK, "lockscreen");
                        break;
                    case 3:
                        save(bitmap, null);
                        popupWindow.dismiss();
                        reportClick(ReportConstants.ACTION_SHARE_CLICK, "desktop");
                        break;
                    default:
                }
            }
        });
        popupWindow.setAnchorView(view);
        popupWindow.show();

        reportClick(ReportConstants.ACTION_SHARE_CLICK, null);
    }

    interface OnSavedCallback {
        void onSaved(String uri);
    }

    private void save(final Bitmap bitmap, final OnSavedCallback callback) {
        rx.Observable.create(new rx.Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                Utils.log("onSubscribe : " + Thread.currentThread().getName());
                if (bitmap == null) {
                    subscriber.onError(null);
                    return;
                }

                String url = saveBitmap(bitmap);
                subscriber.onNext(url);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Utils.toast("save successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Utils.toast("can't get the view cache");
                    }

                    @Override
                    public void onNext(String s) {
                        Utils.log("url = " + s);
                        if (callback != null) {
                            callback.onSaved(s);
                        }
                    }
                });
    }

    private void setToLockScreen(Bitmap bitmap) {
            WallpaperManager mWallManager = WallpaperManager.getInstance(this);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mWallManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setToDesktop(Bitmap bitmap) {
        WallpaperManager manager = WallpaperManager.getInstance(this);
        try {
            manager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.toast("设置壁纸失败");
        }
    }

    private void share(Bitmap bitmap) {
        // 先保存
        save(bitmap, new OnSavedCallback() {
            @Override
            public void onSaved(String uri) {
                // 保存完分享
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_SUBJECT, "TuWen's Picture");
                intent.putExtra(Intent.EXTRA_TEXT, "wa just test");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getTitle()));
            }
        });
    }

    private String saveBitmap(final Bitmap bitmap) {
        final String path = Utils.getSavePath(Utils.getNewName());
        Utils.log("new file name = " + path);
//
//        OutputStream out = null;
//        try {
//            out = new BufferedOutputStream(new FileOutputStream(new File(path)));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        String uri = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Tuwen", "tuwen's work");
        return uri;
    }

    public void onSeClick(View view) {
        colorIndex++;
        if (colorIndex == colors.length) {
            colorIndex = 0;
        }
        mCanvas.setPicture(new ColorDrawable(colors[colorIndex]));
        reportClick(ReportConstants.ACTION_COLOR_CLICK, null);
    }

    public void onTuClick(View view) {
        final ListPopupWindow popupWindow = new ListPopupWindow(this);
        popupWindow.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new String[]{"本地", "网络"}));
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        openPictureOnDevice();
                        popupWindow.dismiss();
                        reportClick(ReportConstants.ACTION_PICTURE_CLICK, "local");
                        break;
                    case 1:
                        openBeautifulPic();
                        popupWindow.dismiss();
                        reportClick(ReportConstants.ACTION_PICTURE_CLICK, "network");
                        break;
                    default:
                }
            }
        });
        popupWindow.setAnchorView(view);
        popupWindow.show();
    }

    private void openBeautifulPic() {
        Intent intent = new Intent(this, PicturePreviewActivity.class);
        intent.putExtra(PicturePreviewActivity.LOAD_PIC_MEDTHOD, PictureLoader.class.getName());
        startActivityForResult(intent, REQUEST_GET_PIC);
    }

    private void openPictureOnDevice() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GET_PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GET_PIC) {
            if (resultCode == RESULT_OK) {
                Glide.with(MainActivity.this)
                        .load(data.getData())
                        .error(R.mipmap.ic_launcher)
                        .placeholder(R.mipmap.ic_launcher)
                        .skipMemoryCache(false)
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                mCanvas.setPicture(resource);
                            }
                        });
            } else {
                Utils.toast("Not selected");
            }
        }
    }

    private void reportClick(String action, String label) {
        ReportManager.getInstance().reportEvent(ReportConstants.CATEGORY_MAIN, action, label);
    }
}
