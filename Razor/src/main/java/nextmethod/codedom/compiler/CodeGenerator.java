package nextmethod.codedom.compiler;

import nextmethod.base.NotImplementedException;
import nextmethod.codedom.*;

import javax.annotation.Nonnull;
import java.io.PrintWriter;
import java.math.BigDecimal;

import static nextmethod.base.TypeHelpers.typeIs;

/**
 *
 */
public abstract class CodeGenerator implements ICodeGenerator {

	private final Visitor visitor;
	private CodeGeneratorOptions options;
	private CodeTypeMember currentMember;
	private CodeTypeDeclaration currentType;


	protected CodeGenerator() {
		this.visitor = new Visitor(this);
	}

	protected CodeTypeDeclaration getCurrentClass() {
		return currentType;
	}

	protected CodeTypeMember getCurrentMember() {
		return currentMember;
	}

	protected String getCurrentMemberName() {
		return currentMember == null
			? "<% unknown %>"
			: currentMember.getName();
	}

	protected String getCurrentTypeName() {
		return currentType == null
			? "<% unknown %>"
			: currentType.getName();
	}

	protected int getIndent() {
		throw new NotImplementedException();
	}

	protected void setIndent(final int indent) {
		throw new NotImplementedException();
	}

	protected boolean isCurrentClass() {
		return currentType != null && (currentType.isClass() && !isCurrentDelegate());
	}

	protected boolean isCurrentDelegate() {
		return typeIs(currentType, CodeTypeDelegate.class);
	}

	protected boolean isCurrentEnum() {
		return currentType != null && currentType.isEnum();
	}

	protected boolean isCurrentInterface() {
		return currentType != null && currentType.isInterface();
	}

	protected boolean isCurrentStruct() {
		return false;
	}

	protected abstract String getNullToken();

	protected CodeGeneratorOptions getOptions() {
		return options;
	}

	protected PrintWriter getOutput() {
		throw new NotImplementedException();
	}

	protected void continueOnNewLine(@Nonnull final String st) {
		getOutput().println(st);
	}

	//
	// Code Generation methods
	//
	protected abstract void generateArgumentReferenceExpression(@Nonnull final CodeArgumentReferenceExpression e);

	protected abstract void generateArrayCreateExpression(@Nonnull final CodeArrayCreateExpression e);

	protected abstract void generateArrayIndexerExpression(@Nonnull final CodeArrayIndexerExpression e);

	protected abstract void generateAssignStatement(@Nonnull final CodeAssignStatement s);

	protected abstract void generateAttachEventStatement(@Nonnull final CodeAttachEventStatement s);

	protected abstract void generateAnnotationDeclarationsStart(@Nonnull final CodeAnnotationDeclarationCollection attributes);

	protected abstract void generateAnnotationDeclarationsEnd(@Nonnull final CodeAnnotationDeclarationCollection attributes);

	protected abstract void generateBaseReferenceExpression(@Nonnull final CodeBaseReferenceExpression e);

	protected void generateBinaryOperatorExpression(@Nonnull final CodeBinaryOperatorExpression e) {
		throw new NotImplementedException();
	}


	protected abstract void generateCastExpression(@Nonnull final CodeCastExpression e);

	public void generateCodeFromMember(@Nonnull final CodeTypeMember member, @Nonnull final PrintWriter writer, @Nonnull final CodeGeneratorOptions options) {
		throw new NotImplementedException();
	}

	protected abstract void generateComment(@Nonnull final CodeComment comment);

	protected void generateCommentStatement(@Nonnull final CodeCommentStatement statement) {
		throw new NotImplementedException();
	}

	protected void generateCommentStatements(@Nonnull final CodeCommentStatementCollection statements) {
		throw new NotImplementedException();
	}

	protected void generateCompileUnit(@Nonnull final CodeCompileUnit compileUnit) {
		throw new NotImplementedException();
	}

	protected void generateCompileUnitEnd(@Nonnull final CodeCompileUnit compileUnit) {
		throw new NotImplementedException();
	}

	protected void generateCompileUnitStart(@Nonnull final CodeCompileUnit compileUnit) {
		throw new NotImplementedException();
	}

	protected abstract void generateConditionStatement(@Nonnull final CodeConditionStatement s);

	protected abstract void generateConstructor(@Nonnull final CodeConstructor x, @Nonnull final CodeTypeDeclaration d);

	protected void generateDecimalValue(@Nonnull final BigDecimal d) {
		throw new NotImplementedException();
	}


	protected void generateDefaultValueExpression(@Nonnull final CodeDefaultValueExpression e) {
		throw new NotImplementedException();
	}

	protected abstract void generateDelegateCreateExpression(@Nonnull final CodeDelegateCreateExpression e);

	protected abstract void generateDelegateInvokeExpression(@Nonnull final CodeDelegateInvokeExpression e);

	protected void generateDirectionExpression(@Nonnull final CodeDirectionExpression e) {
		throw new NotImplementedException();
	}

