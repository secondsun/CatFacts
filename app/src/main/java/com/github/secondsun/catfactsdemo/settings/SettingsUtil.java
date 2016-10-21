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
package com.github.secondsun.catfactsdemo.settings;

import android.content.Context;
import android.content.SharedPreferences;

import com.github.secondsun.catfactsdemo.R;

/**
 * Created by secondsun on 10/21/16.
 */

public final class SettingsUtil {

    private static final String SETTINGS = "SettingsUtil.SETTINGS";
    private static final String FETCH_OPTION_ID_KEY = "SettingsUtil.FETCH_OPTION_ID_KEY";

    private SettingsUtil() {

    }

    public static FetchOption loadFetchOption(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        int id = prefs.getInt(FETCH_OPTION_ID_KEY, R.id.use_loader);
        return FetchOption.fromId(id);
    }

    public static void storeFetchOption(Context context, FetchOption option) {

        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        prefs.edit().putInt(FETCH_OPTION_ID_KEY, option.id).commit();

    }

}
