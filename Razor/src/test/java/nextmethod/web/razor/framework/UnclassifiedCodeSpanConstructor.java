package nextmethod.web.razor.framework;

import nextmethod.web.razor.generator.ExpressionCodeGenerator;
import nextmethod.web.razor.generator.ISpanCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.generator.StatementCodeGenerator;
import nextmethod.web.razor.generator.TypeMemberCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;

import java.util.Set;

public class UnclassifiedCodeSpanConstructor {

	final SpanConstructor self;

	public UnclassifiedCodeSpanConstructor(SpanConstructor self) {
		this.self = self;
	}

	public SpanConstructor asMetaCode() {
		self.getBuilder().setKind(SpanKind.MetaCode);
		return self;
	}

	public SpanConstructor asStatement() {
		return self.with(new StatementCodeGenerator());
	}

	public SpanConstructor asExpression() {
		return self.with(new ExpressionCodeGenerator());
	}

	public SpanConstructor asImplicitExpression(final Set<String> keywords) {
		return asImplicitExpression(keywords, false);
	}

	public SpanConstructor asImplicitExpression(final Set<String> keywords, final boolean acceptTrailingDot) {
		return self.with(new ImplicitExpressionEditHandler(SpanConstructor.testTokenizer, keywords, acceptTrailingDot)).with(new ExpressionCodeGenerator());
	}

	public SpanConstructor asFunctionsBody() {
		return self.with(new TypeMemberCodeGenerator());
	}

	public SpanConstructor asPackageImport(final String pkg, final int pkgKeywordLength) {
		return self.with(new AddImportCodeGenerator(pkg, pkgKeywordLength));
	}

	public SpanConstructor hidden() {
		return self.with(SpanCodeGenerator.Null);
	}

	public SpanConstructor asBaseType(final String baseType) {
		return self.with(new SetBaseTypeCodeGenerator(baseType));
	}

	public SpanConstructor asRazorDirectiveAttribute(final String key, final String value) {
		return self.with(new RazorDirectiveAttributeCodeGenerator(key, value));
	}

	public SpanConstructor as(final ISpanCodeGenerator codeGen) {
		return self.with(codeGen);
	}
}