	protected void generateDoubleValue(@Nonnull final Double d) {
		throw new NotImplementedException();
	}

	protected abstract void generateEntryPointMethod(@Nonnull final CodeEntryPointMethod m, @Nonnull final CodeTypeDeclaration d);

	protected abstract void generateEvent(@Nonnull final CodeMemberEvent ev, @Nonnull final CodeTypeDeclaration d);

	protected abstract void generateEventReferenceExpression(@Nonnull final CodeEventReferenceExpression e);

	protected void generateExpression(@Nonnull final CodeExpression e) {
		throw new NotImplementedException();
	}

	protected abstract void generateExpressionStatement(@Nonnull final CodeExpressionStatement statement);

	protected abstract void generateField(@Nonnull final CodeMemberField f);

	protected abstract void generateFieldReferenceExpression(@Nonnull final CodeFieldReferenceExpression e);

	protected abstract void generateGotoStatement(@Nonnull final CodeGotoStatement statement);

	protected abstract void generateIndexerExpression(@Nonnull final CodeIndexerExpression e);

	protected abstract void generateIterationStatement(@Nonnull final CodeIterationStatement s);

	protected abstract void generateLabeledStatement(@Nonnull final CodeLabeledStatement statement);

	protected abstract void generateLinePragmaStart(@Nonnull final CodeLinePragma p);

	protected abstract void generateLinePragmaEnd(@Nonnull final CodeLinePragma p);

	protected abstract void generateMethod(@Nonnull final CodeMemberMethod m, @Nonnull final CodeTypeDeclaration d);

	protected abstract void generateMethodInvokeExpression(@Nonnull final CodeMethodInvokeExpression e);

	protected abstract void generateMethodReferenceExpression(@Nonnull final CodeMethodReferenceExpression e);

	protected abstract void generateMethodReturnStatement(@Nonnull final CodeMethodReturnStatement e);

	protected void generateNamespace(@Nonnull final CodePackage ns) {
		throw new NotImplementedException();
	}

	protected abstract void generateNamespaceStart(@Nonnull final CodePackage ns);

	protected abstract void generateNamespaceEnd(@Nonnull final CodePackage ns);

	protected abstract void generateNamespaceImport(@Nonnull final CodePackageImport i);

	protected void generateNamespaceImports(@Nonnull final CodePackage e) {
		throw new NotImplementedException();
	}

	protected void generateNamespaces(@Nonnull final CodeCompileUnit e) {
		throw new NotImplementedException();
	}

	protected abstract void generateObjectCreateExpression(@Nonnull final CodeObjectCreateExpression e);

	protected void generateParameterDeclarationExpression(@Nonnull final CodeParameterDeclarationExpression e) {
		throw new NotImplementedException();
	}

	protected void generatePrimitiveExpression(@Nonnull final CodePrimitiveExpression e) {
		throw new NotImplementedException();
	}

	protected abstract void generateProperty(@Nonnull final CodeMemberProperty p, @Nonnull final CodeTypeDeclaration d);

	protected abstract void generatePropertyReferenceExpression(@Nonnull final CodePropertyReferenceExpression e);

	protected abstract void generatePropertySetValueReferenceExpression(@Nonnull final CodePropertySetValueReferenceExpression e);

	protected abstract void generateRemoveEventStatement(@Nonnull final CodeRemoveEventStatement statement);

	protected void generateSingleFloatValue(@Nonnull final Float s) {
		throw new NotImplementedException();
	}

	protected void generateSnippetCompileUnit(@Nonnull final CodeSnippetCompileUnit e) {
		throw new NotImplementedException();

	}

	protected abstract void generateSnippetExpression(@Nonnull final CodeSnippetExpression e);

	protected abstract void generateSnippetMember(@Nonnull final CodeSnippetTypeMember m);

	protected void generateSnippetStatement(@Nonnull final CodeSnippetStatement s) {
		throw new NotImplementedException();
	}

	protected void generateStatement(@Nonnull final CodeStatement s) {
		throw new NotImplementedException();
	}

	protected void generateStatements(@Nonnull final CodeStatementCollection c) {
		throw new NotImplementedException();
	}

	protected abstract void generateThisReferenceExpression(@Nonnull final CodeThisReferenceExpression e);

	protected abstract void generateThrowExceptionStatement(@Nonnull final CodeThrowExceptionStatement s);

	protected abstract void generateTryCatchFinallyStatement(@Nonnull final CodeTryCatchFinallyStatement s);

	protected abstract void generateTypeEnd(@Nonnull final CodeTypeDeclaration declaration);

	protected abstract void generateTypeConstructor(@Nonnull final CodeTypeConstructor constructor);

	protected void generateTypeOfExpression(@Nonnull final CodeTypeOfExpression e) {
		throw new NotImplementedException();
	}

