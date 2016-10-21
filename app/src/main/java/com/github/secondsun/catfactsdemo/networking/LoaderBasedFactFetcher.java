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
package com.github.secondsun.catfactsdemo.networking;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;

import com.github.secondsun.catfactsdemo.CatFacts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by secondsun on 10/21/16.
 */

public class LoaderBasedFactFetcher implements LoaderManager.LoaderCallbacks<List<String>>, FactFetcher {

    public static int LOADER_ID = 0x123;
    private final CatFacts activity;
    private HttpCatLoader loader;

    public LoaderBasedFactFetcher(CatFacts activity) {
        this.activity = activity;
    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        return loader = new HttpCatLoader(activity);
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
        activity.displayFacts(data);
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {
        activity.displayFacts(new ArrayList<String>());
    }

    @Override
    public void onResume() {
        Loader<Object> localLoader = activity.getLoaderManager().getLoader(LOADER_ID);
        if (localLoader != null && !localLoader.isStarted()) {
            localLoader.startLoading();
        } if (localLoader != null ) {
            activity.displayFacts(((HttpCatLoader) (Loader<?>) activity.getLoaderManager().getLoader(LOADER_ID)).data);
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void reset() {
        if (activity.getLoaderManager().getLoader(LOADER_ID) != null) {
            ((HttpCatLoader) (Loader<?>) activity.getLoaderManager().getLoader(LOADER_ID)).data.clear();
        }
    }

    @Override
    public void load(CatFacts activity) {

        final Loader<List<String>> localLoader = activity.getLoaderManager().initLoader(LoaderBasedFactFetcher.LOADER_ID, Bundle.EMPTY, this);
        localLoader.startLoading();


    }

    @Override
    public void init(CatFacts activity) {
        activity.getLoaderManager();
    }

    @Override
    public void unload() {

    }

    private static class HttpCatLoader extends AsyncTaskLoader<List<String>> {

        private ArrayList<String> data = new ArrayList<>();

        public HttpCatLoader(Context context) {
            super(context.getApplicationContext());
        }

        @Override
        public List<String> loadInBackground() {
            try {

                if (data != null && data.size() > 0) {
                    return data;
                }

                Thread.sleep(Constants.DELAY);

                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(Constants.CAT_FACTS_URL)
                        .build();

                Response response = client.newCall(request).execute();

                JSONObject responseJson = new JSONObject(response.body().string());
                JSONArray facts = responseJson.getJSONArray("facts");

                ArrayList<String> toReturn = new ArrayList<>(facts.length());

                for (int i = 0; i < facts.length(); i++) {
                    toReturn.add(facts.getString(i));
                }

                this.data = toReturn;

                return toReturn;

            } catch (InterruptedException | IOException | JSONException e) {
                ArrayList<String> toReturn = new ArrayList<>();
                toReturn.add("Error:" + e.getMessage());
                this.data = toReturn;
                return toReturn;
            }

        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (data != null && data.size() > 0) {
                deliverResult(data);
            } else {
                forceLoad();
            }

        }

    }

}
