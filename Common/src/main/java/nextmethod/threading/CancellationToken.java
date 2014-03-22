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

import javax.annotation.Nonnull;

import nextmethod.annotations.Internal;
import nextmethod.base.Delegates;

import static com.google.common.base.Preconditions.checkNotNull;

// TODO
public final class CancellationToken {

    private final CancellationTokenSource source;

    public CancellationToken(final boolean canceled) {
        this(
                canceled
                ? CancellationTokenSource.CanceledSource
                : null
            );
    }

    @Internal
    public CancellationToken(final CancellationTokenSource source) {
        this.source = source;
    }

    private CancellationTokenSource source() {
        return source == null
               ? CancellationTokenSource.NoneSource
               : source;
    }

    public static CancellationToken none() {
        return new CancellationToken(false);
    }

    public boolean canBeCanceled() {
        return source != null;
    }

    public boolean isCancellationRequested() {
        return source().isCancellationRequested();
    }

    public void throwIfCancellationRequested() {
        if (source().isCancellationRequested()) {
            throw new OperationCanceledException(this);
        }
    }

    public CancellationTokenRegistration register(@Nonnull final Delegates.IAction callback) {
        return register(callback, false);
    }

    public CancellationTokenRegistration register(@Nonnull final Delegates.IAction callback,
                                                  final boolean useSynchronizationContext
                                                 ) {
        return source().register(checkNotNull(callback), useSynchronizationContext);
    }

}
