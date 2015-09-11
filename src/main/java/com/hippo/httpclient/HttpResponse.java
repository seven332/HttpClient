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

import android.support.annotation.NonNull;

import com.hippo.yorozuya.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class HttpResponse {

    private HttpRequest mRequest;

    HttpResponse(HttpRequest request) {
        mRequest = request;
    }

    private void checkRequest() {
        if (mRequest.getState() != HttpRequest.STATE_RUNNING) {
            throw new IllegalStateException("The request is disconnected");
        }
    }

    public URL getUrl() {
        checkRequest();

        return mRequest.conn.getURL();
    }

    public int getResponseCode() throws IOException {
        checkRequest();

        return mRequest.conn.getResponseCode();
    }

    public int getContentLength() {
        checkRequest();

        return mRequest.conn.getContentLength();
    }

    public String getContentType() {
        checkRequest();

        return mRequest.conn.getContentType();
    }

    public String getContentEncoding() {
        checkRequest();

        return mRequest.conn.getContentEncoding();
    }

    public String getHeaderField(String key) {
        checkRequest();

        return mRequest.conn.getHeaderField(key);
    }

    public Map<String, List<String>> getHeaderFields() {
        checkRequest();

        return mRequest.conn.getHeaderFields();
    }

    public @NonNull InputStream getInputStream() throws IOException {
        checkRequest();

        InputStream is;
        HttpRequest request = mRequest;
        try {
            is = request.conn.getInputStream();
        } catch (IOException e) {
            is = request.conn.getErrorStream();
        }
        if (is == null) {
            throw new IOException("Can't get InputStream");
        }

        String encoding = request.conn.getContentEncoding();
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
            return new GZIPInputStream(is);
        } else {
            return is;
        }
    }

    public String getString() throws IOException {
        checkRequest();

        HttpRequest request = mRequest;
        HttpURLConnection conn = request.conn;
        InputStream is = getInputStream();

        ByteArrayOutputStream baos;
        int length = conn.getContentLength();
        if (length >= 0) {
            baos = new ByteArrayOutputStream(length);
        } else {
            baos = new ByteArrayOutputStream();
        }

        IOUtils.copy(is, baos);

        return baos.toString("UTF-8"); // TODO get charset from header or body
    }
}
