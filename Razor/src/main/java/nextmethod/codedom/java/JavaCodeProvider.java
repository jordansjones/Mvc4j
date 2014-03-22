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

package nextmethod.codedom.java;

import java.util.Map;

import nextmethod.codedom.compiler.CodeDomProvider;
import nextmethod.codedom.compiler.ICodeCompiler;
import nextmethod.codedom.compiler.ICodeGenerator;

public class JavaCodeProvider extends CodeDomProvider {

    public static final String FileExtension = "java";

    private Map<String, String> providerOptions;

    public JavaCodeProvider() {
        this.fileExtension = FileExtension;
    }

    public JavaCodeProvider(final Map<String, String> providerOptions) {
        this();
        this.providerOptions = providerOptions;
    }

    @Override
    public ICodeCompiler createCompiler() {
        if (providerOptions != null && !providerOptions.isEmpty()) {
            return new JavaCodeCompiler(providerOptions);
        }
        return new JavaCodeCompiler();
    }

    @Override
    public ICodeGenerator createGenerator() {
        if (providerOptions != null && !providerOptions.isEmpty()) {
            return new JavaCodeGenerator(providerOptions);
        }
        return new JavaCodeGenerator();
    }
}
