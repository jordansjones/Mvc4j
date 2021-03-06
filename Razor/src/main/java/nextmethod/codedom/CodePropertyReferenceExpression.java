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

public class CodePropertyReferenceExpression extends CodeExpression implements Serializable {

    private static final long serialVersionUID = 6391820834110266987L;

    private CodeExpression targetObject;
    private String propertyName;

    public CodePropertyReferenceExpression() {
    }

    public CodePropertyReferenceExpression(final CodeExpression targetObject, final String propertyName) {
        this.targetObject = targetObject;
        this.propertyName = propertyName;
    }

    @Override
    public void accept(@Nonnull final ICodeDomVisitor visitor) {
        visitor.visit(this);
    }

    public CodeExpression getTargetObject() {
        return targetObject;
    }

    public void setTargetObject(final CodeExpression targetObject) {
        this.targetObject = targetObject;
    }

    public String getPropertyName() {
        return propertyName == null ? Strings.Empty : propertyName;
    }

    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }
}
