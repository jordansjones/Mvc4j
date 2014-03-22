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

package nextmethod.threading;

import javax.annotation.Nullable;

import nextmethod.annotations.Internal;
import nextmethod.base.IDisposable;

public final class CancellationTokenRegistration implements IDisposable {

    private final int id;
    private final CancellationTokenSource source;

    @Internal
    public CancellationTokenRegistration(final int id, @Nullable final CancellationTokenSource source) {
        this.id = id;
        this.source = source;
    }

    @Override
    public void close() {
        if (source != null) {
            source.removeCallback(this);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CancellationTokenRegistration that = (CancellationTokenRegistration) o;

        if (id != that.id) return false;
        if (source != null
            ? !source.equals(that.source)
            : that.source != null) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (source != null
                                ? source.hashCode()
                                : 0);
        return result;
    }
}
