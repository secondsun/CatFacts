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

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;

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

public class AsyncTaskbasedFactLoader implements FactFetcher {

    private final CatFacts activity;
    private ArrayList<String> data = new ArrayList<>();

    public AsyncTaskbasedFactLoader(CatFacts activity) {
        this.activity = activity;
    }

    AsyncTask<Void, Void, List<String>> task = makeTask();


    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void reset() {
        task = makeTask();
        data = new ArrayList<>();
    }


    private AsyncTask<Void, Void, List<String>> makeTask() {
        return new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... params) {
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

                    data = toReturn;

                    return toReturn;

                } catch (InterruptedException | IOException | JSONException e) {
                    ArrayList<String> toReturn = new ArrayList<>();
                    toReturn.add("Error:" + e.getMessage());
                    data = toReturn;
                    return toReturn;
                }
            }

            @Override
            protected void onPostExecute(List<String> strings) {
                super.onPostExecute(strings);
                if (activity != null) {
                    activity.displayFacts(strings);
                }
            }
        };

    }

    @Override
    public void load(final CatFacts activity) {
        if (task.getStatus().equals(AsyncTask.Status.PENDING)) {
            task.execute();
        } else {
            Snackbar.make(activity.getFab(), "AsyncTasks can only be run once.", Snackbar.LENGTH_LONG)
                    .setAction("Reset", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reset();
                        }
                    }).show();
        }
    }

    @Override
    public void init(CatFacts activity) {
        reset();
    }

    @Override
    public void unload() {

    }
}
