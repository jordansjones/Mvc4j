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

// TODO
public class CodeTypeMemberCollection extends BaseCodeCollection<CodeTypeMember> implements Serializable {

    private static final long serialVersionUID = -931048809114327031L;


    public void copyTo(@Nonnull final CodeTypeMember[] destination, final int index) {
        final CodeTypeMember[] members = this.delegate().stream().toArray(CodeTypeMember[]::new);
        System.arraycopy(members, 0, destination, index, members.length);
    }
}
