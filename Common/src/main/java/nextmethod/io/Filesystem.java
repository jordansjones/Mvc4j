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

package nextmethod.io;

import java.nio.file.FileSystems;
import javax.annotation.Nonnull;

import com.google.common.base.Joiner;
import nextmethod.base.SystemHelpers;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Filesystem {

    private Filesystem() {}

    public static String getFileName(@Nonnull final String fileName) {
        checkNotNull(fileName);
        return FileSystems.getDefault().getPath(fileName).getFileName().toString();
    }

    private static final Joiner pathJoiner = Joiner.on(SystemHelpers.pathSeparator());

    public static String createFilePath(@Nonnull final String... parts) {
        checkArgument(parts.length > 0);
        return pathJoiner.join(parts);
    }
}
