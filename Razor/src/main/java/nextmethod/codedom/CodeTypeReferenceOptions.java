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

public class CodeTypeReferenceOptions implements Serializable {

    private static final long serialVersionUID = -2322501788454782250L;

    public static final CodeTypeReferenceOptions GlobalReference = new CodeTypeReferenceOptions(1);
    public static final CodeTypeReferenceOptions GenericTypeParameter = new CodeTypeReferenceOptions(1 << 1);

    public final int val;

    public CodeTypeReferenceOptions(final int val) {
        this.val = val;
    }
}
