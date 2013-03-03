/*
 * Copyright 2013 Jordan S. Jones <jordansjones@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nextmethod.codedom.compiler;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFormatter;
import nextmethod.base.NotImplementedException;
import nextmethod.base.Strings;
import nextmethod.codedom.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.base.TypeHelpers.typeAs;
import static nextmethod.base.TypeHelpers.typeIs;

/**
 *
 */
public abstract class CodeGenerator implements ICodeGenerator {

	private final Visitor visitor;
	private PrintWriter output;
	private CodeGeneratorOptions options;
	private JCodeModel codeModel;
	private JFormatter formatter;
	private CodeTypeMember currentMember;
	private CodeTypeDeclaration currentType;

	private int indentLevel;


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
		return indentLevel;
	}

	protected void setIndent(final int indent) {
		final int prevLevel = this.indentLevel;
		this.indentLevel = indent;
		while (prevLevel > 0) {
			this.formatter.o();
		}
		for (int i = 0; i < indent; i++) {
			this.formatter.i();
		}
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
		return this.output;
	}

	protected JFormatter getFormatter() {
		return formatter;
	}

	protected JCodeModel getCodeModel() {
		return this.codeModel;
	}

	protected void continueOnNewLine(@Nonnull final String st) {
		formatter.p(st).nl();
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
		formatter.p('(');
		generateExpression(e.getLeft());
		formatter.p(' ');
		outputOperator(e.getOp());
		formatter.p(' ');
		generateExpression(e.getRight());
		formatter.p(')');
	}


	protected abstract void generateCastExpression(@Nonnull final CodeCastExpression e);

	public void generateCodeFromMember(@Nonnull final CodeTypeMember member, @Nonnull final PrintWriter writer, @Nonnull final CodeGeneratorOptions options) {
		throw new NotImplementedException();
	}

	protected abstract void generateComment(@Nonnull final CodeComment comment);

	protected void generateCommentStatement(@Nonnull final CodeCommentStatement statement) {
		generateComment(statement.getComment());
	}

	protected void generateCommentStatements(@Nonnull final CodeCommentStatementCollection statements) {
		for (CodeCommentStatement commentStatement : statements) {
			generateCommentStatement(commentStatement);
		}
	}

	protected void generateCompileUnit(@Nonnull final CodeCompileUnit compileUnit) {
		generateCompileUnitStart(compileUnit);
		for (CodePackage codePackage : compileUnit.getPackages()) {
			if (Strings.isNullOrEmpty(codePackage.getName())) {
				generateNamespace(codePackage);
			}
		}

		// TODO: AssemblyCustomAttributes?

		for (CodePackage codePackage : compileUnit.getPackages()) {
			if (!Strings.isNullOrEmpty(codePackage.getName())) {
				generateNamespace(codePackage);
			}
		}

		generateCompileUnitEnd(compileUnit);
	}

	protected void generateCompileUnitEnd(@Nonnull final CodeCompileUnit compileUnit) {
		if (!compileUnit.getEndDirectives().isEmpty()) {
			generateDirectives(compileUnit.getEndDirectives());
		}
	}

	protected void generateCompileUnitStart(@Nonnull final CodeCompileUnit compileUnit) {
		if (!compileUnit.getStartDirectives().isEmpty()) {
			generateDirectives(compileUnit.getStartDirectives());
			formatter.nl();
		}
	}

	protected abstract void generateConditionStatement(@Nonnull final CodeConditionStatement s);

	protected abstract void generateConstructor(@Nonnull final CodeConstructor x, @Nonnull final CodeTypeDeclaration d);

	protected void generateDecimalValue(@Nonnull final BigDecimal d) {
		JExpr.lit(d.toString()).generate(formatter);
	}


	protected void generateDefaultValueExpression(@Nonnull final CodeDefaultValueExpression e) {
		throw new NotImplementedException();
	}

	protected abstract void generateDelegateCreateExpression(@Nonnull final CodeDelegateCreateExpression e);

	protected abstract void generateDelegateInvokeExpression(@Nonnull final CodeDelegateInvokeExpression e);

	protected void generateDirectionExpression(@Nonnull final CodeDirectionExpression e) {
		generateExpression(e);
	}

	protected void generateDoubleValue(@Nonnull final Double d) {
		JExpr.lit(d).generate(formatter);
	}

	protected abstract void generateEntryPointMethod(@Nonnull final CodeEntryPointMethod m, @Nonnull final CodeTypeDeclaration d);

	protected abstract void generateEvent(@Nonnull final CodeMemberEvent ev, @Nonnull final CodeTypeDeclaration d);

	protected abstract void generateEventReferenceExpression(@Nonnull final CodeEventReferenceExpression e);

	protected void generateExpression(@Nonnull final CodeExpression e) {
		checkArgument(e != null);

		try {
			e.accept(visitor);
		}
		catch (NotImplementedException nie) {
			throw new IllegalArgumentException("Element type is not supported: " + e.getClass().getName());
		}
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
		for (CodeCommentStatement statement : ns.getComments()) {
			generateCommentStatement(statement);
		}

		generatePackageStart(ns);

		for (CodePackageImport anImport : ns.getImports()) {
			final CodeLinePragma linePragma = anImport.getLinePragma();
			if (linePragma != null) {
				generateLinePragmaStart(linePragma);
			}
			generatePackageImport(anImport);

			if (linePragma != null) {
				generateLinePragmaEnd(linePragma);
			}
		}

		formatter.nl();

		generateTypes(ns);

		generatePackageEnd(ns);
	}

	protected abstract void generatePackageStart(@Nonnull final CodePackage ns);

	protected abstract void generatePackageEnd(@Nonnull final CodePackage ns);

	protected abstract void generatePackageImport(@Nonnull final CodePackageImport i);

	protected void generatePackageImports(@Nonnull final CodePackage e) {
		for (CodePackageImport codePackageImport : e.getImports()) {
			CodeLinePragma linePragma = codePackageImport.getLinePragma();
			if (linePragma != null) {
				generateLinePragmaStart(linePragma);
			}

			generatePackageImport(codePackageImport);

			if (linePragma != null) {
				generateLinePragmaEnd(linePragma);
			}
		}
	}

	protected void generatePackages(@Nonnull final CodeCompileUnit e) {
		for (CodePackage codePackage : e.getPackages()) {
			generateNamespace(codePackage);
		}
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
		JExpr.lit(s).generate(this.formatter);
	}

	protected void generateSnippetCompileUnit(@Nonnull final CodeSnippetCompileUnit e) {
		final CodeLinePragma linePragma = checkNotNull(e).getLinePragma();

		if (linePragma != null) {
			generateLinePragmaStart(linePragma);
		}

		formatter.p(e.getValue()).nl();

		if (linePragma != null) {
			generateLinePragmaEnd(linePragma);
		}
	}

	protected abstract void generateSnippetExpression(@Nonnull final CodeSnippetExpression e);

	protected abstract void generateSnippetMember(@Nonnull final CodeSnippetTypeMember m);

	protected void generateSnippetStatement(@Nonnull final CodeSnippetStatement s) {
		formatter.p(s.getValue()).nl();
	}

	protected void generateStatement(@Nonnull final CodeStatement s) {
		final CodeLinePragma linePragma = s.getLinePragma();

		if (!s.getStartDirectives().isEmpty()) {
			generateDirectives(s.getStartDirectives());
		}

		if (linePragma != null) {
			generateLinePragmaStart(linePragma);
		}

		final CodeSnippetStatement snippet = typeAs(s, CodeSnippetStatement.class);
		if (snippet != null) {
			final int indent = getIndent();
			try {
				setIndent(0);
				generateSnippetStatement(snippet);
			}
			finally {
				setIndent(indent);
			}
		}
		else {
			try {
				s.accept(visitor);
			}
			catch (NotImplementedException nie) {
				throw new IllegalArgumentException("Element type is not supported: " + s.getClass().getName());
			}
		}

		if (linePragma != null) {
			generateLinePragmaEnd(linePragma);
		}

		if (!s.getEndDirectives().isEmpty()) {
			generateDirectives(s.getEndDirectives());
		}
	}

	protected void generateStatements(@Nonnull final CodeStatementCollection c) {
		for (CodeStatement statement : c) {
			generateStatement(statement);
		}
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
		outputType(e.getType());
	}

	protected void generateTypes(@Nonnull final CodePackage e) {
		for (CodeTypeDeclaration typeDeclaration : e.getTypes()) {
			if (options.isBlankLinesBetweenMembers()) {
				formatter.nl();
			}
			generateType(typeDeclaration);
		}
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
		outputExpressionList(expressions, false);
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

	protected void initOutput(@Nonnull final Writer output, @Nullable CodeGeneratorOptions options) {

		if (options == null) {
			options = new CodeGeneratorOptions();
		}
		this.output = new PrintWriter(checkNotNull(output));
		this.options = options;
		this.codeModel = new JCodeModel();
		this.formatter = new JFormatter(this.output, this.options.getIndentString());
	}

	@Override
	public void generateCodeFromCompileUnit(@Nonnull final CodeCompileUnit compileUnit, @Nonnull final Writer output, @Nonnull final CodeGeneratorOptions options) {
		initOutput(output, options);

		if (typeIs(compileUnit, CodeSnippetCompileUnit.class)) {
			generateSnippetCompileUnit((CodeSnippetCompileUnit) compileUnit);
		}
		else {
			generateCompileUnit(compileUnit);
		}
	}

	@Override
	public void generateCodeFromExpression(@Nonnull final CodeExpression expression, @Nonnull final Writer output, @Nonnull final CodeGeneratorOptions options) {
		initOutput(output, options);
		generateExpression(expression);
	}

	@Override
	public void generateCodeFromPackage(@Nonnull final CodePackage codePackage, @Nonnull final Writer writer, @Nonnull final CodeGeneratorOptions options) {
		initOutput(output, options);
		generateNamespace(codePackage);
	}

	@Override
	public void generateCodeFromStatement(@Nonnull final CodeStatement statement, @Nonnull final Writer output, @Nonnull final CodeGeneratorOptions options) {
		initOutput(output, options);
		generateStatement(statement);
	}

	@Override
	public void generateCodeFromType(@Nonnull final CodeTypeDeclaration type, @Nonnull final Writer output, @Nonnull final CodeGeneratorOptions options) {
		initOutput(output, options);
		generateType(type);
	}

	protected void generateType(@Nonnull final CodeTypeDeclaration type) {
		this.currentType = type;
		this.currentMember = null;

		if (!type.getStartDirectives().isEmpty()) {
			generateDirectives(type.getStartDirectives());
		}
		for (CodeCommentStatement statement : type.getComments()) {
			generateCommentStatement(statement);
		}

		if (type.getLinePragma() != null) {
			generateLinePragmaStart(type.getLinePragma());
		}

		generateTypeStart(type);

		final CodeTypeMemberCollection typeMembers = type.getMembers();
		final CodeTypeMember[] members = new CodeTypeMember[typeMembers.size()];
		typeMembers.copyTo(members, 0);

		if (!options.isVerbatimOrder()) {
//			TODO: Does this matter?
//			final int[] order = new int[members.length];
//			for (int n = 0; n < members.length; n++) {
//				order[n] = Arrays.binarySearch(memberTypes, members[n].getClass()) * members.length + n;
//			}
//
//			Arrays.sort();
		}

		CodeTypeDeclaration subtype = null;
		for (CodeTypeMember member : members) {
			CodeTypeMember prevMember = this.currentMember;
			this.currentMember = member;

			if (prevMember != null && subtype == null) {
				if (prevMember.getLinePragma() != null) {
					generateLinePragmaEnd(prevMember.getLinePragma());
				}
				if (!prevMember.getEndDirectives().isEmpty()) {
					generateDirectives(prevMember.getEndDirectives());
				}
			}

			if (options.isBlankLinesBetweenMembers()) {
				formatter.nl();
			}

			subtype = typeAs(member, CodeTypeDeclaration.class);
			if (subtype != null) {
				generateType(subtype);
				this.currentType = type;
				continue;
			}

			if (!currentMember.getStartDirectives().isEmpty()) {
				generateDirectives(currentMember.getStartDirectives());
			}
			for (CodeCommentStatement statement : member.getComments()) {
				generateCommentStatement(statement);
			}

			if (member.getLinePragma() != null) {
				generateLinePragmaStart(member.getLinePragma());
			}

			try {
				member.accept(visitor);
			}
			catch (NotImplementedException ne) {
				throw new IllegalArgumentException("Element type " + member.getClass() + " is not supported.");
			}
		}

		// Hack because of previous continue usage
		if (currentMember != null && !typeIs(currentMember, CodeTypeDeclaration.class)) {
			if (currentMember.getLinePragma() != null) {
				generateLinePragmaEnd(currentMember.getLinePragma());
			}
			if (!currentMember.getEndDirectives().isEmpty()) {
				generateDirectives(currentMember.getEndDirectives());
			}
		}

		this.currentType = type;
		generateTypeEnd(type);

		if (type.getLinePragma() != null) {
			generateLinePragmaEnd(type.getLinePragma());
		}
		if (!type.getEndDirectives().isEmpty()) {
			generateDirectives(type.getEndDirectives());
		}
	}

	public static boolean isValidLanguageIndependentIdentifier(@Nonnull final String value) {
		if (value == null || value.equals(Strings.Empty)) {
			return false;
		}

		// TODO

		return true;
	}

	@Override
	public void validateIdentifier(@Nonnull final String value) {
		if (!isValidIdentifier(value)) {
			throw new IllegalArgumentException("Identifier is invalid: " + value);
		}
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
		// Intentionally left empty
	}

}
