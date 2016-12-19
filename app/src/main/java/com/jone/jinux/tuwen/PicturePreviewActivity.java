package com.jone.jinux.tuwen;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.polites.android.GestureImageView;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by Jinux on 16/12/15.
 */
public class PicturePreviewActivity extends Activity {
    public static final String LOAD_PIC_MEDTHOD = "load_pic_method";

    PictureLoader mPicLoader;

    ViewPager mpagers;
    private PagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        mpagers = (ViewPager) findViewById(R.id.pagers);

        Intent intent = getIntent();
        if (intent != null) {
            String loadPicMethod = intent.getStringExtra(LOAD_PIC_MEDTHOD);
            if (TextUtils.isEmpty(loadPicMethod)) {
                Utils.toast("no load picture method");
                finish();
                return;
            }

            try {
                Class<PictureLoader> clazz = (Class<PictureLoader>) Class.forName(loadPicMethod);
                mPicLoader = clazz.getDeclaredConstructor().newInstance();
                String baseUrl = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/10/" + Math.round(Math.random() * 20);
                mPicLoader.load(baseUrl, new PictureLoader.OnResult<List<String>>() {
                    @Override
                    public void onResult(final List<String> result) {
                        mpagers.setAdapter(new PagerAdapter() {
                            @Override
                            public int getCount() {
                                return result.size();
                            }

                            @Override
                            public boolean isViewFromObject(View view, Object object) {
                                return view == object;
                            }

                            @Override
                            public Object instantiateItem(ViewGroup container, int position) {
                                GestureImageView view = new GestureImageView(getBaseContext());
                                view.setOnClickListener(new MOnClickListener(result.get(position)));
                                Glide.with(PicturePreviewActivity.this)
                                        .load(result.get(position))
                                        .error(R.mipmap.ic_launcher)
                                        .placeholder(R.mipmap.ic_launcher)
                                        .skipMemoryCache(false)
                                        .into(view);
                                container.addView(view);
                                return view;
                            }

                            @Override
                            public void destroyItem(ViewGroup container, int position, Object object) {
                                container.removeView((View) object);
                            }
                        });
                    }
                });
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Utils.toast("no load picture class");
                finish();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private class MOnClickListener implements View.OnClickListener {
        private final String mUrl;

        public MOnClickListener(String s) {
            mUrl = s;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setData(Uri.parse(mUrl));
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
