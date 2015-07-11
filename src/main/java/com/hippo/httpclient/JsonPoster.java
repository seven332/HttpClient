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

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;

public class JsonPoster extends StringGetter {

    private JSONObject mJSON;

    public JsonPoster(JSONObject json) {
        mJSON = json;
    }

    @Override
    public void onBeforeConnect(HttpURLConnection conn) throws Exception {
        super.onBeforeConnect(conn);

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
    }

    @Override
    public void onOutput(HttpURLConnection conn) throws Exception {
        super.onOutput(conn);

        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.write(mJSON.toString().getBytes("utf-8"));
        out.flush();
        out.close();
    }
}
