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

package nextmethod.web.razor.framework;

import nextmethod.base.IDisposable;
import nextmethod.base.Strings;
import nextmethod.web.razor.utils.DisposableAction;

import static nextmethod.base.SystemHelpers.newLine;

/**
 *
 */
public class ErrorCollector {

    private final StringBuilder message = new StringBuilder();
    private int indent = 0;

    private boolean success;

    public ErrorCollector() {
        success = true;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message.toString();
    }

    public void addError(final String msg, final Object... args) {
        append("F", msg, args);
        success = false;
    }

    public void addMessage(final String msg, final Object... args) {
        append("P", msg, args);
    }

    public IDisposable indent() {
        indent++;
        return new DisposableAction(this::unindent);
    }

    public void unindent() {
        indent--;
    }

    private void append(final String prefix, final String msg, final Object[] args) {
        message
            .append(prefix)
            .append(":")
            .append(Strings.repeat("\t", indent))
            .append(String.format(msg, args))
            .append(newLine());
    }
}
