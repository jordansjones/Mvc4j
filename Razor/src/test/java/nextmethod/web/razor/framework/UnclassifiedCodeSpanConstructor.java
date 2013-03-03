package nextmethod.web.razor.framework;

import nextmethod.base.NotImplementedException;
import nextmethod.web.razor.editor.ImplicitExpressionEditorHandler;
import nextmethod.web.razor.generator.AddImportCodeGenerator;
import nextmethod.web.razor.generator.ExpressionCodeGenerator;
import nextmethod.web.razor.generator.ISpanCodeGenerator;
import nextmethod.web.razor.generator.RazorDirectiveAnnotationCodeGenerator;
import nextmethod.web.razor.generator.SetBaseTypeCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.generator.StatementCodeGenerator;
import nextmethod.web.razor.generator.TypeMemberCodeGenerator;
import nextmethod.web.razor.parser.ParserVisitor;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;
import java.util.Set;

public class UnclassifiedCodeSpanConstructor extends SyntaxTreeNode implements ISpanConstructor {

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
		return self.with(new ImplicitExpressionEditorHandler(SpanConstructor.testTokenizer, keywords, acceptTrailingDot)).with(new ExpressionCodeGenerator());
	}

	public SpanConstructor asFunctionsBody() {
		return self.with(new TypeMemberCodeGenerator());
	}

	public SpanConstructor asPackageImport(final String pkg, final int pkgKeywordLength) {
		return self.with(new AddImportCodeGenerator(pkg, pkgKeywordLength));
	}

	public SpanConstructor asNamespaceImport(final String pkg, final int pkgKeywordLength) {
		return asPackageImport(pkg, pkgKeywordLength);
	}

	public SpanConstructor hidden() {
		return self.with(SpanCodeGenerator.Null);
	}

	public SpanConstructor asBaseType(final String baseType) {
		return self.with(new SetBaseTypeCodeGenerator(baseType));
	}

	public SpanConstructor asRazorDirectiveAnnotation(final String key, final String value) {
		return self.with(new RazorDirectiveAnnotationCodeGenerator(key, value));
	}

	public SpanConstructor as(final ISpanCodeGenerator codeGen) {
		return self.with(codeGen);
	}

	@Override
	public Span build() {
		return self.build();
	}

	@Override
	public boolean isBlock() {
		throw new NotImplementedException();
	}

	@Override
	public int getLength() {
		throw new NotImplementedException();
	}

	@Override
	public SourceLocation getStart() {
		throw new NotImplementedException();
	}

	@Override
	public void accept(@Nonnull final ParserVisitor visitor) {
		throw new NotImplementedException();
	}

	@Override
	public boolean equivalentTo(@Nonnull final SyntaxTreeNode node) {
		throw new NotImplementedException();
	}
}
