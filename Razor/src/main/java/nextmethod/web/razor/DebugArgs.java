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

package nextmethod.web.razor;

import nextmethod.annotations.Internal;

@Internal
public final class DebugArgs {

    private DebugArgs() {}

    public static final String DebuggerIsAttached = "DEBUGGER_IS_ATTACHED";
    public static final String RazorEditorTrace = "RAZOR_EDITOR_TRACE";
    public static final String EditorTracing = "EDITOR_TRACING";
    public static final String CheckTree = "CHECK_TREE";
    public static final String GenerateBaselines = "GENERATE_BASELINES";

}
