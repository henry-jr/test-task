package com.example.henryjr.rssfeedtest;

import android.graphics.drawable.Drawable;

public interface IImageListener {
        void onSuccess(Drawable drawable);
        void onFailed();
}
