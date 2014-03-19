package nextmethod.web.razor.generator;

import nextmethod.base.Delegates;
import nextmethod.codedom.CodeSnippetTypeMember;
import nextmethod.web.razor.generator.internal.CodeWriter;
import nextmethod.web.razor.parser.syntaxtree.Span;

import javax.annotation.Nonnull;

import static nextmethod.base.TypeHelpers.typeIs;

public class TypeMemberCodeGenerator extends SpanCodeGenerator {

	@Override
	public void generateCode(@Nonnull final Span target, @Nonnull final CodeGeneratorContext context) {
		final String generatedCode = context.buildCodeString(input -> {
			input.writeSnippet(target.getContent());
		});

		final CodeSnippetTypeMember member = new CodeSnippetTypeMember(pad(generatedCode, target));
		member.setLinePragma(context.generateLinePragma(target, target.getStart().getCharacterIndex()));
		context.getGeneratedClass().getMembers().add(member);
	}

	@Override
	public String toString() {
		return "TypeMember";
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object obj) {
		return obj != null && typeIs(obj, TypeMemberCodeGenerator.class);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
