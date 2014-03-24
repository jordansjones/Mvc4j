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

import nextmethod.base.Strings;


public class CodeMemberField extends CodeTypeMember implements Serializable {

    private static final long serialVersionUID = -7687259189542307333L;

    private CodeExpression initExpression;
    private CodeTypeReference type;

    public CodeMemberField() {
    }

    public CodeMemberField(final CodeTypeReference type, final String name) {
        this.type = type;
        this.setName(name);
    }

    public CodeMemberField(final String type, final String name) {
        this.type = new CodeTypeReference(type);
        this.setName(name);
    }

    public CodeMemberField(final Class<?> type, final String name) {
        this.type = new CodeTypeReference(type);
        this.setName(name);
    }

    public CodeExpression getInitExpression() {
        return initExpression;
    }

    public void setInitExpression(final CodeExpression initExpression) {
        this.initExpression = initExpression;
    }

    public CodeTypeReference getType() {
        if (type == null) {
            type = new CodeTypeReference(Strings.Empty);
        }
        return type;
    }

    public void setType(final CodeTypeReference type) {
        this.type = type;
    }

    @Override
    public void accept(@Nonnull final ICodeDomVisitor visitor) {
        visitor.visit(this);
    }
}
