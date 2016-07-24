package com.zekunwang.nytimessearch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zekunwang.nytimessearch.adapters.ArticleArrayAdapter;
import com.zekunwang.nytimessearch.models.Article;
import com.zekunwang.nytimessearch.models.Setting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import cz.msebera.android.httpclient.Header;


public class SearchActivity extends AppCompatActivity implements
        SettingDialogFragment.SettingDialogListener {

    // NYTimes API: 1fca3fa8b1c647ec80f7f0a27fc050c1
    // http://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=1fca3fa8b1c647ec80f7f0a27fc050c1
    @BindView(R.id.gvResults) GridView gvResults;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    Setting setting;
    final String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    RequestParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // set color for notification bar
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.btn_background_deep));

        ButterKnife.bind(this);
        setupViews();

        // load sharedPreference
        setting = new Setting();
        // check internet connection
        if (!isNetworkAvailable() || !isOnline()) {
            Toast.makeText(this, "Internet Unavailable...", Toast.LENGTH_LONG).show();
        }
    }

    private void setupViews() {
        articles = new ArrayList<>();

        // get width of current metric
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        double width = metrics.widthPixels * (1 - 4 * ArticleArrayAdapter.RATIO);
        int spacing = (int)(width / 5);   // width / height = 0.66279
        // set vertical spacing and grid view padding dynamically
        gvResults.setPadding(spacing, spacing, 0, spacing);
        gvResults.setVerticalSpacing(spacing);
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);
    }

    @OnItemClick(R.id.gvResults)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
        intent.putExtra("article", articles.get(position));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // add actionbar search listener
        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                onArticleSearch(query.trim());
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showSettingDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(final String query) {
        if (!isNetworkAvailable() || !isOnline()) {
            Toast.makeText(this, "Internet Unavailable...", Toast.LENGTH_LONG).show();
            return;
        }
        adapter.clear();    // clear adapter data and start new search
        if (query == null || query.length() == 0) {
            gvResults.setOnScrollListener(null);
        } else {
            // update query params
            params = setting.getQuery();
            params.put("api-key", "1fca3fa8b1c647ec80f7f0a27fc050c1");
            params.put("q", query); // search key words

            loadPage(0);
            gvResults.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to your AdapterView

                    loadPage(page);
                    // or customLoadMoreDataFromApi(totalItemsCount);
                    return true; // ONLY if more data is actually being loaded; false otherwise.
                }
            });
        }
    }

    private void loadPage(int page) {
        // update page
        params.put("page", page);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleResults = null;
                try {
                    articleResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(articleResults));  // = articles.addALl() + notification
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showSettingDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SettingDialogFragment settingDialogFragment = SettingDialogFragment.newInstance(setting);
        settingDialogFragment.show(fm, "fragment_setting");
    }

    @Override
    public void onFinishSettingDialog(Setting setting) {
        this.setting = setting;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double dpToPx(double dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        double px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public double pxToDp(double px) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        double dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }
}
