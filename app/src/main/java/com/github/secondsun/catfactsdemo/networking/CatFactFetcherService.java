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

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.secondsun.catfactsdemo.CatFacts;
import com.github.secondsun.catfactsdemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CatFactFetcherService extends Service {

    public static final String FILTER_KEY = "CatFactService";
    public static final String RESET = "CatFactService.RESET";
    public static final String FACTS = "CatFactService.FACTS";
    public static final String LOAD = "CatFactService.START";
    public static final String UNLOAD = "CatFactService.STOP";
    private static final int ONGOING_NOTIFICATION_ID = 0x100;

    private ArrayList<String> data = new ArrayList<>();

    public CatFactFetcherService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new AsyncTask<Intent, Void, Void>() {

            @Override
            protected Void doInBackground(Intent... params) {
                if (params!= null && params.length > 0) {
                    onHandleIntent(params[0]);
                }
                return null;
            }
        }.execute(intent);
        return START_STICKY;
    }

    protected void onHandleIntent(Intent intent) {

        if (intent.getBooleanExtra(LOAD, false)) {

            CharSequence contentText;

            if (data.size() > 0) {
                contentText = "Data Loaded";
            } else {
                contentText = "Awaiting Data Load";
            }
            Intent notificationIntent = new Intent(this, CatFacts.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification = new Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("CatFact Fetcher").setContentText(contentText).setContentIntent(pendingIntent).build();
            startForeground(ONGOING_NOTIFICATION_ID, notification);
        } else if (intent.getBooleanExtra(UNLOAD, false)) {
            stopForeground(true);
        } else {
            if (intent.getBooleanExtra(RESET, false)) {
                data.clear();
                Intent notificationIntent = new Intent(this, CatFacts.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
                Notification notification = new Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("CatFact Fetcher").setContentText("Awaiting Data Load").setContentIntent(pendingIntent).build();
                startForeground(ONGOING_NOTIFICATION_ID, notification);
            }
            if (data.size() == 0) {
                try {
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


                } catch (InterruptedException | IOException | JSONException e) {
                    ArrayList<String> toReturn = new ArrayList<>();
                    toReturn.add("Error:" + e.getMessage());
                    data = toReturn;

                }
            }

            Intent notificationIntent = new Intent(this, CatFacts.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            Notification notification = new Notification.Builder(this).setSmallIcon(R.mipmap.ic_launcher).setContentTitle("CatFact Fetcher").setContentText("Data Available").setContentIntent(pendingIntent).build();
            startForeground(ONGOING_NOTIFICATION_ID, notification);

            sendData();
        }

    }

    private void sendData() {
        Intent resultIntent = new Intent(FILTER_KEY);
        resultIntent.putStringArrayListExtra(FACTS, data);
        sendBroadcast(resultIntent);
    }
}
