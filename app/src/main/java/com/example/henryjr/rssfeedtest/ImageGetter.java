package com.example.henryjr.rssfeedtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageGetter implements Html.ImageGetter {

    private TextView textView = null;
    private MainActivity mActivity = null;

    public ImageGetter(MainActivity mActivity, TextView target) {
        this.mActivity = mActivity;
        textView = target;
    }

    @Override
    public Drawable getDrawable(String source) {
        BitmapDrawableTemp drawable = new BitmapDrawableTemp();
        new ImageGetterTask(drawable).execute(source);
        return drawable;
    }

    private class BitmapDrawableTemp extends BitmapDrawable implements IImageListener {

        protected Drawable drawable;

        @Override
        public void draw(final Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, width, height);
            setBounds(0, 0, width, height);
            if (textView != null) {
                textView.setText(textView.getText());
            }
        }

        @Override
        public void onSuccess(Drawable drawable) {
            setDrawable(drawable);
        }

        @Override
        public void onFailed() {
            //nothing
        }
    }

    public class ImageGetterTask extends AsyncTask<String, Void, Bitmap> {
        private BitmapDrawableTemp bitmapTemp;
        private String url;

        public ImageGetterTask(BitmapDrawableTemp bitmapTemp) {
            this.bitmapTemp = bitmapTemp;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            url = params[0];
            if (mActivity.getBitmapFromMemCache(url) != null){
                return mActivity.getBitmapFromMemCache(url);
            }
            return downloadBitmap(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (bitmap != null) {
                mActivity.addBitmapToMemoryCache(url, bitmap);
                bitmapTemp.setDrawable(new BitmapDrawable(
                        textView.getContext().getResources(), bitmap));
            }
        }

        private Bitmap downloadBitmap(String url) {
            HttpURLConnection urlConnection = null;
            try {
                URL uri = new URL(url);
                urlConnection = (HttpURLConnection) uri.openConnection();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    return null;
                }

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (Exception e) {
                urlConnection.disconnect();
                Log.w(ImageGetterTask.class.getSimpleName(), "Error downloading image from " + url);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }
}