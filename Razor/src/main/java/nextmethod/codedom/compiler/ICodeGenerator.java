package nextmethod.codedom.compiler;

import nextmethod.codedom.CodeCompileUnit;
import nextmethod.codedom.CodeExpression;
import nextmethod.codedom.CodePackage;
import nextmethod.codedom.CodeStatement;
import nextmethod.codedom.CodeTypeDeclaration;
import nextmethod.codedom.CodeTypeReference;

import javax.annotation.Nonnull;
import java.io.Writer;

/**
 *
 */
public interface ICodeGenerator {

	String createEscapedIdentifier(final String value);
	String createValidIdentifier(final String value);

	void generateCodeFromCompileUnit(@Nonnull final CodeCompileUnit compileUnit, @Nonnull final Writer writer, @Nonnull final CodeGeneratorOptions options);
	void generateCodeFromExpression(@Nonnull final CodeExpression codeExpression, @Nonnull final Writer writer, @Nonnull final CodeGeneratorOptions options);
	void generateCodeFromPackage(@Nonnull final CodePackage codePackage, @Nonnull final Writer writer, @Nonnull final CodeGeneratorOptions options);
	void generateCodeFromStatement(@Nonnull final CodeStatement codeStatement, @Nonnull final Writer writer, @Nonnull final CodeGeneratorOptions options);
	void generateCodeFromType(@Nonnull final CodeTypeDeclaration codeTypeDeclaration, @Nonnull final Writer writer, @Nonnull final CodeGeneratorOptions options);

	String getTypeOutput(@Nonnull final CodeTypeReference type);
	boolean isValidIdentifier(@Nonnull final String value);
	boolean supports(@Nonnull final GeneratorSupport supports);
	void validateIdentifier(@Nonnull final String value);
}
