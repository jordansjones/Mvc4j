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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nextmethod.annotations.Internal;
import nextmethod.base.NotImplementedException;
import nextmethod.base.Strings;
import nextmethod.codedom.*;
import nextmethod.codedom.compiler.CodeGenerator;
import nextmethod.codedom.compiler.GeneratorSupport;
import nextmethod.codedom.compiler.IndentingPrintWriter;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

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
		boolean params_set = false;

		for (CodeAnnotationDeclaration annotation : annotations) {
			if ("System.ParamArrayAttribute".equalsIgnoreCase(annotation.getName())) { // NOTE: Do we need this?
				params_set = true;
				continue;
			}

			generateAnnotationDeclarationsStart(annotations);
			if (prefix != null) {
				getOutput().write(prefix);
			}
			outputAnnotationDeclaration(annotation);
			generateAnnotationDeclarationsEnd(annotations);

			if (inline) {
				getOutput().write(" ");
			}
			else {
				getOutput().println();
			}
		}

		if (params_set) {
			if (prefix != null) {
				getOutput().write(prefix);
			}
			getOutput().write("params");

			if (inline) {
				getOutput().write(" ");
			}
			else {
				getOutput().println();
			}
		}
	}

	private void outputAnnotationDeclaration(final CodeAnnotationDeclaration annotation) {
		getOutput().write(annotation.getName().replace('+', '.'));
		getOutput().write('(');

		final Iterator<CodeAnnotationArgument> iterator = annotation.getArguments().iterator();
		if (iterator.hasNext()) {
			CodeAnnotationArgument argument = iterator.next();
			outputAnnotationArgument(argument);

			while (iterator.hasNext()) {
				getOutput().write(", ");
				argument = iterator.next();
				outputAnnotationArgument(argument);
			}
		}

		getOutput().write(')');
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
		final IndentingPrintWriter output = getOutput();

		output.write("new ");
		final CodeExpressionCollection initializers = e.getInitializers();
		CodeTypeReference createType = e.getCreateType();

		if (!initializers.isEmpty()) {
			outputType(createType);

			if (e.getCreateType().getArrayRank() == 0) {
				output.write("[]");
			}

			outputStartBrace();
			incrementIndent();
			outputExpressionList(initializers, true);
			decrementIndent();
			output.write("}");
		}
		else {
			CodeTypeReference arrayType = createType.getArrayElementType();
			while(arrayType != null) {
				createType = arrayType;
				arrayType = arrayType.getArrayElementType();
			}

			outputType(createType);

			output.write('[');

			final CodeExpression size = e.getSizeExpression();
			if (size != null) {
				generateExpression(size);
			}
			else {
				output.write(e.getSize());
			}
			output.write(']');
		}
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
		String commentChars = null;
		if (comment.isDocComment()) {
			commentChars = "///";
		}
		else {
			commentChars = "//";
		}

		getOutput().print(commentChars);
		String text = comment.getText();

		for (int i = 0; i < text.length(); i++)
		{
			getOutput().print(text.charAt(i));
			if (text.charAt(i) == '\r')
			{
				if (i < (text.length() - 1) && text.charAt(i + 1) == '\n') {
					continue;
				}
				getOutput().print(commentChars);
			}
			else if (text.charAt(i) == '\n') {
				getOutput().print(commentChars);
			}
		}

		getOutput().println();
	}

	@Override
	protected void generateCompileUnit(@Nonnull final CodeCompileUnit compileUnit) {
		generateCompileUnitStart(compileUnit);

		List<CodePackageImport> imports = null;
		for (CodePackage codePackage : compileUnit.getPackages()) {
			if (!Strings.isNullOrEmpty(codePackage.getName()))
				continue;

			if (codePackage.getImports().isEmpty())
				continue;

			if (imports == null)
				imports = Lists.newArrayList();

			imports.addAll(codePackage.getImports());
		}

		if (imports != null && !imports.isEmpty()) {
			Collections.sort(imports, new Comparator<CodePackageImport>() {
				@Override
				public int compare(final CodePackageImport o1, final CodePackageImport o2) {
					return o1.getPackage().compareTo(o2.getPackage());
				}
			});

			for (CodePackageImport anImport : imports) {
				generatePackageImport(anImport);
			}

			getOutput().println();
		}

		CodePackageImportCollection globalImports = null;
		for (CodePackage aPackage : compileUnit.getPackages()) {
			if (Strings.isNullOrEmpty(aPackage.getName())) {
				globalImports = aPackage.getImports();
				aPackage.setImports(new CodePackageImportCollection());
			}

			generatePackage(aPackage);

			if (globalImports != null) {
				aPackage.setImports(globalImports);
				globalImports = null;
			}
		}

		generateCompileUnitEnd(compileUnit);
	}

	@Override
	protected void generateCompileUnitStart(@Nonnull final CodeCompileUnit compileUnit) {
		generateComment(new CodeComment("------------------------------------------------------------------------------"));
		generateComment(new CodeComment(" <autogenerated>"));
		generateComment(new CodeComment("     This code was generated by a tool."));
		generateComment(new CodeComment("     Runtime Version: " + System.getProperty("java.version")));
		generateComment(new CodeComment(""));
		generateComment(new CodeComment("     Changes to this file may cause incorrect behavior and will be lost if"));
		generateComment(new CodeComment("     the code is regenerated."));
		generateComment(new CodeComment(" </autogenerated>"));
		generateComment(new CodeComment("------------------------------------------------------------------------------"));

		getOutput().println();
		super.generateCompileUnitStart(compileUnit);
	}

	@Override
	protected void generateCompileUnitStart(@Nonnull final CodeCompileUnit compileUnit) {
//		TODO: Commented out until we have something to put here
//		getOutput().println();
		super.generateCompileUnitStart(compileUnit);
	}

	@Override
	protected void generateCompileUnit(@Nonnull final CodeCompileUnit compileUnit) {
		generateCompileUnitStart(compileUnit);

		// Package then imports

		List<CodePackageImport> globalImports = null;
		for (CodePackage codePackage : compileUnit.getPackages()) {
			if (Strings.isNullOrEmpty(codePackage.getName())) {
				globalImports = Lists.newArrayList(codePackage.getImports());
				codePackage.getImports().clear();
			}

			generatePackage(codePackage);

			if (globalImports != null) {
				codePackage.getImports().addAll(globalImports);
				globalImports = null;
			}
		}

		List<CodePackageImport> imports = null;
		for (CodePackage codePackage : compileUnit.getPackages()) {

			if (!Strings.isNullOrEmpty(codePackage.getName())) continue;

			if (codePackage.getImports().isEmpty()) continue;

			if (imports == null)
				imports = Lists.newArrayList();

			codePackage.getImports().forEach(imports::add);
		}

		if (imports != null) {
			imports.sort((a, b) -> a.getPackage().compareTo(b.getPackage()));
			imports.forEach(this::generatePackageImport);
		}

		generateCompileUnitEnd(compileUnit);
	}

	@Override
	protected void generateConditionStatement(@Nonnull final CodeConditionStatement s) {
		throw new NotImplementedException();
	}

	@Override
	protected void generateConstructor(@Nonnull final CodeConstructor x, @Nonnull final CodeTypeDeclaration d) {
		if (isCurrentDelegate() || isCurrentEnum() || isCurrentInterface()) return;

		outputAnnotations(x.getCustomAnnotations(), null, false);

		outputMemberAccessModifier(x.getAttributes());
		getOutput().write(getSafeName(getCurrentTypeName()) + "(");
		outputParameters(x.getParameters());
		getOutput().write(")");

		outputStartBrace();
		incrementIndent();

		if (!x.getBaseConstructorArgs().isEmpty())
		{
			getOutput().write("super(");
			outputExpressionList(x.getBaseConstructorArgs());
			getOutput().println(");");
		}
		if (!x.getChainedConstructorArgs().isEmpty())
		{
			getOutput().write("this(");
			outputExpressionList(x.getBaseConstructorArgs());
			getOutput().println(");");
		}

		generateStatements(x.getStatements());

		decrementIndent();
		outputEndBrace();
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
		getOutput().println();
		getOutput().format("//#line %d \"%s\"", p.getLineNumber(), p.getFileName());
		getOutput().println();
	}

	@Override
	protected void generateLinePragmaEnd(@Nonnull final CodeLinePragma p) {
		getOutput().println();
		getOutput().println("//#line default");
		getOutput().println("//#line hidden");
	}

	@Override
	protected void generateMethod(@Nonnull final CodeMemberMethod m, @Nonnull final CodeTypeDeclaration d) {
		if (isCurrentDelegate() || isCurrentEnum()) return;

		final IndentingPrintWriter output = getOutput();

		outputAnnotations(m.getCustomAnnotations(), null, false);

		outputAnnotations(m.getReturnTypeCustomAnnotations(), "return: ", false);

		final MemberAttributes attributes = m.getAttributes();

		if (!isCurrentInterface()) {
			if (m.getPrivateImplementationType() == null) {
				outputMemberAccessModifier(attributes);
				outputVTableModifier(attributes);
				outputMemberScopeModifier(attributes);
			}
		}
		else {
			outputVTableModifier(attributes);
		}

		outputType(m.getReturnType());
		output.write(' ');

		final CodeTypeReference privateType = m.getPrivateImplementationType();
		if (privateType != null) {
			output.write(privateType.getBaseType());
			output.write('.');
		}
		output.write(getSafeName(m.getName()));

		generateGenericsParameters(m.getTypeParameters());

		output.write('(');
		outputParameters(m.getParameters());
		output.write(')');

		generateGenericsConstraints(m.getTypeParameters());

		if (isAbstract(attributes) || d.isInterface()) {
			output.println(';');
		}
		else {
			outputStartBrace();
			incrementIndent();
			generateStatements(m.getStatements());
			decrementIndent();
			output.println('}');
		}
	}

	static boolean isAbstract(final MemberAttributes attributes) {
		return (attributes.val & MemberAttributes.ScopeMask.val) == MemberAttributes.Abstract.val;
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
		final IndentingPrintWriter output = getOutput();

		final String name = ns.getName();
		if (!Strings.isNullOrEmpty(name)) {
			output.print("package ");
			output.print(getSafeName(name));
			output.println(";");
		}
	}

	@Override
	protected void generatePackageEnd(@Nonnull final CodePackage ns) {
		// Do nothing
	}

	@Override
	protected void generatePackageImport(@Nonnull final CodePackageImport i) {
		getOutput().print("import ");
		getOutput().print(getSafeName(i.getPackage()));
		getOutput().println(';');
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
		getOutput().write(m.getText());
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
		if (!isCurrentDelegate()) {
			decrementIndent();
			getOutput().println("}");
		}
	}

	@Override
	protected void generateTypeConstructor(@Nonnull final CodeTypeConstructor constructor) {
		if (isCurrentClass() || isCurrentEnum() || isCurrentInterface()) return;

		outputAnnotations(constructor.getCustomAnnotations(), null, false);

		getOutput().write("static");
		outputStartBrace();
		incrementIndent();
		generateStatements(constructor.getStatements());
		decrementIndent();
		outputEndBrace();
	}

	@Override
	protected void generateTypeStart(@Nonnull final CodeTypeDeclaration declaration) {
		final IndentingPrintWriter output = getOutput();
		outputAnnotations(declaration.getCustomAnnotations(), null, false);

		if (!isCurrentDelegate()) {
			outputTypeAttributes(declaration);

			output.print(getSafeName(declaration.getName()));

			generateGenericsParameters(declaration.getTypeParameters());

			final Iterator<CodeTypeReference> iterator = declaration.getBaseTypes().iterator();
			if (iterator.hasNext()) {
				CodeTypeReference type = (CodeTypeReference) iterator.next();

				output.write(" : ");
				outputType(type);
				while (iterator.hasNext()) {
					type = (CodeTypeReference) iterator.next();

					output.write(", ");
					outputType(type);
				}
			}

			generateGenericsConstraints(declaration.getTypeParameters());
			outputStartBrace();
			incrementIndent();
		}
		else {
			if ((declaration.getTypeAttributes().val & TypeAttributes.VisibilityMask.val) == TypeAttributes.Public.val) {
				output.write("public ");
			}
			CodeTypeDelegate delegate = (CodeTypeDelegate) declaration;
			output.write("delegate ");
			outputType(delegate.getReturnType());
			output.write(" ");
			output.write(getSafeName(declaration.getName()));
			output.write("(");
			outputParameters(delegate.getParameters());
			output.println(");");

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
		getOutput().write(getTypeOutput(t));
	}

	private void outputVTableModifier(final MemberAttributes attributes) {
		// This doesn't apply
	}

	@Override
	protected String quoteSnippetString(@Nonnull final String value) {
		throw new NotImplementedException();
	}

	@Override
	public String createEscapedIdentifier(final String value) {
		return getSafeName(checkNotNull(value));
	}

	@Override
	public String createValidIdentifier(final String value) {
		throw new NotImplementedException();
	}

	@Override
	public String getTypeOutput(@Nonnull final CodeTypeReference type) {
		if (type.getOptions() != null && ((type.getOptions().val & CodeTypeReferenceOptions.GenericTypeParameter.val) != 0)) {
			return type.getBaseType();
		}

		String typeOutput = null;
		if (type.getArrayElementType() != null) {
			typeOutput = getTypeOutput(type.getArrayElementType());
		}
		else {
			typeOutput = determineTypeOutput(type);
		}

		int rank = type.getArrayRank();
		if (rank > 0) {
			typeOutput += '[';
			for (--rank; rank > 0; --rank) {
				typeOutput += ',';
			}
			typeOutput += ']';
		}

		return typeOutput;
	}

	private String determineTypeOutput(final CodeTypeReference type) {
		String typeOutput = null;
		String baseType = type.getBaseType();
		final String lowerBaseType = baseType.toLowerCase();

		if (Integer.class.getName().equalsIgnoreCase(lowerBaseType)) {
			typeOutput = "int";
		}
		else if (Long.class.getName().equalsIgnoreCase(lowerBaseType)) {
			typeOutput = "long";
		}
		else if (Void.class.getName().equalsIgnoreCase(lowerBaseType)) {
			typeOutput = "void";
		}
		else if (Character.class.getName().equalsIgnoreCase(lowerBaseType)) {
			typeOutput = "char";
		}
		else if (Boolean.class.getName().equalsIgnoreCase(lowerBaseType)) {
			typeOutput = "boolean";
		}
		else if (Byte.class.getName().equalsIgnoreCase(lowerBaseType)) {
			typeOutput = "byte";
		}
		else if (Double.class.getName().equalsIgnoreCase(lowerBaseType)) {
			typeOutput = "double";
		}
		else if (Float.class.getName().equalsIgnoreCase(lowerBaseType)) {
			typeOutput = "float";
		}
		else if (Short.class.getName().equalsIgnoreCase(lowerBaseType)) {
			typeOutput = "short";
		}
		else if (String.class.getName().equalsIgnoreCase(lowerBaseType)) {
			typeOutput = "String";
		}
		else if (Object.class.getName().equalsIgnoreCase(lowerBaseType)) {
			typeOutput = "Object";
		}
		else {
			final StringBuilder sb = new StringBuilder(baseType.length());
			int lastProcessedChar = 0;
			for (int i = 0; i < baseType.length(); i++) {
				char currentChar = baseType.charAt(i);
				if (currentChar != '+' && currentChar != '.') {
					if (currentChar == '`') {
						sb.append(createEscapedIdentifier(baseType.substring(lastProcessedChar, i - lastProcessedChar)));
						i++;
						int end = i;
						while (end < baseType.length() && Character.isDigit(baseType.charAt(end))) {
							end++;
						}
						int typeArgCount = Integer.valueOf(baseType.substring(i, end - i));
						outputTypeArguments(type.getTypeArguments(), sb, typeArgCount);
						i = end;
						if ((i < baseType.length()) && ((baseType.charAt(i) == '+') || (baseType.charAt(i) == '.'))) {
							sb.append('.');
							i++;
						}
						lastProcessedChar = i;
					}
					else {
						sb.append(createEscapedIdentifier(baseType.substring(lastProcessedChar, i - lastProcessedChar)));
						sb.append('.');
						i++;
						lastProcessedChar = i;
					}
				}
			}

			if (lastProcessedChar < baseType.length()) {
				sb.append(createEscapedIdentifier(baseType.substring(lastProcessedChar)));
			}
			typeOutput = sb.toString();
		}

		return typeOutput;
	}

	@Override
	public boolean isValidIdentifier(@Nonnull final String value) {
		throw new NotImplementedException();
	}

	@Override
	public boolean supports(@Nonnull final GeneratorSupport supports) {
		return true;
	}

	@Override
	protected void generateDirectives(@Nonnull final CodeDirectiveCollection directives) {
		for (CodeDirective directive : directives) {
//			if (typeIs(directive, CodeChecksumPragma.class)) {
//
//			}
//			if (typeIs(directive, CodeRegionDirective.class)) {
//
//			}
		}
	}

	void generateGenericsParameters(@Nonnull final CodeTypeParameterCollection parameters) {
		final int count = parameters.size();
		if (count == 0) return;

		getOutput().write('<');
		for (int i = 0; i < count - 1; ++i) {
			getOutput().write(parameters.get(i).getName());
			getOutput().write(", ");
		}
		getOutput().write(parameters.get(count - 1).getName());
		getOutput().write('>');
	}

	void generateGenericsConstraints(@Nonnull final CodeTypeParameterCollection parameters) {
		// TODO
	}

	private void outputTypeAttributes(@Nonnull final CodeTypeDeclaration declaration) {
		final IndentingPrintWriter output = getOutput();
		final TypeAttributes attributes = declaration.getTypeAttributes();

		final TypeAttributes typeVisibility = TypeAttributes.valueOf(attributes.val & TypeAttributes.VisibilityMask.val);
		if (TypeAttributes.Public.equals(typeVisibility)) {
			output.write("public ");
		}
		else if (TypeAttributes.Private.equals(typeVisibility)) {
			output.write("private ");
		}
		else if (TypeAttributes.Protected.equals(typeVisibility)) {
			output.write("protected ");
		}

		if (declaration.isEnum()) {
			output.write("enum ");
		}
		else {
			if ((attributes.val & TypeAttributes.Interface.val) != 0) {
				output.write("interface ");
			}
			else {
				if ((attributes.val & TypeAttributes.Final.val) != 0) {
					output.write("final ");
				}
				else if ((attributes.val & TypeAttributes.Abstract.val) != 0)
				{
					output.write("abstract ");
				}
				output.write("class ");
			}
		}

	}

	private void outputTypeArguments(final CodeTypeReferenceCollection typeArgs, final StringBuilder sb, final int count) {
		if (count == 0) return;
		if (typeArgs.size() == 0) {
			sb.append("<>");
			return;
		}

		sb.append('<');
		sb.append(getTypeOutput(typeArgs.get(0)));
		for (int i = 1; i < count; i++) {
			sb.append(", ")
				.append(getTypeOutput(typeArgs.get(i)));
		}
		sb.append('>');
	}

	private void outputStartBrace() {
		if ("C".equalsIgnoreCase(getOptions().getBracingStyle())) {
			getOutput().println();
			getOutput().println("{");
		}
		else {
			getOutput().println(" {");
		}
	}

	private void outputEndBrace() {
		getOutput().println("}");
	}

	private String getSafeName(final String id) {
		// Reserved Words?
		return id;
	}
}
