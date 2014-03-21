package nextmethod.web.razor.generator;


import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.Strings;
import nextmethod.codedom.CodeCompileUnit;
import nextmethod.codedom.CodeLinePragma;
import nextmethod.codedom.CodeMemberField;
import nextmethod.codedom.CodeMemberMethod;
import nextmethod.codedom.CodePackage;
import nextmethod.codedom.CodePackageImport;
import nextmethod.codedom.CodeSnippetStatement;
import nextmethod.codedom.CodeTypeDeclaration;
import nextmethod.codedom.MemberAttributes;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.generator.internal.CodeWriter;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.utils.DisposableAction;

import javax.annotation.Nonnull;
import java.util.Map;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class CodeGeneratorContext {

	private static final String DesignTimeHelperMethodName = "__RazorDesignTimeHelpers__";

	private int nextDesignTimePragmaId = 1;
	private boolean expressionHelperVariableWritten;
	private CodeMemberMethod designTimeHelperMethod;
	private StatementBuffer currentBuffer = new StatementBuffer();

	private CodeGeneratorContext() {
		this.expressionRenderingMode = ExpressionRenderingMode.WriteToOutput;
	}

	// Internal/Private state. Technically consumers might want to use some of these but they can implement them independently if necessary.
	// It's way safer to make them internal for now, especially with the code generator stuff in a bit of flux.
	private ExpressionRenderingMode expressionRenderingMode;
	private Delegates.IAction2<String, CodeLinePragma> statementCollector;
	private Delegates.IFunc<CodeWriter> codeWriterFactory;

	private String sourceFile;
	private CodeCompileUnit compileUnit;
	private CodePackage codePackage;
	private CodeTypeDeclaration generatedClass;
	private RazorEngineHost host;
	private Map<Integer, GeneratedCodeMapping> codeMappings;
	private String targetWriterName;
	private CodeMemberMethod targetMethod;

	public String getCurrentBufferedStatement() {
		return currentBuffer == null ? Strings.Empty : currentBuffer.getBuilder().toString();
	}

	public static CodeGeneratorContext create(final RazorEngineHost host, final String className, final String rootPackage, final String sourceFile, final boolean shouldGenerateLinePragmas) {
		return create(host, null, className, rootPackage, sourceFile, shouldGenerateLinePragmas);
	}

	static CodeGeneratorContext create(final RazorEngineHost host, final Delegates.IFunc<CodeWriter> writerFactory, final String className, final String rootPackage, final String sourceFile, final boolean shouldGenerateLinePragmas) {
		final CodeGeneratorContext context = new CodeGeneratorContext();
		context.host = host;
		context.codeWriterFactory = writerFactory;
		context.sourceFile = shouldGenerateLinePragmas ? sourceFile : null;
		context.compileUnit = new CodeCompileUnit();
		context.codePackage = new CodePackage(rootPackage);
		context.generatedClass = new CodeTypeDeclaration(className);
		context.generatedClass.setIsClass(true);
		context.targetMethod = new CodeMemberMethod();
		context.targetMethod.setName(host.getGeneratedClassContext().getExecuteMethodName());
		context.targetMethod.setAttributes(MemberAttributes.Public);
		context.codeMappings = Maps.newHashMap();

		context.compileUnit.getPackages().add(context.codePackage);
		context.codePackage.getTypes().add(context.generatedClass);
		context.generatedClass.getMembers().add(context.targetMethod);

		Iterables.transform(host.getPackageImports(), CodePackageImport::new).forEach(context.codePackage.getImports()::add);

		return context;
	}

	public void addDesignTypeHelperStatement(final CodeSnippetStatement statement) {
		if (designTimeHelperMethod == null) {
			designTimeHelperMethod = new CodeMemberMethod();
			designTimeHelperMethod.setName(DesignTimeHelperMethodName);
			designTimeHelperMethod.setAttributes(MemberAttributes.Private);

			designTimeHelperMethod.getStatements().add(new CodeSnippetStatement(buildCodeString(CodeWriter::writeDisableUnusedFieldWarningPragma)));
			designTimeHelperMethod.getStatements().add(new CodeSnippetStatement(buildCodeString(CodeWriter::writeRestoreUnusedFieldWarningPragma)));
			generatedClass.getMembers().add(0, designTimeHelperMethod);
		}

		designTimeHelperMethod.getStatements().add(designTimeHelperMethod.getStatements().size() - 1, statement);
	}

	public int addCodeMapping(final SourceLocation sourceLocation, final int generatedCodeStart, final int generatedCodeLength) {
		if (generatedCodeLength == Integer.MAX_VALUE) {
			throw new IllegalArgumentException("generatedCodeStart out of range");
		}

		final GeneratedCodeMapping mapping = new GeneratedCodeMapping(
			sourceLocation.getAbsoluteIndex(),
			sourceLocation.getLineIndex() + 1,
			sourceLocation.getCharacterIndex() + 1,
			generatedCodeStart + 1,
			generatedCodeLength
		);

		int id = nextDesignTimePragmaId++;
		codeMappings.put(id, mapping);
		return id;
	}

	public CodeLinePragma generateLinePragma(final Span target) {
		return generateLinePragma(target, 0);
	}

	public CodeLinePragma generateLinePragma(final Span target, final int generatedCodeStart) {
		return generateLinePragma(target, generatedCodeStart, target.getContent().length());
	}

	public CodeLinePragma generateLinePragma(final Span target, final int generatedCodeStart, final int codeLength) {
		return generateLinePragma(target.getStart(), generatedCodeStart, codeLength);
	}
	public CodeLinePragma generateLinePragma(final SourceLocation start, final int generatedCodeStart, final int codeLength) {
		if (!Strings.isNullOrEmpty(sourceFile)) {
			if (host.isDesignTimeMode()) {
				int mappingId = addCodeMapping(start, generatedCodeStart, codeLength);
				return new CodeLinePragma(sourceFile, mappingId);
			}
			return new CodeLinePragma(sourceFile, start.getLineIndex() + 1);
		}
		return null;
	}

	public void bufferStatementFragment(final Span sourceSpan) {
		bufferStatementFragment(sourceSpan.getContent(), sourceSpan);
	}

	public void bufferStatementFragment(final String fragment) {
		bufferStatementFragment(fragment, null);
	}

	public void bufferStatementFragment(final String fragment, final Span sourceSpan) {
		final StringBuilder builder = currentBuffer.getBuilder();
		if (sourceSpan != null && currentBuffer.getLinePragmaSpan() == null) {
			currentBuffer.setLinePragmaSpan(sourceSpan);
			// Pad the output as necessary
			int start = builder.length();
			if (currentBuffer.getGeneratedCodeStart().isPresent()) {
				start = currentBuffer.getGeneratedCodeStart().get();
			}

			String padded = CodeGeneratorBase.pad(builder.toString(), sourceSpan, start);
			currentBuffer.setGeneratedCodeStart(Optional.of(start + (padded.length() - builder.length())));
			builder.delete(0, builder.length());
			builder.append(padded);
		}
		builder.append(fragment);
	}

	public void markStartOfGeneratedCode() {
		currentBuffer.markStart();
	}

	public void markEndOfGeneratedCode() {
		currentBuffer.markEnd();
	}

	public void flushBufferedStatement() {
		final StringBuilder builder = currentBuffer.getBuilder();
		if (builder.length() > 0) {
			CodeLinePragma pragma = null;
			if (currentBuffer.getLinePragmaSpan() != null) {
				int start = builder.length();
				if (currentBuffer.getGeneratedCodeStart().isPresent()) {
					start = currentBuffer.getGeneratedCodeStart().get();
				}
				int len = builder.length() - start;
				if (currentBuffer.getCodeLength().isPresent()) {
					len = currentBuffer.getCodeLength().get();
				}
				pragma = generateLinePragma(currentBuffer.getLinePragmaSpan(), start, len);
			}
			addStatement(builder.toString(), pragma);
			currentBuffer.reset();
		}
	}

	public void addStatement(final String generatedCode) {
		addStatement(generatedCode, null);
	}

	public void addStatement(final String body, final CodeLinePragma pragma) {
		if (statementCollector == null) {
			final CodeSnippetStatement statement = new CodeSnippetStatement(body);
			statement.setLinePragma(pragma);
			targetMethod.getStatements().add(statement);
		}
		else {
			statementCollector.invoke(body, pragma);
		}
	}

	public void ensureExpressionHelperVariable() {
		if (!expressionHelperVariableWritten) {
			final CodeMemberField field = new CodeMemberField(Object.class, "__o");
			field.setAttributes(MemberAttributes.valueOf(MemberAttributes.Private.val | MemberAttributes.Static.val));
			generatedClass.getMembers().add(0, field);
			expressionHelperVariableWritten = true;
		}
	}

	public IDisposable changeStatementCollection(final Delegates.IAction2<String, CodeLinePragma> collector) {
		final Delegates.IAction2<String, CodeLinePragma> oldCollector = this.statementCollector;
		this.statementCollector = collector;
		return new DisposableAction(() -> {
			statementCollector = oldCollector;
		});
	}

	public void addContextCall(final Span contentSpan, final String methodName, final boolean isLiteral) {
		addStatement(buildCodeString(input -> {
			input.writeStartMethodInvoke(methodName);
			if (!Strings.isNullOrEmpty(getTargetWriterName())) {
				input.writeSnippet(getTargetWriterName());
				input.writeParameterSeparator();
			}
			input.writeStringLiteral(getHost().getInstrumentedSourceFilePath());
			input.writeParameterSeparator();
			input.writeSnippet(String.valueOf(contentSpan.getContent().length()));
			input.writeParameterSeparator();
			input.writeSnippet(String.valueOf(isLiteral));
			input.writeEndMethodInvoke();
			input.writeEndStatement();
		}));
	}

	@SuppressWarnings("ConstantConditions")
	CodeWriter createCodeWriter() {
		assert codeWriterFactory != null;
		if (codeWriterFactory == null) {
			throw new UnsupportedOperationException(RazorResources().createCodeWriterNoCodeWriter());
		}
		return codeWriterFactory.invoke();
	}

	String buildCodeString(final Delegates.IAction1<CodeWriter> action) {
		try (CodeWriter cw = codeWriterFactory.invoke()) {
			action.invoke(cw);
			return cw.getContent();
		}
	}

	public String getTargetWriterName() {
		return targetWriterName;
	}

	public void setTargetWriterName(String targetWriterName) {
		this.targetWriterName = targetWriterName;
	}

	public RazorEngineHost getHost() {
		return host;
	}

	public Delegates.IFunc<CodeWriter> getCodeWriterFactory() {
		return codeWriterFactory;
	}

	public void setCodeWriterFactory(final Delegates.IFunc<CodeWriter> codeWriterFactory) {
		this.codeWriterFactory = codeWriterFactory;
	}

	public ExpressionRenderingMode getExpressionRenderingMode() {
		return expressionRenderingMode;
	}

	public void setExpressionRenderingMode(ExpressionRenderingMode expressionRenderingMode) {
		this.expressionRenderingMode = expressionRenderingMode;
	}

	public String getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(@Nonnull final String sourceFile) {
		this.sourceFile = sourceFile;
	}

	public Map<Integer, GeneratedCodeMapping> getCodeMappings() {
		return codeMappings;
	}

	public void setCodeMappings(Map<Integer, GeneratedCodeMapping> codeMappings) {
		this.codeMappings = codeMappings;
	}

	public CodePackage getCodePackage() {
		return codePackage;
	}

	public void setCodePackage(CodePackage codePackage) {
		this.codePackage = codePackage;
	}

	public CodeCompileUnit getCompileUnit() {
		return compileUnit;
	}

	public void setCompileUnit(CodeCompileUnit compileUnit) {
		this.compileUnit = compileUnit;
	}

	public StatementBuffer getCurrentBuffer() {
		return currentBuffer;
	}

	public void setCurrentBuffer(StatementBuffer currentBuffer) {
		this.currentBuffer = currentBuffer;
	}

	public boolean isExpressionHelperVariableWritten() {
		return expressionHelperVariableWritten;
	}

	public void setExpressionHelperVariableWritten(boolean expressionHelperVariableWritten) {
		this.expressionHelperVariableWritten = expressionHelperVariableWritten;
	}

	public CodeTypeDeclaration getGeneratedClass() {
		return generatedClass;
	}

	public void setGeneratedClass(CodeTypeDeclaration generatedClass) {
		this.generatedClass = generatedClass;
	}

	public int getNextDesignTimePragmaId() {
		return nextDesignTimePragmaId;
	}

	public void setNextDesignTimePragmaId(int nextDesignTimePragmaId) {
		this.nextDesignTimePragmaId = nextDesignTimePragmaId;
	}

	public Delegates.IAction2<String, CodeLinePragma> getStatementCollector() {
		return statementCollector;
	}

	public void setStatementCollector(final Delegates.IAction2<String, CodeLinePragma> statementCollector) {
		this.statementCollector = statementCollector;
	}

	public CodeMemberMethod getTargetMethod() {
		return targetMethod;
	}

	public void setTargetMethod(CodeMemberMethod targetMethod) {
		this.targetMethod = targetMethod;
	}

	private class StatementBuffer {

		private final StringBuilder builder = new StringBuilder();
		private Optional<Integer> generatedCodeStart;
		private Optional<Integer> codeLength;
		private Span linePragmaSpan;

		public void reset() {
			builder.delete(0, builder.length());
			generatedCodeStart = Optional.absent();
			codeLength = Optional.absent();
			linePragmaSpan = null;
		}

		public void markStart() {
			generatedCodeStart = Optional.of(builder.length());
		}

		public void markEnd() {
			codeLength = Optional.of(builder.length() - generatedCodeStart.get());
		}

		public StringBuilder getBuilder() {
			return builder;
		}

		public Optional<Integer> getCodeLength() {
			return codeLength;
		}

		public void setCodeLength(Optional<Integer> codeLength) {
			this.codeLength = codeLength;
		}

		public Optional<Integer> getGeneratedCodeStart() {
			return generatedCodeStart;
		}

		public void setGeneratedCodeStart(Optional<Integer> generatedCodeStart) {
			this.generatedCodeStart = generatedCodeStart;
		}

		public Span getLinePragmaSpan() {
			return linePragmaSpan;
		}

		public void setLinePragmaSpan(Span linePragmaSpan) {
			this.linePragmaSpan = linePragmaSpan;
		}
	}

}
