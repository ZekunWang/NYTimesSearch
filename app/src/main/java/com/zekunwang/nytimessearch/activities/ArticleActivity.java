package com.zekunwang.nytimessearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zekunwang.nytimessearch.HelperMethods;
import com.zekunwang.nytimessearch.R;
import com.zekunwang.nytimessearch.models.Article;
import com.zekunwang.nytimessearch.models.Doc;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ArticleActivity extends AppCompatActivity {

    @BindView(R.id.wvArticle) WebView webView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    NestedScrollView nestedScrollView;
    ActionBar actionBar;
    final float SCALE_RATE = 1.2f;
    float scaleX, scaleY;
    final int TAP_THRESHOLD = 500;
    long timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        // add navigation up button
        actionBar.setDisplayHomeAsUpEnabled(true);

        Doc doc = Parcels.unwrap(getIntent().getParcelableExtra("doc"));
        // avoid popping a new activity for the link
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        if (doc.getWebUrl() != null) {
            webView.loadUrl(doc.getWebUrl());
        }
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        // get default scale
        scaleX = nestedScrollView.getScaleX();
        scaleY = nestedScrollView.getScaleY();
    }

    // in/out fullscreen
    @OnClick({R.id.fab, R.id.zoomIn, R.id.zoomOut, R.id.toolbar})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (actionBar.isShowing()) {
                    getWindow().getDecorView()
                            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                    actionBar.hide();
                    fab.setImageResource(R.drawable.ic_fullscreen_exit);
                } else {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    actionBar.show();
                    fab.setImageResource(R.drawable.ic_fullscreen);
                }
                break;
            case R.id.zoomIn:
                scaleX *= SCALE_RATE;
                scaleY *= SCALE_RATE;
                webView.setScaleX(scaleX);
                webView.setScaleY(scaleY);
                break;
            case R.id.zoomOut:
                scaleX /= SCALE_RATE;
                scaleY /= SCALE_RATE;
                webView.setScaleX(scaleX);
                webView.setScaleY(scaleY);
                break;
            case R.id.toolbar:
                long newTimeStamp = System.currentTimeMillis();
                if (newTimeStamp - timeStamp <= TAP_THRESHOLD) {
                    nestedScrollView.scrollTo(0, 0);
                }
                timeStamp = newTimeStamp;
        }
    }

    @Override
    public void onBackPressed() {
        // check if webView can go back
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_article, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        ShareActionProvider miShare = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        // get reference to WebView
        WebView wvArticle = (WebView) findViewById(R.id.wvArticle);
        // pass in the URL currently being used by the WebView
        shareIntent.putExtra(Intent.EXTRA_TEXT, wvArticle.getUrl());

        miShare.setShareIntent(shareIntent);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
