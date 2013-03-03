package nextmethod.web.razor.generator.internal;

import nextmethod.annotations.Internal;
import nextmethod.base.Delegates;

import javax.annotation.Nonnull;

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

	protected <T> void writeCommaSeparatedList(@Nonnull final T[] items, @Nonnull final Delegates.IAction1<T> writeItemAction) {
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
