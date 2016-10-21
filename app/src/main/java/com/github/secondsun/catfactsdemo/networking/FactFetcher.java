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

import com.github.secondsun.catfactsdemo.CatFacts;

/**
 * Created by secondsun on 10/21/16.
 */
public interface FactFetcher {

    void onResume();
    void onPause();
    void reset();
    void load(CatFacts activity);
    void init(CatFacts activity);
    void unload();
}
