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

public class CodePrimitiveExpression extends CodeExpression implements Serializable {

    private static final long serialVersionUID = 6501060541239915815L;

    private Object value;

    public CodePrimitiveExpression() {
    }

    public CodePrimitiveExpression(final Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    @Override
    public void accept(@Nonnull final ICodeDomVisitor visitor) {
        visitor.visit(this);
    }
}
