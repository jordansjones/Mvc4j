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

import nextmethod.annotations.Internal;
import nextmethod.base.NotImplementedException;

public class CodeObject implements Serializable {

    private static final long serialVersionUID = 4834403342206879294L;

    private Object userData;

    public Object getUserData() {
        if (userData == null) {
            userData = new Object();
        }
        return userData;
    }

    @Internal
    public void accept(@Nonnull final ICodeDomVisitor visitor) {
        throw new NotImplementedException();
    }

}
