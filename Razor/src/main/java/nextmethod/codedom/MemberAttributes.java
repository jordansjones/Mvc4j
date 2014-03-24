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
import java.lang.reflect.Modifier;

public class MemberAttributes implements Serializable {

    private static final long serialVersionUID = 3039744525780381022L;

    public static final MemberAttributes Abstract = new MemberAttributes(Modifier.ABSTRACT);
    public static final MemberAttributes Final = new MemberAttributes(Modifier.FINAL);
    public static final MemberAttributes Private = new MemberAttributes(Modifier.PRIVATE);
    public static final MemberAttributes Protected = new MemberAttributes(Modifier.PROTECTED);
    public static final MemberAttributes Public = new MemberAttributes(Modifier.PUBLIC);
    public static final MemberAttributes Static = new MemberAttributes(Modifier.STATIC);
    public static final MemberAttributes Transient = new MemberAttributes(Modifier.TRANSIENT);
    public static final MemberAttributes Volatile = new MemberAttributes(Modifier.VOLATILE);

    public static final MemberAttributes ScopeMask = new MemberAttributes(Modifier.ABSTRACT | Modifier.FINAL | Modifier.STATIC);

    public static final MemberAttributes AccessMask = new MemberAttributes(Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE);

    public static MemberAttributes valueOf(final int val) {
        return new MemberAttributes(val);
    }

    public final int val;

    private MemberAttributes(final int modifier) {
        this.val = modifier;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MemberAttributes that = (MemberAttributes) o;

        if (val != that.val) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return val;
    }
}
