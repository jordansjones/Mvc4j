package nextmethod.web.razor.generator;

import com.google.common.base.Strings;
import nextmethod.annotations.TODO;
import nextmethod.web.razor.parser.syntaxtree.Span;

import javax.annotation.Nonnull;
import java.util.Objects;

import static nextmethod.base.TypeHelpers.typeAs;

public class AddImportCodeGenerator extends SpanCodeGenerator {

	private final String pkg;
	private int keywordLength;

	public AddImportCodeGenerator(@Nonnull final String pkg, final int keywordLength) {
		this.pkg = pkg;
		this.keywordLength = keywordLength;
	}

	@Override
	@TODO
	public void generateCode(@Nonnull Span target, @Nonnull CodeGeneratorContext context) {
		// Try to find the package in the existing imports
		String pkg = this.pkg;
		if (!Strings.isNullOrEmpty(pkg) && Character.isWhitespace(pkg.charAt(0))) {
			pkg = pkg.substring(1);
		}

//		final CodePackage imprt = context.getCodePackage();
//		imprt.getImports();
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
