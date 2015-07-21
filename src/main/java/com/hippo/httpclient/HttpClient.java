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

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;

public class HttpClient {

    public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/43.0.2357.132 Safari/537.36";

    private int mConnectTimeout = 5000;
    private int mReadTimeout = 5000;
    private String mUserAgent = System.getProperty("http.agent", DEFAULT_USER_AGENT);
    private Proxy mProxy;

    public void setConnectTimeout(int connectTimeout) {
        if (connectTimeout < 0) {
            throw new IllegalStateException("Connect timeout must be non-negtive");
        }
        mConnectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        if (readTimeout < 0) {
            throw new IllegalStateException("Read timeout must be non-negtive");
        }
        mReadTimeout = readTimeout;
    }

    public void setProxy(Proxy proxy, String user, String password) {
        mProxy = proxy;
        Authenticator.setDefault(new PasswordAuthenticator(user, password));
    }

    public HttpResponse execute(HttpRequest request) throws Exception {
        if (request.getState() != HttpRequest.STATE_NONE) {
            throw new IOException("The request is running or disconnected");
        }

        if (request.getHttpImple() == null) {
            request.setHttpImpl(new StringGetter());
        }
        request.start();
        return HttpEngine.request(this, request);
    }

    HttpURLConnection openConnent(URL url) throws IOException {
        if (mProxy != null) {
            return (HttpURLConnection) url.openConnection(mProxy);
        } else {
            return (HttpURLConnection) url.openConnection();
        }
    }

    protected void onBeforeConnect(HttpURLConnection conn) throws Exception {
        conn.setConnectTimeout(mConnectTimeout);
        conn.setReadTimeout(mReadTimeout);
        conn.setRequestProperty("User-Agent", mUserAgent);
        conn.setInstanceFollowRedirects(true);
    }

    protected void onAfterConnect(HttpURLConnection conn) throws Exception {
    }

    protected void fillCookie(URL url, Cookie cookie) {
    }

    protected void storeCookie(URL url, String cookie) {
    }

    class PasswordAuthenticator extends Authenticator {

        private String mUser;
        private char[] mPassword;

        public PasswordAuthenticator(String user, String password) {
            mUser = user;
            mPassword = password.toCharArray();
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return (new PasswordAuthentication(mUser, mPassword));
        }
    }
}
