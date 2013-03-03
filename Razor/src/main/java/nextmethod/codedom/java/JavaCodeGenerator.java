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

package nextmethod.codedom.java;

import com.google.common.collect.Maps;
import com.sun.codemodel.JCodeModel;
import nextmethod.annotations.Internal;
import nextmethod.base.NotImplementedException;
import nextmethod.base.Strings;
import nextmethod.codedom.*;
import nextmethod.codedom.compiler.CodeGenerator;
import nextmethod.codedom.compiler.GeneratorSupport;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 *
 */
@Internal
class JavaCodeGenerator extends CodeGenerator {

	public static final String NullToken = "null";

	private Map<String, String> providerOptions = Maps.newHashMap();

	protected boolean dontWriteSemicolon;

	public JavaCodeGenerator() {
		dontWriteSemicolon = false;
	}

	public JavaCodeGenerator(final Map<String, String> providerOptions) {
		this.providerOptions.putAll(providerOptions);
	}

	protected Map<String, String> getProviderOptions() {
		return providerOptions;
	}

	private void outputAnnotations(@Nonnull final CodeAnnotationDeclarationCollection annotations, final String prefix, final boolean inline) {
		throw new NotImplementedException();
	}

	@Override
	protected String getNullToken() {
		return NullToken;
	}

	@Override
	protected void generateArgumentReferenceExpression(@Nonnull final CodeArgumentReferenceExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateArrayCreateExpression(@Nonnull final CodeArrayCreateExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateArrayIndexerExpression(@Nonnull final CodeArrayIndexerExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateAssignStatement(@Nonnull final CodeAssignStatement s) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateAttachEventStatement(@Nonnull final CodeAttachEventStatement s) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateAnnotationDeclarationsStart(@Nonnull final CodeAnnotationDeclarationCollection attributes) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateAnnotationDeclarationsEnd(@Nonnull final CodeAnnotationDeclarationCollection attributes) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateBaseReferenceExpression(@Nonnull final CodeBaseReferenceExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateCastExpression(@Nonnull final CodeCastExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateComment(@Nonnull final CodeComment comment) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateConditionStatement(@Nonnull final CodeConditionStatement s) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateConstructor(@Nonnull final CodeConstructor x, @Nonnull final CodeTypeDeclaration d) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateDelegateCreateExpression(@Nonnull final CodeDelegateCreateExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateDelegateInvokeExpression(@Nonnull final CodeDelegateInvokeExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateEntryPointMethod(@Nonnull final CodeEntryPointMethod m, @Nonnull final CodeTypeDeclaration d) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateEvent(@Nonnull final CodeMemberEvent ev, @Nonnull final CodeTypeDeclaration d) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateEventReferenceExpression(@Nonnull final CodeEventReferenceExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateExpressionStatement(@Nonnull final CodeExpressionStatement statement) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateField(@Nonnull final CodeMemberField f) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateFieldReferenceExpression(@Nonnull final CodeFieldReferenceExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateGotoStatement(@Nonnull final CodeGotoStatement statement) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateIndexerExpression(@Nonnull final CodeIndexerExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateIterationStatement(@Nonnull final CodeIterationStatement s) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateLabeledStatement(@Nonnull final CodeLabeledStatement statement) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateLinePragmaStart(@Nonnull final CodeLinePragma p) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateLinePragmaEnd(@Nonnull final CodeLinePragma p) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateMethod(@Nonnull final CodeMemberMethod m, @Nonnull final CodeTypeDeclaration d) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateMethodInvokeExpression(@Nonnull final CodeMethodInvokeExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateMethodReferenceExpression(@Nonnull final CodeMethodReferenceExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateMethodReturnStatement(@Nonnull final CodeMethodReturnStatement e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generatePackageStart(@Nonnull final CodePackage ns) {
		final JCodeModel codeModel = getCodeModel();

		final String name = ns.getName();
		if (!Strings.isNullOrEmpty(name)) {
			codeModel._package(name);
		}
	}

	@Override
	protected void generatePackageEnd(@Nonnull final CodePackage ns) {
		// Do nothing
	}

	@Override
	protected void generatePackageImport(@Nonnull final CodePackageImport i) {
		// TODO
	}

	@Override
	protected void generateObjectCreateExpression(@Nonnull final CodeObjectCreateExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateProperty(@Nonnull final CodeMemberProperty p, @Nonnull final CodeTypeDeclaration d) {
		throw new NotImplementedException();
	}

	@Override
	protected void generatePropertyReferenceExpression(@Nonnull final CodePropertyReferenceExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generatePropertySetValueReferenceExpression(@Nonnull final CodePropertySetValueReferenceExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateRemoveEventStatement(@Nonnull final CodeRemoveEventStatement statement) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateSnippetExpression(@Nonnull final CodeSnippetExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateSnippetMember(@Nonnull final CodeSnippetTypeMember m) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateThisReferenceExpression(@Nonnull final CodeThisReferenceExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateThrowExceptionStatement(@Nonnull final CodeThrowExceptionStatement s) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateTryCatchFinallyStatement(@Nonnull final CodeTryCatchFinallyStatement s) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateTypeEnd(@Nonnull final CodeTypeDeclaration declaration) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateTypeConstructor(@Nonnull final CodeTypeConstructor constructor) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateTypeStart(@Nonnull final CodeTypeDeclaration declaration) {
		outputAnnotations(declaration.getCustomAttributes(), null, false);

		if (!isCurrentDelegate()) {
			outputTypeAnnotations(declaration);

		}
	}

	@Override
	protected void generateVariableDeclarationStatement(@Nonnull final CodeVariableDeclarationStatement e) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateVariableReferenceExpression(@Nonnull final CodeVariableReferenceExpression e) {
		throw new NotImplementedException();
	}

	@Override
	protected void outputType(@Nonnull final CodeTypeReference t) {
		throw new NotImplementedException();
	}

	@Override
	protected String quoteSnippetString(@Nonnull final String value) {
		throw new NotImplementedException();
	}

	@Override
	public String createEscapedIdentifier(final String value) {
		throw new NotImplementedException();
	}

	@Override
	public String createValidIdentifier(final String value) {
		throw new NotImplementedException();
	}

	@Override
	public String getTypeOutput(@Nonnull final CodeTypeReference type) {
		throw new NotImplementedException();
	}

	@Override
	public boolean isValidIdentifier(@Nonnull final String value) {
		throw new NotImplementedException();
	}

	@Override
	public boolean supports(@Nonnull final GeneratorSupport supports) {
		throw new NotImplementedException();
	}

	private void outputTypeAnnotations(@Nonnull final CodeTypeDeclaration declaration) {
		throw new NotImplementedException();
	}
}
