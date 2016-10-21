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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.github.secondsun.catfactsdemo.CatFacts;

import java.util.List;

import static com.github.secondsun.catfactsdemo.R.id.reset;

/**
 * Created by secondsun on 10/21/16.
 */

public class ServiceBasedFactFetcher implements FactFetcher {

    private final CatFacts activity;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (activity != null) {
                List<String> facts = intent.getStringArrayListExtra(CatFactFetcherService.FACTS);
                activity.displayFacts(facts);
            }
        }
    };

    private boolean reset = false;

    public ServiceBasedFactFetcher(CatFacts activity) {
        this.activity = activity;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {
        activity.unregisterReceiver(receiver);
    }

    @Override
    public void reset() {
        this.reset = true;
    }

    @Override
    public void load(CatFacts activity) {
        activity.registerReceiver(receiver, new IntentFilter(CatFactFetcherService.FILTER_KEY));
        Intent startIntent = new Intent(activity, CatFactFetcherService.class);
        startIntent.putExtra(CatFactFetcherService.RESET, reset);
        reset = false;
        activity.startService(startIntent);
    }

    @Override
    public void init(CatFacts activity) {
        activity.registerReceiver(receiver, new IntentFilter(CatFactFetcherService.FILTER_KEY));
        Intent startIntent = new Intent(activity, CatFactFetcherService.class);
        startIntent.putExtra(CatFactFetcherService.LOAD, true);
        activity.startService(startIntent);
    }

    @Override
    public void unload() {
        Intent startIntent = new Intent(activity, CatFactFetcherService.class);
        startIntent.putExtra(CatFactFetcherService.UNLOAD, true);
        activity.startService(startIntent);
    }
}
