/*
 * Copyright 2015 Hippo Seven
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

package com.hippo.httpclient;

import java.net.HttpURLConnection;
import java.net.URL;

public interface HttpImpl {

    void onBeforeConnect(HttpURLConnection conn) throws Exception;

    void onOutput(HttpURLConnection conn) throws Exception;

    void onAfterConnect(HttpURLConnection conn) throws Exception;

    void fillCookie(URL url, Cookie cookie);

    void storeCookie(URL url, String cookie);
}
