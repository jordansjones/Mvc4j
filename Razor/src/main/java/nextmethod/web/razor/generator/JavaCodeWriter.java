package nextmethod.web.razor.generator;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import nextmethod.annotations.TODO;
import nextmethod.base.Delegates;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.StringWriter;

class JavaCodeWriter extends BaseCodeWriter {

	@Override
	protected void writeStartGenerics() {
		getInnerWriter().write("<");
	}

	@Override
	protected void writeEndGenerics() {
		getInnerWriter().write(">");
	}

	@Override
	public int writeVariableDeclaration(@Nonnull final String type, @Nonnull final String name, @Nullable final String value) {
		getInnerWriter().write(type);
		getInnerWriter().write(" ");
		getInnerWriter().write(name);
		getInnerWriter().write(" = ");
		if (!Strings.isNullOrEmpty(value)) {
			getInnerWriter().write(value);
		}
		else {
			getInnerWriter().write("null");
		}
		return 0;
	}

	@Override
	public void writeDisableUnusedFieldWarningPragma() {
		super.writeDisableUnusedFieldWarningPragma();
	}

	@Override
	public void writeRestoreUnusedFieldWarningPragma() {
		super.writeRestoreUnusedFieldWarningPragma();
	}

	@Override
	public void writeStringLiteral(@Nonnull final String literal) {
		final int length = literal.length();
		// If the string is short, use C style quoting (e.g. "\r\n")
		// Also do it if it is too long to fit in one line
		// If the string contains '\0', verbatim style won't work
		if (length >= 256 && length <= 1500 && literal.indexOf('\0') == -1) {
			writeVerbatimStringLiteral(literal, length);
		}
		else {
			writeCStyleStringLiteral(literal, length);
		}
	}

	@TODO
	private void writeVerbatimStringLiteral(@Nonnull final String literal, final int length) {
		// How to do '@"blahblah";' in Java?
		getInnerWriter().write("@\"");
		for (int i = 0; i < length; i++) {
			final char c = literal.charAt(i);
			if (c == '\"') {
				getInnerWriter().write("\"\"");
			}
			else {
				getInnerWriter().write(c);
			}
		}
		getInnerWriter().write("\"");
	}

	private void writeCStyleStringLiteral(@Nonnull final String literal, final int length) {
		final StringWriter writer = getInnerWriter();
		writer.write("\"");
		for (int i = 0; i < length; i++) {
			final char c = literal.charAt(i);
			switch(c) {
				case '\r':
					writer.write("\\r");
					break;
				case '\t':
					writer.write("\\t");
					break;
				case '\"':
					writer.write("\\\"");
					break;
				case '\'':
					writer.write("\\\'");
					break;
				case '\\':
					writer.write("\\\\");
					break;
				case '\0':
					writer.write("\\\0");
					break;
				case '\n':
					writer.write("\\n");
					break;
				case '\u2028':
				case '\u2029':
					writer.write("\\u");
					writer.write(String.format("%X", (int) c));
					break;
				default:
					writer.write(c);
					break;
			}
			if (i > 0 && i % 80 == 0) {
				// If current character is a high surrogate and the following
				// character is a low surrogate, dont' break them.
				// Otherwise when we write the string to a file, we might lose
				// the characters
				if (Character.isHighSurrogate(c)
					&& (i < length - 1)
					&& Character.isLowSurrogate(c)
					) {
					writer.write(literal.charAt(++i));
				}
				writer.write("\" +");
				writeLine();
				writer.write('\"');
			}
			writer.write("\"");
		}
	}

	@Override
	public void writeEndStatement() {
		getInnerWriter().write(";");
	}

	@Override
	public void writeIdentifier(@Nonnull final String identifier) {
		getInnerWriter().write("@" + identifier);
	}

	@Override
	public void writeBooleanLiteral(final boolean value) {
		getInnerWriter().write(String.valueOf(value));
	}

	@Override
	protected void emitStartLambdaExpression(@Nonnull final String[] parameterNames) {
		final StringWriter innerWriter = getInnerWriter();
		final boolean hasParams = parameterNames.length == 0 || parameterNames.length > 1;
		if (hasParams) {
			innerWriter.write("(");
		}
		writeCommaSeparatedList(parameterNames, new Delegates.IAction1<String>() {
			@Override
			public void invoke(@Nullable final String input) {
				innerWriter.write(input);
			}
		});
		if (hasParams) {
			innerWriter.write(")");
		}
		innerWriter.write(" => ");
	}

	@Override
	protected void emitStartLambdaDelegate(@Nonnull final String[] parameterNames) {
		emitStartLambdaExpression(parameterNames);
		getInnerWriter().write("{");
	}

	@Override
	protected void emitEndLambdaDelegate() {
		getInnerWriter().write("}");
	}

	@Override
	protected void emitStartConstructor(@Nonnull final String typeName) {
		getInnerWriter().write("new ");
		getInnerWriter().write(typeName);
		getInnerWriter().write("(");
	}

	@Override
	public void writeReturn() {
		getInnerWriter().write("return ");
	}

	@Override
	public void writeLinePragma(@Nonnull final Optional<Integer> lineNumber, final String fileName) {
		final StringWriter innerWriter = getInnerWriter();
		writeLine();
		if (lineNumber.isPresent()) {
			innerWriter.write("#line ");
			innerWriter.write(lineNumber.get());
			innerWriter.write(" \"");
			innerWriter.write(fileName);
			innerWriter.write("\"");
			writeLine();
		}
		else {
			innerWriter.write("#line default");
			innerWriter.write("#line hidden");
		}
	}

	@Override
	public void writeHiddenLinePragma() {
		getInnerWriter().write("#line hidden");
	}

	@Override
	public void writeHelperHeaderPrefix(@Nonnull final String templateTypeName, final boolean isStatic) {
		final StringWriter innerWriter = getInnerWriter();
		innerWriter.write("public ");
		if (isStatic) {
			innerWriter.write("static ");
		}
		innerWriter.write(templateTypeName);
		innerWriter.write(" ");
	}
}