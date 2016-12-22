package com.jone.jinux.tuwen.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private static final int REQUEST_GET_PIC = 923;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initEdgeLenth();
    }

    @Override
    protected String getReportScreenName() {
        return ReportConstants.SCREEN_MAIN;
    }

    private void initEdgeLenth() {
        View v = findViewById(R.id.bg);
    }

    public void onShareClick(View view) {
        final View v = findViewById(R.id.bg);

        rx.Observable.create(new rx.Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                Utils.log("onSubscribe : " + Thread.currentThread().getName());
                Bitmap bitmap = v.getDrawingCache();
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
                Utils.log("onNext : "+ Thread.currentThread().getName());
                v.destroyDrawingCache();
                Utils.log("url = " + s);
            }
        });

        reportClick(ReportConstants.ACTION_SHARE_CLICK, null);
    }

    private String saveBitmap(Bitmap bitmap) {
        String path = Utils.getSavePath(Utils.getNewName());
        Utils.log("new file name = " + path);

        String url = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,
                getString(R.string.app_name),
                getString(R.string.app_name) + " created");

        return url;
    }

    private int[] colors = new int[]{
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.CYAN,
    };
    int colorIndex = 0;

    public void onSeClick(View view) {
        colorIndex++;
        if (colorIndex == colors.length) {
            colorIndex = 0;
        }
        findViewById(R.id.bg).setBackgroundDrawable(new ColorDrawable(colors[colorIndex]));
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
                                findViewById(R.id.bg).setBackgroundDrawable(resource);
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
