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

class RazorCodeGeneratorTestBuilder {

    private String name;
    private String baselineName;
    private boolean generatePragmas = true;
    private boolean designTimeMode = false;
    private List<GeneratedCodeMapping> expectedDesignTimePragmas;
    private TestSpan[] testSpans;
    private TabTest tabTest = TabTest.Both;
    private Delegates.IAction1<RazorEngineHost> hostConfig;
    private RazorCodeGeneratorTestRunner runner;

    public RazorCodeGeneratorTestBuilder setName(final String name) {
        this.name = name;
        return this;
    }

    public RazorCodeGeneratorTestBuilder setBaselineName(final String baselineName) {
        this.baselineName = baselineName;
        return this;
    }

    public RazorCodeGeneratorTestBuilder setGeneratePragmas(final boolean generatePragmas) {
        this.generatePragmas = generatePragmas;
        return this;
    }

    public RazorCodeGeneratorTestBuilder setDesignTimeMode(final boolean designTimeMode) {
        this.designTimeMode = designTimeMode;
        return this;
    }

    public RazorCodeGeneratorTestBuilder setExpectedDesignTimePragmas(final List<GeneratedCodeMapping> expectedDesignTimePragmas) {
        this.expectedDesignTimePragmas = expectedDesignTimePragmas;
        return this;
    }

    public RazorCodeGeneratorTestBuilder setTestSpans(final TestSpan[] testSpans) {
        this.testSpans = testSpans;
        return this;
    }

    public RazorCodeGeneratorTestBuilder setTabTest(final TabTest tabTest) {
        this.tabTest = tabTest;
        return this;
    }

    public RazorCodeGeneratorTestBuilder setHostConfig(final Delegates.IAction1<RazorEngineHost> hostConfig) {
        this.hostConfig = hostConfig;
        return this;
    }

    public RazorCodeGeneratorTestBuilder setRunner(final RazorCodeGeneratorTestRunner runner) {
        this.runner = runner;
        return this;
    }

    public void run() {
        runner.execute(
            this.name,
            this.baselineName,
            this.generatePragmas,
            this.designTimeMode,
            this.expectedDesignTimePragmas,
            this.testSpans,
            this.tabTest,
            this.hostConfig
        );
    }

}