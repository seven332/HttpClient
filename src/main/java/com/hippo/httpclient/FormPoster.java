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

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FormPoster extends StringGetter {

    private List<String[]> mList = new ArrayList<>();

    public FormPoster addData(String key, String value) {
        mList.add(new String[]{key, value});
        return this;
    }

    @Override
    public void onBeforeConnect(HttpURLConnection conn) throws Exception {
        super.onBeforeConnect(conn);

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    }

    @Override
    public void onOutput(HttpURLConnection conn) throws Exception {
        super.onOutput(conn);

        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String[] arg : mList) {
            if (i != 0) {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(arg[0], "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(arg[1], "UTF-8"));
            i++;
        }
        out.writeBytes(sb.toString());
        out.flush();
        out.close();
    }
}
