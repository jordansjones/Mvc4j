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

package nextmethod.base;

import java.lang.reflect.TypeVariable;
import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public final class TypeHelpers {

    private TypeHelpers() {
    }

    public static <T> boolean typeIs(final Object o, @Nonnull final Class<T> cls) {
        checkNotNull(cls);
        return o != null && cls.isInstance(o);
    }

    public static <T> T typeAs(final OutParam<?> o, @Nonnull final Class<T> cls) {
        return typeAs(o.value(), cls);
    }

    public static <T> T typeAs(final Object o, @Nonnull final Class<T> cls) {
        checkNotNull(cls);

        return o != null && cls.isInstance(o)
               ? cls.cast(o)
               : null;
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> typeOf(@Nonnull final T o) {
        return (Class<T>) checkNotNull(o).getClass();
    }

    public static boolean isGenericType(@Nonnull final Class<?> cls) {
        final TypeVariable<? extends Class<?>>[] typeVariables = checkNotNull(cls).getTypeParameters();
        return typeVariables.length > 0;
    }

    public static int getArrayDimensionCount(@Nonnull final Class<?> cls) {
        int count = 0;
        Class arrayClass = cls;
        while (arrayClass.isArray()) {
            count++;
            arrayClass = arrayClass.getComponentType();
        }
        return count;
    }

}
