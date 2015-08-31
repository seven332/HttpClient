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

public class FormDataPoster extends StringGetter {

    private static final String BOUNDARY = "----WebKitFormBoundary7eDB0hDQ91s22Tkf";

    private FormData[] mFormDatas;

    /**
     * @param formDatas null FormData will be skipped
     */
    public FormDataPoster(FormData... formDatas) {
        mFormDatas = formDatas;
    }

    @Override
    public void onBeforeConnect(HttpURLConnection conn) throws Exception {
        super.onBeforeConnect(conn);

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
    }

    @Override
    public void onOutput(HttpURLConnection conn) throws Exception {
        super.onOutput(conn);

        DataOutputStream out = new DataOutputStream(conn.getOutputStream());

        for (FormData data : mFormDatas) {
            if (data == null) {
                continue;
            }

            out.write("--".getBytes());
            out.write(BOUNDARY.getBytes());
            out.write("\r\n".getBytes());
            data.doOutPut(out);
        }
        out.write("--".getBytes());
        out.write(BOUNDARY.getBytes());
        out.write("--".getBytes());

        out.flush();
        out.close();
    }
}
