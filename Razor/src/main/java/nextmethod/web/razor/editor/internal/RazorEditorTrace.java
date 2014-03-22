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

package nextmethod.web.razor.editor.internal;

import java.text.MessageFormat;

import nextmethod.annotations.Internal;
import nextmethod.base.Debug;
import nextmethod.web.razor.DebugArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

// TODO: Use something better than System.out
@Internal
public final class RazorEditorTrace {

    private static final Logger logger = LoggerFactory.getLogger(RazorEditorTrace.class);

    private RazorEditorTrace() {}

    private static final Object lockObj = new Object();
    private static Boolean enabled;

    static {
        synchronized (lockObj) {
            if (enabled == null) {
                final boolean isEnabled = Debug.isDebugArgPresent(DebugArgs.RazorEditorTrace);

                logger.trace(
                                RazorResources().traceStartup(
                                                                 isEnabled
                                                                 ? RazorResources().traceEnabled()
                                                                 : RazorResources().traceDisabled()
                                                             )
                            );
                enabled = isEnabled;
            }
        }
    }

    private static boolean isEnabled() {
        return enabled;
    }

    public static void traceLine(final String format, final Object... args) {
        if (isEnabled() && Debug.isDebugArgPresent(DebugArgs.EditorTracing) && logger.isTraceEnabled()) {
            logger.trace(
                            RazorResources().traceFormat(
                                                            args != null && args.length > 0
                                                            ? MessageFormat.format(format, args)
                                                            : format
                                                        )
                        );
        }
    }
}
