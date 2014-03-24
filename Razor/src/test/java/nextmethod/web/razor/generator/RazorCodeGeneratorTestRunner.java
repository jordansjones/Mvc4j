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

package nextmethod.web.razor.generator;

import java.util.List;

import nextmethod.base.Delegates;
import nextmethod.web.razor.RazorEngineHost;

@FunctionalInterface
interface RazorCodeGeneratorTestRunner {
    void execute(
        final String name,
        String baselineName,
        boolean generatePragmas,
        boolean designTimeMode,
        final List<GeneratedCodeMapping> expectedDesignTimePragmas,
        final TestSpan[] spans,
        TabTest tabTest,
        Delegates.IAction1<RazorEngineHost> hostConfig
    );
}
