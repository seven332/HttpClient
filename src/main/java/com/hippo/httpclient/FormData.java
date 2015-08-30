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
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class FormData {
    private final Map<String, String> mProperties;

    public FormData() {
        mProperties = new LinkedHashMap<>();
    }

    public void setProperty(String key, String value) {
        mProperties.put(key, value);
    }

    public void clearProperty(String key) {
        mProperties.remove(key);
    }

    public void clearAllProperties() {
        mProperties.clear();
    }

    /**
     * Put information to target OutputStream.
     *
     * @param os Target OutputStream
     * @throws IOException
     */
    public abstract void output(OutputStream os) throws IOException;

    public void doOutPut(OutputStream os) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String key : mProperties.keySet())
            sb.append(key).append(": ").append(mProperties.get(key)).append("\r\n");
        sb.append("\r\n");
        os.write(sb.toString().getBytes());
        output(os);
    }
}
