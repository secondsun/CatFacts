/**
 * Copyright 2016 Summers Pittman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.secondsun.catfactsdemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.secondsun.catfactsdemo.networking.AsyncTaskbasedFactLoader;
import com.github.secondsun.catfactsdemo.networking.FactFetcher;
import com.github.secondsun.catfactsdemo.networking.LoaderBasedFactFetcher;
import com.github.secondsun.catfactsdemo.networking.ServiceBasedFactFetcher;
import com.github.secondsun.catfactsdemo.settings.FetchOption;
import com.github.secondsun.catfactsdemo.settings.SettingsUtil;

import java.util.ArrayList;
import java.util.List;

public class CatFacts extends AppCompatActivity {


    private FetchOption fetchOption = FetchOption.LOADER;
    private ListView factsView;
    private ArrayAdapter<String> factsAdapter;
    private FactFetcher fetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cat_facts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            fetcher.load(CatFacts.this);
            }
        });
        factsView = (ListView) findViewById(R.id.facts_view);
        factsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        factsView.setAdapter(factsAdapter);
        this.fetchOption = SettingsUtil.loadFetchOption(this);
        fetcher = getFetcher();

    }

    private FactFetcher getFetcher() {
        FactFetcher fetcher;
        switch (fetchOption) {
            case ASYNC_TASK:
                fetcher =  new AsyncTaskbasedFactLoader(this);
                break;
            case LOADER:
                fetcher =  new LoaderBasedFactFetcher(this);
                break;
            case SERVICE:
                fetcher =  new ServiceBasedFactFetcher(this);
                break;
            default:
                this.fetchOption = FetchOption.LOADER;
                fetcher =  new LoaderBasedFactFetcher(this);
                break;
        }
        fetcher.init(this);
        return fetcher;
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetcher.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SettingsUtil.storeFetchOption(this, fetchOption);
        fetcher.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cat_facts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        displayFacts(new ArrayList<String>());

        if (id == R.id.use_loader) {
            this.fetchOption = FetchOption.LOADER;
            fetcher.unload();
            this.fetcher = getFetcher();
            return true;
        } else if (id == R.id.use_async) {
            this.fetchOption = FetchOption.ASYNC_TASK;
            fetcher.unload();
            this.fetcher = getFetcher();
            return true;
        } else if (id == R.id.use_service) {
            this.fetchOption = FetchOption.SERVICE;
            fetcher.unload();
            this.fetcher = getFetcher();
            return true;
        } else if (id == R.id.reset) {
            this.fetcher.reset();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayFacts(List<String> data) {
        factsAdapter.clear();
        factsAdapter.addAll(data);
        factsAdapter.notifyDataSetChanged();
    }

    public View getFab() {
        return findViewById(R.id.fab);
    }
}
