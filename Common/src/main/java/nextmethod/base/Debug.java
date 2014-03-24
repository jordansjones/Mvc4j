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

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public final class Debug {

    private static final Logger logger = LoggerFactory.getLogger(Debug.class);

    public static void doAssert(final boolean expression, final String message) {
        if (isAssertEnabled && !expression && logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    public static boolean isDebugArgPresent(final String argName) {
        String argVal = System.getProperty(argName);
        if (argVal == null) {
            argVal = System.getenv(argName);
        }
        return argVal != null;
    }

    /**
     * Emits the specified error message
     *
     * @param message Error message to emit
     */
    public static void fail(@Nonnull final String message) {
        if (isAssertEnabled) {
            logger.error("---- DEBUG ASSERTION FAILED ----" + System.lineSeparator() + message);
        }
    }

    private static final boolean isAssertEnabled = checkIfAssertIsEnabled();

    public static boolean isAssertEnabled() {
        return isAssertEnabled;
    }

    @SuppressWarnings({"ConstantConditions", "AssertWithSideEffects"})
    private static boolean checkIfAssertIsEnabled() {
        boolean isEnabled = false;
        assert isEnabled = true;
        return isEnabled;
    }
}
