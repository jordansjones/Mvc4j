package nextmethod.web.razor.generator;

import com.google.common.base.Optional;
import com.google.common.io.Closeables;
import nextmethod.annotations.TODO;
import nextmethod.base.IDisposable;

import java.io.StringWriter;

abstract class CodeWriter implements IDisposable {

	private StringWriter writer;

	protected CodeWriter() {}

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

	public abstract void writeParameterSeparator();
	public abstract void writeReturn();
	public abstract void writeLinePragma(final Optional<Integer> lineNumber, final String fileName);
	public abstract void writeHelperHeaderPrefix(final String templateTypeName, final boolean isStatic);
	public abstract void writeSnippet(final String snippet);
	public abstract void writeStringLiteral(final String literal);
	public abstract int writeVariableDeclaration(final String type, final String name, final String value);

	public void writeLinePragma() {
		writeLinePragma(null);
	}

	@TODO
	public void writeLinePragma(final /*CodeLinePragma*/Object pragma) {
		if (pragma == null) {
			writeLinePragma(Optional.<Integer>absent(), null);
		}
		else {
//			writeLinePragma(pragma.getLineNumber(), pragma.getFileName());
		}
	}

	public void writeHiddenLinePragma() {

	}

	public void writeDisableUnusedFieldWarningPragma() {

	}

	public void writeRestoreUnusedFieldWarningPragma() {

	}

	public void writeIdentifier(final String identifier) {
		getInnerWriter().write(identifier);
	}

	public void writeHelperHeaderSuffix(final String templateTypeName) {

	}

	public void writeHelperTrailer() {

	}

	public void writeStartMethodInvoke(final String methodName) {
		emitStartMethodInvoke(methodName);
	}

	public void writeStartMethodInvoke(final String methodName, final String... genericArguments) {
		emitStartMethodInvoke(methodName, genericArguments);
	}

	public void writeEndMethodInvoke() {
		emitEndMethodInvoke();
	}

	public void writeEndStatement() {

	}

	public void writeStartAssigment(final String variableName) {
		getInnerWriter().write(variableName);
		getInnerWriter().write(" = ");
	}

	public void writeStartLambdaExpression(final String... parameterNames) {
		emitStartLambdaExpression(parameterNames);
	}

	public void writeStartConstructor(final String typeName) {
		emitStartConstructor(typeName);
	}

	public void writeStartLambdaDelegate(final String... parameterNames) {
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

	protected abstract void emitStartLambdaDelegate(final String[] parameterNames);
	protected abstract void emitStartLambdaExpression(final String[] parameterNames);
	protected abstract void emitStartConstructor(final String typeName);
	protected abstract void emitStartMethodInvoke(final String methodName);

	protected void emitStartMethodInvoke(final String methodName, final String... genericArguments) {
		emitStartMethodInvoke(methodName);
	}

	protected abstract void emitEndLambdaDelegate();
	protected abstract void emitEndLambdaExpression();
	protected abstract void emitEndConstructor();
	protected abstract void emitEndMethodInvoke();

	protected void dispose(final boolean disposing) {
		if (disposing && writer != null) {
			Closeables.closeQuietly(writer);
		}
	}
}
