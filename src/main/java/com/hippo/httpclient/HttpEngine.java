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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public final class HttpEngine {

    private static final int HTTP_TEMP_REDIRECT = 307;

    private static final int MAX_REDIRECTS = 5;

    public static HttpResponse request(HttpClient client, HttpRequest request) throws Exception {
        URL url = request.getUrl();
        Cookie cookie = new Cookie();
        HttpURLConnection conn;
        Exception exception = null;
        int redirectionCount = 0;

        try {
            while (redirectionCount++ < MAX_REDIRECTS) {
                if (request.getState() != HttpRequest.STATE_RUNNING) {
                    throw new IOException("The Http call is canceled");
                }

                conn = client.openConnent(url);
                request.conn = conn;
                client.onBeforeConnect(conn);
                request.onBeforeConnect(conn);

                cookie.clear();
                client.fillCookie(url, cookie);
                request.fillCookie(url, cookie);
                conn.setRequestProperty("Cookie", cookie.toString());

                conn.connect();
                request.onOutput(conn);
                final int responseCode = conn.getResponseCode();
                switch (responseCode) {
                    case HttpURLConnection.HTTP_MOVED_PERM:
                    case HttpURLConnection.HTTP_MOVED_TEMP:
                    case HttpURLConnection.HTTP_SEE_OTHER:
                    case HTTP_TEMP_REDIRECT:
                        final String location = conn.getHeaderField("Location");
                        conn.disconnect();
                        url = new URL(url, location);
                        break;
                    default:
                        // Store cookie
                        List<String> cookies = conn.getHeaderFields().get("Set-Cookie");
                        if (cookies != null) {
                            for (String cookieStr : cookies) {
                                client.storeCookie(url, "Set-Cookie", cookieStr);
                                request.storeCookie(url, "Set-Cookie", cookieStr);
                            }
                        }

                        List<String> cookies2 = conn.getHeaderFields().get("Set-Cookie2");
                        if (cookies != null) {
                            for (String cookieStr : cookies2) {
                                client.storeCookie(url, "Set-Cookie2", cookieStr);
                                request.storeCookie(url, "Set-Cookie2", cookieStr);
                            }
                        }

                        client.onAfterConnect(conn);
                        request.onAfterConnect(conn);
                        return new HttpResponse(request);
                }
            }
        } catch (Exception e) {
            exception = e;
        }

        request.disconnect();

        if (exception == null) {
            exception = new RedirectionException();
        }

        throw exception;
    }
}
