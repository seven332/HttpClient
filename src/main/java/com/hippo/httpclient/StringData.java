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

import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.OutputStream;

public class StringData extends FormData {

    private final String mStr;

    public StringData(@Nullable String str) {
        mStr = str;
    }

    @Override
    public void output(OutputStream os) throws IOException {
        if (mStr != null) {
            os.write(mStr.getBytes("UTF-8"));
        }
    }
}
