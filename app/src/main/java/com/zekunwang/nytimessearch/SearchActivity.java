package com.zekunwang.nytimessearch;

import com.zekunwang.nytimessearch.fragments.SettingDialogFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zekunwang.nytimessearch.activities.ArticleActivity;
import com.zekunwang.nytimessearch.adapters.ContactsAdapter;
import com.zekunwang.nytimessearch.listeners.EndlessRecyclerViewScrollListener;
import com.zekunwang.nytimessearch.models.Article;
import com.zekunwang.nytimessearch.models.Setting;
import com.zekunwang.nytimessearch.models.SpacesItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;


public class SearchActivity extends AppCompatActivity implements
        SettingDialogFragment.SettingDialogListener {

    // NYTimes API: 1fca3fa8b1c647ec80f7f0a27fc050c1
    // http://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=1fca3fa8b1c647ec80f7f0a27fc050c1
    @BindView(R.id.rvResults) RecyclerView rvResults;
    ArrayList<Article> articles;
    ContactsAdapter adapter;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    Setting setting;
    final String URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json";
    final String API_KEY = "1fca3fa8b1c647ec80f7f0a27fc050c1";
    RequestParams params;
    final int TAP_THRESHOLD = 4000;
    long timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        setupViews();

        // load sharedPreference
        setting = new Setting();
        // check internet connection
        if (!HelperMethods.isNetworkAvailable(this) || !HelperMethods.isOnline()) {
            Toast.makeText(this, "Internet Unavailable...", Toast.LENGTH_LONG).show();
        }
    }
    // double tap to scroll to top
    @OnClick(R.id.toolbar)
    public void onClick(View v) {
        long newTimeStamp = System.currentTimeMillis();
        if (newTimeStamp - timeStamp <= TAP_THRESHOLD && articles.size() != 0) {
            mStaggeredGridLayoutManager.scrollToPositionWithOffset(0, 0);
        }
        timeStamp = newTimeStamp;
    }

    private void setupViews() {
        articles = new ArrayList<>();

        // get width of current metric
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        double width = metrics.widthPixels * (1 - 4 * ContactsAdapter.RATIO);
        int spacing = (int)(width / 5) / 2;   // width / height = 0.66279
        // set vertical spacing and grid view padding dynamically
        rvResults.setPadding(spacing, spacing, spacing, spacing);
        adapter = new ContactsAdapter(this, articles);
        adapter.setOnItemClickListener(new ContactsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                // get position from RecyclerView and switch activity
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                intent.putExtra("article", articles.get(position));
                startActivity(intent);
            }
        });
        rvResults.setAdapter(adapter);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        rvResults.setLayoutManager(mStaggeredGridLayoutManager);
        // set RecycleView item divider width
        SpacesItemDecoration decoration = new SpacesItemDecoration(spacing);
        rvResults.addItemDecoration(decoration);
        // set animation
        //rvResults.setItemAnimator(new SlideInUpAnimator());
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
        if (!HelperMethods.isNetworkAvailable(this) || !HelperMethods.isOnline()) {
            Toast.makeText(this, "Internet Unavailable...", Toast.LENGTH_LONG).show();
            return;
        }
        adapter.clear();    // clear adapter data and start new search
        if (query == null || query.length() == 0) {
            rvResults.addOnScrollListener(null);
        } else {
            // update query params
            params = setting.getQuery();
            params.put("api-key", API_KEY);
            params.put("q", query); // search key words

            loadPage(0);
            rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(mStaggeredGridLayoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    loadPage(page);
                }
            });
        }
    }

    private void loadPage(int page) {
        // update page
        params.put("page", page);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(URL, params, new JsonHttpResponseHandler(){
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

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                    JSONArray errorResponse) {
                Toast.makeText(getApplicationContext(), "Data request failed...", Toast.LENGTH_LONG).show();
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

}
