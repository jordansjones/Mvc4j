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

package nextmethod.codedom;

import java.io.Serializable;
import java.util.Arrays;

import nextmethod.base.Strings;

public class CodeAnnotationDeclaration implements Serializable {

    private static final long serialVersionUID = 2953310238641471846L;

    private String name;
    private CodeAnnotationArgumentCollection arguments;
    private CodeTypeReference attributeType;

    public CodeAnnotationDeclaration() {
    }

    public CodeAnnotationDeclaration(final String name) {
        this.name = name;
    }

    public CodeAnnotationDeclaration(final String name, final CodeAnnotationArgument... arguments) {
        this.name = name;
        this.addArguments(arguments);
    }

    public CodeAnnotationDeclaration(final CodeTypeReference attributeType) {
        this.attributeType = attributeType;
        if (attributeType != null) {
            this.name = attributeType.getBaseType();
        }
    }

    public CodeAnnotationDeclaration(final CodeTypeReference attributeType, final CodeAnnotationArgument... arguments) {
        this.attributeType = attributeType;
        if (attributeType != null) {
            this.name = attributeType.getBaseType();
        }
        this.addArguments(arguments);
    }

    public CodeAnnotationArgumentCollection getArguments() {
        if (arguments == null) {
            arguments = new CodeAnnotationArgumentCollection();
        }
        return arguments;
    }

    public CodeTypeReference getAttributeType() {
        return attributeType;
    }

    public void addArguments(final CodeAnnotationArgument... arguments) {
        if (arguments != null && arguments.length > 0) {
            this.getArguments().addAll(Arrays.asList(arguments));
        }
    }

    public String getName() {
        return name == null
               ? Strings.Empty
               : name;
    }

    public void setName(final String name) {
        this.name = name;
        this.attributeType = new CodeTypeReference(name);
    }
}
