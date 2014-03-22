/*
 * Copyright 2014 Jordan S. Jones <jordansjones@gmail.com>
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

package nextmethod.web.razor.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import nextmethod.base.NotImplementedException;

public class TestFile {

    public static final String ResourceNameFormat = "/testFiles/%s";

    private final String resourceName;
    private final URL resourceUrl;

    public TestFile(final String resourceName) {
        this.resourceName = resourceName;
        this.resourceUrl = Resources.getResource(getClass(), resourceName);
    }

    public String getResourceName() {
        return resourceName;
    }

    public static TestFile create(final String localResourceName) {
        return new TestFile(String.format(ResourceNameFormat, localResourceName));
    }

    public byte[] readAllBytes() {
        try {
            return Files.readAllBytes(Paths.get(resourceUrl.toURI()));
        }
        catch (URISyntaxException | IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public String readAllText() {
        String blah = new String(readAllBytes(), Charsets.UTF_8);
        blah = blah.replaceAll("\\r\\n", "\n");
        blah = blah.replaceAll("\\r", "\n");
        return blah.replaceAll("\\n", "\r\n");
    }

    public void save(final String filePath) {
        throw new NotImplementedException();
    }
}
