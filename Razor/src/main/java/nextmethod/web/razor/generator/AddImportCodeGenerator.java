package nextmethod.web.razor.generator;

import nextmethod.base.Strings;
import nextmethod.codedom.CodePackage;
import nextmethod.codedom.CodePackageImport;
import nextmethod.web.razor.parser.syntaxtree.Span;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

import static nextmethod.base.TypeHelpers.typeAs;

public class AddImportCodeGenerator extends SpanCodeGenerator {

	private final String pkg;
	private int keywordLength;

	public AddImportCodeGenerator(@Nonnull final String pkg, final int keywordLength) {
		this.pkg = pkg;
		this.keywordLength = keywordLength;
	}

	@Override
	public void generateCode(@Nonnull Span target, @Nonnull CodeGeneratorContext context) {
		// Try to find the package in the existing imports
		String pkg = this.pkg;
		if (!Strings.isNullOrEmpty(pkg) && Character.isWhitespace(pkg.charAt(0))) {
			pkg = pkg.substring(1);
		}

		final String importPackage = pkg;

		final CodePackage imprt = context.getCodePackage();
		final Optional<CodePackageImport> importOptional = imprt.getImports()
			.stream()
			.filter(x -> Strings.nullToEmpty(x.getPackage()).equals(importPackage.trim()))
			.findFirst();

		final CodePackageImport codePackageImport;
		if (!importOptional.isPresent())
		{
			codePackageImport = new CodePackageImport(importPackage);
			context.getCodePackage().getImports().add(codePackageImport);
		}
		else
		{
			codePackageImport = importOptional.get();
		}

		codePackageImport.setLinePragma(context.generateLinePragma(target));
	}

	public String getPackage() {
		return pkg;
	}

	public int getPackageKeywordLength() {
		return keywordLength;
	}

	public void setPackageKeywordLength(int keywordLength) {
		this.keywordLength = keywordLength;
	}

	@Override
	public String toString() {
		return "Import:" + pkg + ";KwdLen:" + keywordLength;
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object obj) {
		final AddImportCodeGenerator other = typeAs(obj, AddImportCodeGenerator.class);
		return other != null && Objects.equals(pkg, other.pkg) && keywordLength == other.keywordLength;
	}

	@Override
	public int hashCode() {
		return Objects.hash(pkg, keywordLength);
	}
}
