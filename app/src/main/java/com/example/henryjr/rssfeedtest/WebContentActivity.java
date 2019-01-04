package com.example.henryjr.rssfeedtest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra("title"));
        WebView webview = findViewById(R.id.web);
        webview.loadData(getIntent().getStringExtra("content"), "text/html", "UTF-8");
        webview.setWebViewClient(new WebViewClient());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String link = getIntent().getStringExtra("link");
        if (TextUtils.isEmpty(link)) return true;

        if (id == R.id.action_share) {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/html");
            share.putExtra(Intent.EXTRA_TEXT, link);
            startActivity(Intent.createChooser(share, getString(R.string.share)));
            return true;
        } else if (id == R.id.action_open_browser) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(link));
            startActivity(i);
            return true;
        } else if (id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
