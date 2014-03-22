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

package nextmethod.web.razor.utils;

import java.util.concurrent.atomic.AtomicBoolean;

import nextmethod.base.Delegates;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DisposableActionTest {

    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullAction() {
        new DisposableAction(null);
    }

    @Test
    public void actionIsExecutedOnExplicitDispose() {
        final AtomicBoolean called = new AtomicBoolean(false);
        DisposableAction action = new DisposableAction(createAction(called));

        assertFalse(called.get());

        action.close();

        assertTrue("The action was not run when the DisposableAction was closed", called.get());
    }

    @Test
    public void actionIsExecutedOnImplicitDispose() {
        final AtomicBoolean called = new AtomicBoolean(false);

        try (DisposableAction action = new DisposableAction(createAction(called))) {
            assertFalse(called.get());
        }

        assertTrue("The action was not run when the DisposableAction was closed", called.get());
    }

    private static Delegates.IAction createAction(final AtomicBoolean called) {
        return () -> called.set(true);
    }
}
