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

// TODO
public class SynchronizationContext {

    private boolean notificationRequired;

    private static ThreadLocal<SynchronizationContext> currentContext;

    public SynchronizationContext() {
    }

    SynchronizationContext(final SynchronizationContext context) {
        currentContext.set(context);
    }

    public static SynchronizationContext current() {
        if (currentContext.get() == null) {
            currentContext.set(new SynchronizationContext());
        }
        return currentContext.get();
    }

    public SynchronizationContext createCopy() {
        return new SynchronizationContext(this);
    }

    public boolean isWaitNotificationRequired() {
        return notificationRequired;
    }

    public void operationCompleted() {

    }

    public void operationStarted() {

    }

    public void post(final SendOrPostCallback d, final Object state) {

    }

    public void send(final SendOrPostCallback d, final Object state) {

    }
}
