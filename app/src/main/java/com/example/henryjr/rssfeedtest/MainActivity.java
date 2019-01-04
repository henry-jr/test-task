package com.example.henryjr.rssfeedtest;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.LruCache;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IFeedListener<FeedModel> {

    private RecyclerView mRecyclerView;
    private EditText mEditText;
    private Button mFetchFeedButton;
    private SwipeRefreshLayout mSwipeLayout;

    public LruCache<String, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = findViewById(R.id.recyclerView);
        mEditText = findViewById(R.id.rssFeedEditText);
        mFetchFeedButton = findViewById(R.id.fetchFeedButton);
        mSwipeLayout = findViewById(R.id.swipeRefreshLayout);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mFetchFeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSwipeLayout.setRefreshing(true);
                new RetrieveFeedTask(MainActivity.this)
                        .execute(mEditText.getText().toString().trim());
            }
        });
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMemoryCache.evictAll();
                new RetrieveFeedTask(MainActivity.this)
                        .execute(mEditText.getText().toString().trim());
            }
        });
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    @Override
    public void onSuccess(List<FeedModel> list) {
        mSwipeLayout.setRefreshing(false);
        mRecyclerView.setAdapter(new FeedAdapter(MainActivity.this, list));
    }

    @Override
    public void onFailed() {
        mSwipeLayout.setRefreshing(false);
        Toast.makeText(MainActivity.this, R.string.invalid_rss_url, Toast.LENGTH_LONG).show();
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }
}