	protected void generateTypeReferenceExpression(@Nonnull final CodeTypeReferenceExpression e) {
		throw new NotImplementedException();
	}

	protected void generateTypes(@Nonnull final CodePackage e) {
		throw new NotImplementedException();
	}

	protected abstract void generateTypeStart(@Nonnull final CodeTypeDeclaration declaration);

	protected abstract void generateVariableDeclarationStatement(@Nonnull final CodeVariableDeclarationStatement e);

	protected abstract void generateVariableReferenceExpression(@Nonnull final CodeVariableReferenceExpression e);

	//
	// Other members
	//

	protected void outputAnnotationArgument(@Nonnull final CodeAnnotationArgument argument) {
		throw new NotImplementedException();
	}

	private void outputAnnotationDeclaration(@Nonnull final CodeAnnotationDeclaration annotation) {
		throw new NotImplementedException();
	}

	protected void outputAnnotationDeclarations(@Nonnull final CodeAnnotationDeclarationCollection annotations) {
		throw new NotImplementedException();
	}

	protected void outputDirection(@Nonnull final FieldDirection direction) {
		throw new NotImplementedException();
	}

	protected void outputExpressionList(@Nonnull final CodeExpressionCollection expressions) {
		throw new NotImplementedException();
	}

	protected void outputExpressionList(@Nonnull final CodeExpressionCollection expressions, boolean newLineBetweenItems) {
		throw new NotImplementedException();
	}

	protected void outputFieldScopeModifier(@Nonnull final MemberAttributes attributes) {
		throw new NotImplementedException();
	}

	protected void outputIdentifier(@Nonnull final String ident) {
		throw new NotImplementedException();
	}

	protected void outputMemberAccessModifier(@Nonnull final MemberAttributes attributes) {
		throw new NotImplementedException();
	}

	protected void outputMemberScopeModifier(@Nonnull final MemberAttributes attributes) {
		throw new NotImplementedException();
	}

	protected void outputOperator(@Nonnull final CodeBinaryOperatorType op) {
		throw new NotImplementedException();
	}

	protected void outputParameters(@Nonnull final CodeParameterDeclarationExpressionCollection parameters) {
		throw new NotImplementedException();
	}

	protected abstract void outputType(@Nonnull final CodeTypeReference t);

	protected void outputTypeAttributes(@Nonnull final TypeAttributes attributes, boolean isStruct, boolean isEnum) {
		throw new NotImplementedException();
	}

	protected void outputTypeNamePair(@Nonnull final CodeTypeReference type, @Nonnull final String name) {
		throw new NotImplementedException();
	}

	protected abstract String quoteSnippetString(@Nonnull final String value);

	protected void initOutput(@Nonnull final PrintWriter output, @Nonnull final CodeGeneratorOptions options) {
		throw new NotImplementedException();
	}

	protected void generateCodeFromCompileUnit(@Nonnull final CodeCompileUnit compileUnit, @Nonnull final PrintWriter output, @Nonnull final CodeGeneratorOptions options) {
		throw new NotImplementedException();
	}

	protected void generateCodeFromExpression(@Nonnull final CodeExpression expression, @Nonnull final PrintWriter output, @Nonnull final CodeGeneratorOptions options) {
		throw new NotImplementedException();
	}

	protected void generateCodeFromNamespace(@Nonnull final CodePackage pkg, @Nonnull final PrintWriter output, @Nonnull final CodeGeneratorOptions options) {
		throw new NotImplementedException();
	}

	protected void generateCodeFromStatement(@Nonnull final CodeStatement statement, @Nonnull final PrintWriter output, @Nonnull final CodeGeneratorOptions options) {
		throw new NotImplementedException();
	}

	protected void generateCodeFromType(@Nonnull final CodeTypeDeclaration type, @Nonnull final PrintWriter output, @Nonnull final CodeGeneratorOptions options) {
		throw new NotImplementedException();
	}

	protected void generateType(@Nonnull final CodeTypeDeclaration type) {
		throw new NotImplementedException();
	}

	public static boolean isValidLanguageIndependentIdentifier(@Nonnull final String value) {
		throw new NotImplementedException();
	}

	@Override
	public void validateIdentifier(@Nonnull final String value) {
		throw new NotImplementedException();
	}


	public static void validateIdentifiers(@Nonnull final CodeObject e) {
		throw new NotImplementedException();
	}

	// The position in the array determines the order in which those
	// kind of CodeTypeMembers are generated. Less is more ;-)
	static Class<?>[] memberTypes = {
		CodeMemberField.class,
		CodeSnippetTypeMember.class,
		CodeTypeConstructor.class,
		CodeConstructor.class,
		CodeMemberProperty.class,
		CodeMemberEvent.class,
		CodeMemberMethod.class,
		CodeTypeDeclaration.class,
		CodeEntryPointMethod.class
	};

	protected void generateDirectives(@Nonnull final CodeDirectiveCollection directives) {
		throw new NotImplementedException();
	}

}
