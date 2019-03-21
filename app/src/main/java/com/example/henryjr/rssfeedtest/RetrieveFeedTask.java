package com.example.henryjr.rssfeedtest;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.webkit.URLUtil;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


public class RetrieveFeedTask extends AsyncTask<String, Void, List<FeedModel>> {
    private WeakReference<MainActivity> mParentActivity = null;

    public RetrieveFeedTask(MainActivity mParentActivity) {
        this.mParentActivity = new WeakReference<>(mParentActivity);
    }

    @Override
    protected List<FeedModel> doInBackground(String... urls) {
        String urlLink = urls[0];
        if (TextUtils.isEmpty(urlLink))
            return null;

        try {
            if (!urlLink.startsWith("http://") && !urlLink.startsWith("https://"))
                urlLink = "http://" + urlLink;

            if (!URLUtil.isValidUrl(urlLink))
                return null;

            URL url = new URL(urlLink);
            InputStream inputStream = url.openConnection().getInputStream();
            return parseFeed(inputStream);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<FeedModel> list) {
        if (mParentActivity.get() != null) {
            if (list != null) {
                mParentActivity.get().onSuccess(list);
            } else {
                mParentActivity.get().onFailed();
            }
        }
    }

    private List<FeedModel> parseFeed(InputStream inputStream) throws XmlPullParserException,
            IOException {
        String title = null;
        String description = null;
        String link = null;
        boolean isItem = false;
        List<FeedModel> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if (name == null)
                    continue;

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                }

                if (title != null && description != null && link != null) {
                    if (isItem) {
                        FeedModel item = new FeedModel(title, description, link);
                        items.add(item);
                    }

                    title = null;
                    description = null;
                    link = null;
                    isItem = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }
    }
}