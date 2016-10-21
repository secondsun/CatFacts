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

import com.github.secondsun.catfactsdemo.R;

/**
 * Created by secondsun on 10/21/16.
 */
public enum FetchOption {
    LOADER(R.id.use_loader), ASYNC_TASK(R.id.use_async), SERVICE(R.id.use_service);

    public final int id;


    FetchOption(int id) {
        this.id = id;
    }

    public static FetchOption fromId(int id) {
        switch (id) {
            case R.id.use_loader:
                return LOADER;
            case R.id.use_async:
                return ASYNC_TASK;
            case R.id.use_service:
                return SERVICE;
            default:
                throw new RuntimeException(id + " id not found");
        }
    }
}
