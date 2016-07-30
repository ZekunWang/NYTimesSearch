package com.zekunwang.nytimessearch.activities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.zekunwang.nytimessearch.HelperMethods;
import com.zekunwang.nytimessearch.R;
import com.zekunwang.nytimessearch.SearchActivity;
import com.zekunwang.nytimessearch.adapters.ContactsAdapter;
import com.zekunwang.nytimessearch.listeners.EndlessRecyclerViewScrollListener;
import com.zekunwang.nytimessearch.models.Article;
import com.zekunwang.nytimessearch.models.Byline;
import com.zekunwang.nytimessearch.models.Doc;
import com.zekunwang.nytimessearch.models.SpacesItemDecoration;

import org.parceler.Parcels;

import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cz.msebera.android.httpclient.Header;


public class ArticleActivity extends AppCompatActivity {

    @BindView(R.id.wvArticle) WebView webView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.nestedScrollView) NestedScrollView nestedScrollView;

    Article article;
    LinearLayoutManager mLinearLayoutManager;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    BottomSheetBehavior behavior;
    RequestParams params;

    ActionBar actionBar;
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

        article = Parcels.unwrap(getIntent().getParcelableExtra("article"));

        // avoid popping a new activity for the link
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        if (article.webUrl != null) {
            webView.loadUrl(article.webUrl);

            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setSupportZoom(true);
        }
    }

    // in/out fullscreen
    @OnClick({R.id.fab, R.id.toolbar})
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
