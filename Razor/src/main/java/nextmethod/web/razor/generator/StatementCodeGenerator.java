package nextmethod.web.razor.generator;

import com.google.common.base.Strings;
import nextmethod.base.Delegates;
import nextmethod.web.razor.generator.internal.CodeWriter;
import nextmethod.web.razor.parser.SyntaxConstants;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;

import javax.annotation.Nonnull;

import static nextmethod.base.TypeHelpers.typeIs;

public class StatementCodeGenerator extends SpanCodeGenerator {

	@Override
	public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
		context.flushBufferedStatement();
		String generatedCode = context.buildCodeString(new Delegates.IAction1<CodeWriter>() {
			@Override
			public void invoke(CodeWriter input) {
				input.writeSnippet(target.getContent());
			}
		});
		int startGeneratedCode = target.getStart().getCharacterIndex();
		generatedCode = pad(generatedCode, target);

		// Is this the span immediately following "@"?
		if (context.getHost().isDesignTimeMode()
			&& !Strings.isNullOrEmpty(generatedCode)
			&& Character.isWhitespace(generatedCode.charAt(0))
			&& target.getPrevious() != null
			&& target.getPrevious().getKind() == SpanKind.Transition
			&& SyntaxConstants.TransitionString.equals(target.getPrevious().getContent())
		) {
			generatedCode = generatedCode.substring(1);
			startGeneratedCode--;
		}

		context.addStatement(generatedCode, context.generateLinePragma(target, startGeneratedCode));
	}

	@Override
	public String toString() {
		return "Stmt";
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object obj) {
		return (obj != null) && typeIs(obj, StatementCodeGenerator.class);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
