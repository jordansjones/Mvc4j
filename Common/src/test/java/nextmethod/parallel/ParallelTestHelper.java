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

package nextmethod.parallel;

import com.google.common.util.concurrent.Uninterruptibles;
import nextmethod.base.Delegates;

public final class ParallelTestHelper {

    private ParallelTestHelper() {}

    public static final int NumRun = 500;

    public static void repeat(final Delegates.IAction action) {
        repeat(action, NumRun);
    }

    public static void repeat(final Delegates.IAction action, final int numberTimes) {
        for (int i = 0; i < numberTimes; i++) {
            action.invoke();
        }
    }

    public static <T> void parallelStressTest(final T obj, final Delegates.IAction1<T> action) {
        parallelStressTest(obj, action, Runtime.getRuntime().availableProcessors() + 2);
    }

    public static <T> void parallelStressTest(final T obj, final Delegates.IAction1<T> action, int numberOfThreads) {
        final Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> action.invoke(obj));
            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i].interrupt();
            Uninterruptibles.joinUninterruptibly(threads[i]);
        }
    }

}
