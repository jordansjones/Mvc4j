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
import javax.annotation.Nonnull;

public class CodeMemberMethod extends CodeTypeMember implements Serializable {

    private static final long serialVersionUID = 2082561759474834442L;

    private CodeTypeReferenceCollection implementationTypes;
    private CodeParameterDeclarationExpressionCollection parameters;
    private CodeTypeReference privateImplements;
    private CodeTypeReference returnType;
    private CodeStatementCollection statements;
    private CodeAnnotationDeclarationCollection returnAnnotations;

    CodeTypeParameterCollection typeParameters;

    public CodeMemberMethod() {
    }

    public CodeTypeReferenceCollection getImplementationTypes() {
        if (implementationTypes == null) {
            implementationTypes = new CodeTypeReferenceCollection();
            // PopulationImplementationTypes Event
        }
        return implementationTypes;
    }

    public CodeParameterDeclarationExpressionCollection getParameters() {
        if (parameters == null) {
            parameters = new CodeParameterDeclarationExpressionCollection();
            // PopulateParameters Event
        }
        return parameters;
    }

    public CodeTypeReference getPrivateImplementationType() {
        return privateImplements;
    }

    public void setPrivateImplementationType(final CodeTypeReference value) {
        this.privateImplements = value;
    }

    public CodeTypeReference getReturnType() {
        if (returnType == null) {
            return new CodeTypeReference(Void.class);
        }
        return returnType;
    }

    public void setReturnType(final CodeTypeReference returnType) {
        this.returnType = returnType;
    }

    public CodeStatementCollection getStatements() {
        if (statements == null) {
            statements = new CodeStatementCollection();
            // PopulateStatements Event
        }
        return statements;
    }

    public CodeAnnotationDeclarationCollection getReturnTypeCustomAnnotations() {
        if (returnAnnotations == null) {
            returnAnnotations = new CodeAnnotationDeclarationCollection();
        }
        return returnAnnotations;
    }

    public CodeTypeParameterCollection getTypeParameters() {
        if (typeParameters == null) {
            typeParameters = new CodeTypeParameterCollection();
        }
        return typeParameters;
    }

    @Override
    public void accept(@Nonnull final ICodeDomVisitor visitor) {
        visitor.visit(this);
    }
}
