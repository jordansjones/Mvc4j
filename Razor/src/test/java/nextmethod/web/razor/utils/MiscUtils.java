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

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
import nextmethod.web.razor.DebugArgs;

import static org.junit.Assert.assertEquals;

public final class MiscUtils {

    private MiscUtils() {}

    public static final int TimeoutInSeconds = 1;

    private static final Pattern RuntimePattern = Pattern.compile("Runtime Version: ([\\d._]*)");
    private static final String RuntimeVersion = "N.N.NNNNN.N";

    public static String stripRuntimeVersion(String s) {
        final Matcher matcher = RuntimePattern.matcher(s);
        if (!matcher.find()) return s;
        return s.substring(0, matcher.start(1)) + RuntimeVersion + s.substring(matcher.end(1));
    }

    @SuppressWarnings("ConstantConditions")
    public static void DoWithTimeoutIfNotDebugging(@Nonnull Delegates.IFunc1<Long, Boolean> withTimeout) {
        if (Debug.isDebugArgPresent(DebugArgs.DebuggerIsAttached)) {
            withTimeout.invoke(Long.MAX_VALUE);
        }
        else {
            assertEquals("Timeout Expired!", true, withTimeout.invoke(TimeUnit.SECONDS.toMillis(TimeoutInSeconds)));
        }
    }


    public static String createTestFilePath(final String... parts) {
        final List<String> pathParts = Lists.newArrayList(parts);
        final FileSystem fileSystem = FileSystems.getDefault();
        String first = pathAsString(Iterables.getFirst(fileSystem.getRootDirectories(), null));
        if (first != null && !first.startsWith(fileSystem.getSeparator())) {
            // Strip the last character if it equals the filesystem separator
            if (first.length() > 1 && first.endsWith(fileSystem.getSeparator())) {
                first = first.substring(0, first.length() - 1);
            }
            pathParts.add(0, first);
        }
        return Joiner.on(fileSystem.getSeparator()).join(pathParts);
    }

    private static String pathAsString(final Path path) {
        return path != null
               ? path.toString()
               : null;
    }
}
