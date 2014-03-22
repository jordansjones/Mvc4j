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

import java.util.Objects;

import nextmethod.base.EventArgs;
import nextmethod.codedom.CodeCompileUnit;

public class CodeGenerationCompleteEventArgs extends EventArgs {

    private final CodeCompileUnit generatedCode;
    private final String virtualPath;
    private final String physicalPath;

    public CodeGenerationCompleteEventArgs(final String virtualPath, final String physicalPath,
                                           final CodeCompileUnit generatedCode
                                          ) {
        Objects.requireNonNull(virtualPath);
        Objects.requireNonNull(generatedCode);

        this.virtualPath = virtualPath;
        this.physicalPath = physicalPath;
        this.generatedCode = generatedCode;
    }

    public CodeCompileUnit getGeneratedCode() {
        return generatedCode;
    }

    public String getVirtualPath() {
        return virtualPath;
    }

    public String getPhysicalPath() {
        return physicalPath;
    }
}
