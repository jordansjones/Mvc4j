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

package nextmethod.web.razor.generator.internal;

import javax.annotation.Nonnull;

import nextmethod.annotations.Internal;
import nextmethod.base.Delegates;

@SuppressWarnings("UnusedDeclaration")
@Internal
public abstract class BaseCodeWriter extends CodeWriter {

    @Override
    public void writeSnippet(@Nonnull final String snippet) {
        getInnerWriter().write(snippet);
    }

    @Override
    protected void emitStartMethodInvoke(@Nonnull final String methodName) {
        emitStartMethodInvoke(methodName, new String[0]);
    }

    @Override
    protected void emitStartMethodInvoke(@Nonnull final String methodName, final String... genericArguments) {
        getInnerWriter().write(methodName);
        if (genericArguments != null && genericArguments.length > 0) {
            writeStartGenerics();
            for (int i = 0; i < genericArguments.length; i++) {
                if (i > 0) {
                    writeParameterSeparator();
                }
                writeSnippet(genericArguments[i]);
            }
            writeEndGenerics();
        }

        getInnerWriter().write("(");
    }

    @Override
    protected void emitEndMethodInvoke() {
        getInnerWriter().write(")");
    }

    @Override
    protected void emitEndConstructor() {
        getInnerWriter().write(")");
    }

    @Override
    protected void emitEndLambdaExpression() {
    }

    @Override
    public void writeParameterSeparator() {
        getInnerWriter().write(", ");
    }

    protected <T> void writeCommaSeparatedList(@Nonnull final T[] items,
                                               @Nonnull final Delegates.IAction1<T> writeItemAction
                                              ) {
        for (int i = 0; i < items.length; i++) {
            if (i > 0) {
                getInnerWriter().write(", ");
            }
            writeItemAction.invoke(items[i]);
        }
    }

    protected void writeStartGenerics() {

    }

    protected void writeEndGenerics() {

    }
}
