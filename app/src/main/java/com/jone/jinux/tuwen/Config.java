package com.jone.jinux.tuwen;

import android.graphics.Bitmap;

/**
 * Created by Jinux on 16/12/14.
 */

public class Config {
    int color;
    Bitmap bitmap;
    String text;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
