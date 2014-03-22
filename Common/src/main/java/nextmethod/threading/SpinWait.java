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

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import nextmethod.base.Delegates;

public final class SpinWait {

    // The number of steps until SpinOnce yields on multicore machines
    private static final int step = 10;
    private static final int maxTime = 200;
    private static final boolean isSingleCpu = (Runtime.getRuntime().availableProcessors() == 1);

    private int nTime;

    public void spinOnce() {
        nTime += 1;
        if (nextSpinWillYield()) {
            // Spinning does no good on single cpus
            Thread.yield();
        }
        else {
            // Multi-CPU system
            spin(Math.min(nTime, maxTime) << 1);
        }
    }

    public void reset() {
        nTime = 0;
    }

    public boolean nextSpinWillYield() {
        return isSingleCpu || nTime % step == 0;
    }

    public int count() {
        return nTime;
    }

    private void spin(int iterations) {
        if (iterations < 0) {
            return;
        }
        while (iterations-- > 0) {
            // Nop
            assert true;
        }
    }

    public static void spinUntil(final Delegates.IFunc<Boolean> condition) {
        final SpinWait sw = new SpinWait();
        while (!condition.invoke()) {
            sw.spinOnce();
        }
    }

    public static boolean spinUntil(final Delegates.IFunc<Boolean> condition, final long amount,
                                    final TimeUnit timeUnit
                                   ) {
        final SpinWait spinWait = new SpinWait();
        final Stopwatch stopwatch = Stopwatch.createStarted();

        while (!condition.invoke()) {
            if (stopwatch.elapsed(timeUnit) > amount) {
                return false;
            }
            spinWait.spinOnce();
        }
        return true;
    }
}
