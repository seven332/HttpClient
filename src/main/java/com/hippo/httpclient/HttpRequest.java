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

import android.os.Looper;
import android.os.Process;

import com.hippo.yorozuya.PriorityThreadFactory;
import com.hippo.yorozuya.SerialThreadExecutor;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

public class HttpRequest {

    public static final int STATE_NONE = 0;
    public static final int STATE_RUNNING = 1;
    public static final int STATE_DISCONNECT = 2;

    public static final int METHOD_GET = 0;
    public static final int METHOD_POST = 1;

    private int mState = STATE_NONE;

    HttpURLConnection conn;
    private HttpImpl mHttpImpl;

    private URL mUrl;
    private int mMethod = METHOD_GET;

    private static final SerialThreadExecutor sThreadExecutor = new SerialThreadExecutor(
            3000, new LinkedBlockingQueue<Runnable>(),
            new PriorityThreadFactory("HttpRequestCancel", Process.THREAD_PRIORITY_BACKGROUND));

    public int getMethod() {
        return mMethod;
    }

    public void setMethod(int method) {
        mMethod = method;
    }

    public URL getUrl() {
        return mUrl;
    }

    public void setUrl(URL url) {
        mUrl = url;
    }

    public void setUrl(String url) {
        try {
            mUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setHttpImpl(HttpImpl httpImpl) {
        mHttpImpl = httpImpl;
    }

    HttpImpl getHttpImple() {
        return mHttpImpl;
    }

    void start() {
        mState = STATE_RUNNING;
    }

    // TODO
    public void disconnect() {
        if (mState != STATE_DISCONNECT) {
            mState = STATE_DISCONNECT;
            if (conn != null) {
                // Avoid NetworkOnMainThreadException
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    sThreadExecutor.execute(new HttpRequestCanceller(conn));
                } else {
                    conn.disconnect();
                }
                conn = null;
            }
        }
    }

    public int getState() {
        return mState;
    }

    public void clear() {
        disconnect();
        mState = STATE_NONE;
        conn = null;
        mHttpImpl = null;
        mUrl = null;
        mMethod = METHOD_GET;
    }

    protected void onBeforeConnect(HttpURLConnection conn) throws Exception {
        switch (mMethod) {
            default:
            case METHOD_GET:
                conn.setRequestMethod("GET");
                break;
            case METHOD_POST:
                conn.setRequestMethod("POST");
                break;
        }

        if (mHttpImpl != null) {
            mHttpImpl.onBeforeConnect(conn);
        }
    }

    protected void onOutput(HttpURLConnection conn) throws Exception {
        if (mHttpImpl != null) {
            mHttpImpl.onOutput(conn);
        }
    }

    protected void onAfterConnect(HttpURLConnection conn) throws Exception {
        if (mHttpImpl != null) {
            mHttpImpl.onAfterConnect(conn);
        }
    }

    protected void fillCookie(URL url, Cookie cookie) {
        if (mHttpImpl != null) {
            mHttpImpl.fillCookie(url, cookie);
        }
    }

    protected void storeCookie(URL url, String cookie) {
        if (mHttpImpl != null) {
            mHttpImpl.storeCookie(url, cookie);
        }
    }

    class HttpRequestCanceller implements Runnable {

        private HttpURLConnection mConn;

        public HttpRequestCanceller(HttpURLConnection conn) {
            mConn = conn;
        }

        @Override
        public void run() {
            if (mConn != null) {
                mConn.disconnect();
                mConn = null;
            }
        }
    }
}
