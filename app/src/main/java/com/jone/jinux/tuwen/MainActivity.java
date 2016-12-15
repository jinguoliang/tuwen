package com.jone.jinux.tuwen;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListPopupWindow;

import com.jone.jinux.tuwen.databinding.ActivityMainBinding;

import java.io.FileNotFoundException;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_GET_PIC = 923;

    private ActivityMainBinding mDataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mDataBinding.setBgColor(Color.CYAN);
        initEdgeLenth();
    }

    private void initEdgeLenth() {
        View v = findViewById(R.id.bg);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) v.getLayoutParams();
        params.width = Utils.getScreenWidth(this);
        params.width = params.height;
        v.setLayoutParams(params);
    }

    public void onShareClick(View view) {
        final View v = findViewById(R.id.bg);

        rx.Observable.create(new rx.Observable.OnSubscribe<String>(){

            @Override
            public void call(Subscriber<? super String> subscriber) {
                Utils.log("onSubscribe : "+ Thread.currentThread().getName());
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
        mDataBinding.setBgColor(colors[colorIndex]);
    }

    public void onTuClick(View view) {
        ListPopupWindow popupWindow = new ListPopupWindow(this);
        popupWindow.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new String[]{"本地", "网络"}));
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        openPictureOnDevice();
                        break;
                    case 1:
                        openBeautifulPic();
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
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                    findViewById(R.id.bg).setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Utils.toast("Open Picture failed");
                }
            } else {
                Utils.toast("Not selected");
            }
        }
    }
}
