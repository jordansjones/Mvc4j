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

package nextmethod.web.razor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class StateMachine<TReturn> {

    protected State currentState;

    protected abstract State getStartState();

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(@Nonnull final State currentState) {
        this.currentState = currentState;
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    protected TReturn turn() {
        if (getCurrentState() != null) {
            StateResult result;
            do {
                // Keep running until we value a null result or output
                result = getCurrentState().invoke();
                setCurrentState(result.getNext());
            }
            while (result != null && !result.isHasOutput());

            if (result == null) {
                return null;
            }
            return result.getOutput();
        }
        return null;
    }

    protected StateResult stop() {
        return null;
    }

    protected StateResult transition(@Nonnull final State newState) {
        return new StateResult(newState);
    }

    protected StateResult transition(@Nonnull final TReturn output, @Nonnull final State newState) {
        return new StateResult(output, newState);
    }

    protected StateResult stay() {
        return new StateResult(getCurrentState());
    }

    protected StateResult stay(@Nullable final TReturn output) {
        return new StateResult(output, getCurrentState());
    }

    protected class StateResult {

        private TReturn output;
        private boolean hasOutput;
        private State next;

        public StateResult(@Nonnull final State next) {
            this.hasOutput = false;
            this.next = next;
        }

        public StateResult(@Nullable final TReturn output, @Nonnull final State next) {
            this.hasOutput = true;
            this.output = output;
            this.next = next;
        }

        public boolean isHasOutput() {
            return hasOutput;
        }

        public void setHasOutput(boolean hasOutput) {
            this.hasOutput = hasOutput;
        }

        public State getNext() {
            return next;
        }

        public void setNext(@Nonnull final State next) {
            this.next = next;
        }

        public TReturn getOutput() {
            return output;
        }

        public void setOutput(@Nullable final TReturn output) {
            this.output = output;
        }
    }

}
