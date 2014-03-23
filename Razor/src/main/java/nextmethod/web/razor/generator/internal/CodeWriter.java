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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.io.Closeables;
import nextmethod.annotations.Internal;
import nextmethod.base.IDisposable;
import nextmethod.codedom.CodeLinePragma;
import nextmethod.web.razor.text.LocationTagged;

import static nextmethod.base.SystemHelpers.newLine;

@SuppressWarnings("UnusedDeclaration")
@Internal
public abstract class CodeWriter implements IDisposable {

    private StringWriter writer;

    protected CodeWriter() {}

    @SuppressWarnings("UnusedDeclaration")
    private enum WriterMode {
        Constructor,
        MethodCall,
        LambdaDelegate,
        LambdaExpression
    }

    public String getContent() {
        return getInnerWriter().toString();
    }

    public StringWriter getInnerWriter() {
        if (writer == null) {
            writer = new StringWriter();
        }
        return writer;
    }

    public boolean supportsMidStatementLinePragmas() {
        return true;
    }

    public void writeLine() {
        getInnerWriter().write(newLine());
    }

    public abstract void writeParameterSeparator();

    public abstract void writeReturn();

    public abstract void writeLinePragma(@Nonnull final Optional<Integer> lineNumber, final String fileName);

    public abstract void writeHelperHeaderPrefix(@Nonnull final String templateTypeName, final boolean isStatic);

    public abstract void writeSnippet(@Nonnull final String snippet);

    public abstract void writeStringLiteral(@Nonnull final String literal);

    public abstract int writeVariableDeclaration(@Nonnull final String type, @Nonnull final String name,
                                                 @Nullable final String value
                                                );

    public void writeLinePragma() {
        writeLinePragma(null);
    }

    public void writeLinePragma(final CodeLinePragma pragma) {
        if (pragma == null) {
            writeLinePragma(Optional.<Integer>empty(), null);
        }
        else {
            writeLinePragma(Optional.of(pragma.getLineNumber()), pragma.getFileName());
        }
    }

    public void writeHiddenLinePragma() {

    }

    public void writeDisableUnusedFieldWarningPragma() {

    }

    public void writeRestoreUnusedFieldWarningPragma() {

    }

    public void writeIdentifier(@Nonnull final String identifier) {
        getInnerWriter().write(identifier);
    }

    public void writeHelperHeaderSuffix(@Nonnull final String templateTypeName) {

    }

    public void writeHelperTrailer() {

    }

    public void writeStartMethodInvoke(@Nonnull final String methodName) {
        emitStartMethodInvoke(methodName);
    }

    public void writeStartMethodInvoke(@Nonnull final String methodName, @Nonnull final String... genericArguments) {
        emitStartMethodInvoke(methodName, genericArguments);
    }

    public void writeEndMethodInvoke() {
        emitEndMethodInvoke();
    }

    public void writeEndStatement() {

    }

    public void writeStartAssigment(@Nonnull final String variableName) {
        getInnerWriter().write(variableName);
        getInnerWriter().write(" = ");
    }

    public void writeStartLambdaExpression(@Nonnull final String... parameterNames) {
        emitStartLambdaExpression(parameterNames);
    }

    public void writeStartConstructor(@Nonnull final String typeName) {
        emitStartConstructor(typeName);
    }

    public void writeStartLambdaDelegate(@Nonnull final String... parameterNames) {
        emitStartLambdaDelegate(parameterNames);
    }

    public void writeEndLambdaExpression() {
        emitEndLambdaExpression();
    }

    public void writeEndConstructor() {
        emitEndConstructor();
    }

    public void writeEndLambdaDelegate() {
        emitEndLambdaDelegate();
    }

    public void writeLineContinuation() {

    }

    public void writeBooleanLiteral(final boolean value) {
        writeSnippet(String.valueOf(value));
    }

    public void writeLocationTaggedString(@Nonnull final LocationTagged<String> value) {
        writeStartMethodInvoke("nextmethod.base.KeyValue.of");
        writeStringLiteral(value.getValue());
        writeParameterSeparator();
        writeSnippet(String.valueOf(value.getLocation().getAbsoluteIndex()));
        writeEndMethodInvoke();
    }

    @Override
    public void close() {
        dispose(true);
    }

    public void clear() {
        if (getInnerWriter() != null) {
            final StringBuffer buffer = getInnerWriter().getBuffer();
            buffer.delete(0, buffer.length());
        }
    }

//	public CodeSnippetStatement toStatement() {
//		return new CodeSnippetStatement(getContent());
//	}
//
//	public CodeSnippetTypeMember toTypeMember() {
//		return new CodeSnippetTypeMember(getContent());
//	}

    protected abstract void emitStartLambdaDelegate(@Nonnull final String[] parameterNames);

    protected abstract void emitStartLambdaExpression(@Nonnull final String[] parameterNames);

    protected abstract void emitStartConstructor(@Nonnull final String typeName);

    protected abstract void emitStartMethodInvoke(@Nonnull final String methodName);

    protected void emitStartMethodInvoke(@Nonnull final String methodName, final String... genericArguments) {
        emitStartMethodInvoke(methodName);
    }

    protected abstract void emitEndLambdaDelegate();

    protected abstract void emitEndLambdaExpression();

    protected abstract void emitEndConstructor();

    protected abstract void emitEndMethodInvoke();

    protected void dispose(final boolean disposing) {
        if (disposing && writer != null) {
            try {
                Closeables.close(writer, true);
            }
            catch (IOException ignored) {}
        }
    }
}
