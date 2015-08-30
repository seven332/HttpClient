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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

public class FileData extends FormData {

    private final File mFile;

    public FileData(File file) {
        mFile = file;
        setProperty("Content-Type",
                URLConnection.guessContentTypeFromName(file.getName()));
    }

    @Override
    public void output(OutputStream os) throws IOException {
        InputStream is = new FileInputStream(mFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while((bytesRead = is.read(buffer)) !=-1)
            os.write(buffer, 0, bytesRead);
        is.close();

        os.write("\r\n".getBytes());
    }
}
