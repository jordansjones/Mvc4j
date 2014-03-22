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

import javax.annotation.Nullable;

public final class Delegates {

    private Delegates() {}

    /**
     * Encapsulates a method that has no parameters and does not return a value;
     */
    public static interface IAction {

        void invoke();
    }

    /**
     * Encapsulates a method that has a single parameter and does not return a value.
     *
     * @param <T1> The type of the parameter of the method that this delegate encapsulates.
     */
    public static interface IAction1<T1> {

        void invoke(@Nullable final T1 input);
    }

    /**
     * Encapsulates a method that has 2 parameters and does not return a value.
     *
     * @param <T1> The type of the first parameter of the method that this delegate encapsulates
     * @param <T2> The type of the second parameter of the method that this delegate encapsulates
     */
    public static interface IAction2<T1, T2> {

        void invoke(@Nullable final T1 input1, @Nullable final T2 input2);
    }

    /**
     * Encapsulates a method that has 3 parameters and does not return a value.
     *
     * @param <T1> The type of the first parameter of the method that this delegate encapsulates
     * @param <T2> The type of the second parameter of the method that this delegate encapsulates
     * @param <T3> The type of the third parameter of the method that this delegate encapsulates
     */
    public static interface IAction3<T1, T2, T3> {

        void invoke(@Nullable final T1 input1, @Nullable final T2 input2, @Nullable final T3 input3);
    }


    /**
     * Encapsulates a method that has no parameters and returns a value of the type specified by the <code>TResult</code> parameter.
     *
     * @param <TResult> The type of the return value of the method that this delegate encapsulates.
     */
    public static interface IFunc<TResult> {

        @Nullable
        TResult invoke();
    }

    /**
     * Encapsulates a method that has one parameter and returns a value of the type specified by the <code>TResult</code> parameter.
     *
     * @param <T1>      The type of the parameter of the method that this delegate encapsulates.
     * @param <TResult> The type of the return value of the method that this delegate encapsulates.
     */
    public static interface IFunc1<T1, TResult> {

        @Nullable
        TResult invoke(@Nullable final T1 input1);
    }

    /**
     * Encapsulates a method that has two parameters and returns a value of the type specified by the <code>TResult</code> parameter.
     *
     * @param <T1>      The type of the first parameter of the method that this delegate encapsulates.
     * @param <T2>      The type of the second parameter of the method that this delegate encapsulates.
     * @param <TResult> The type of the return value of the method that this delegate encapsulates.
     */
    public static interface IFunc2<T1, T2, TResult> {

        @Nullable
        TResult invoke(@Nullable final T1 input1, @Nullable final T2 input2);
    }

    /**
     * Encapsulates a method that has three parameters and returns a value of the type specified by the <code>TResult</code> parameter.
     *
     * @param <T1>      The type of the first parameter of the method that this delegate encapsulates.
     * @param <T2>      The type of the second parameter of the method that this delegate encapsulates.
     * @param <T3>      The type of the third parameter of the method that this delegate encapsulates.
     * @param <TResult> The type of the return value of the method that this delegate encapsulates.
     */
    public static interface IFunc3<T1, T2, T3, TResult> {

        @Nullable
        TResult invoke(@Nullable final T1 input1, @Nullable final T2 input2, @Nullable final T3 input3);
    }

}
