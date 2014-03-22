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

import nextmethod.base.Strings;

/**
 *
 */
public class CodeTypeDelegate extends CodeTypeDeclaration implements Serializable {

    private static final long serialVersionUID = 6788307286035287069L;

    private CodeParameterDeclarationExpressionCollection parameters;
    private CodeTypeReference returnType;

    public CodeTypeDelegate() {
        super.getBaseTypes().add(new CodeTypeReference("System.Delegate"));
    }

    public CodeTypeDelegate(final String name) {
        this();
        setName(name);
    }

    public CodeParameterDeclarationExpressionCollection getParameters() {
        if (parameters == null) {
            parameters = new CodeParameterDeclarationExpressionCollection();
        }
        return parameters;
    }

    public CodeTypeReference getReturnType() {
        if (returnType == null) {
            returnType = new CodeTypeReference(Strings.Empty);
        }
        return returnType;
    }

    public void setReturnType(final CodeTypeReference returnType) {
        this.returnType = returnType;
    }
}
