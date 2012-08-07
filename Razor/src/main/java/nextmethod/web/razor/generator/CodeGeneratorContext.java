package nextmethod.web.razor.generator;


import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import nextmethod.annotations.TODO;
import nextmethod.base.IAction;
import nextmethod.base.IDisposable;
import nextmethod.base.IVoidAction;
import nextmethod.codedom.CodeLinePragma;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.SourceLocation;

import javax.annotation.Nonnull;
import java.util.Map;

@TODO
public class CodeGeneratorContext {

	private static final String DesignTimeHelperMethodName = "__RazorDesignTimeHelpers__";

	private int nextDesignTimePragmaId = 1;
	private boolean expressionHelperVariableWritten;
//	private CodeMemberMethod designTimeHelperMethod;
	private StatementBuffer currentBuffer = new StatementBuffer();

	private CodeGeneratorContext() {
		this.expressionRenderingMode = ExpressionRenderingMode.WriteToOutput;
	}

	private ExpressionRenderingMode expressionRenderingMode;
	private Function<String, CodeLinePragma> statementCollector;
	private IAction<CodeWriter> codeWriterFactory;

	private String sourceFile;
	private CodeCompileUnit compileUnit;
	private CodePackage codePackage;
	private CodeTypeDeclaration generatedClass;
	private RazorEngineHost host;
	private Map<Integer, GeneratedCodeMapping> codeMappings;
	private String targetWriterName;
	private CodeMemberMethod targetMethod;

	public String getCurrentBufferedStatement() {
		return currentBuffer == null ? "" : currentBuffer.getBuilder().toString();
	}

	public static CodeGeneratorContext create(final RazorEngineHost host, final String className, final String rootPackage, final String sourceFile, final boolean shouldGenerateLinePragmas) {
		return create(host, null, className, rootPackage, sourceFile, shouldGenerateLinePragmas);
	}

	@TODO
	static CodeGeneratorContext create(final RazorEngineHost host, final IAction<CodeWriter> writerFactory, final String className, final String rootPackage, final String sourceFile, final boolean shouldGenerateLinePragmas) {
		final CodeGeneratorContext context = new CodeGeneratorContext();
		context.host = host;
		context.codeWriterFactory = writerFactory;
		context.sourceFile = shouldGenerateLinePragmas ? sourceFile : null;
		context.compileUnit = new CodeCompileUnit();
		context.codePackage = new CodePackage(rootPackage);
		context.generatedClass = new CodeTypeDeclaration(className);
		context.generatedClass.isClass = true;
		context.targetMethod = new CodeMemberMethod();
		context.targetMethod.name = host.getGeneratedClassContext().getExecuteMethodName();
		context.targetMethod.attributes = MemberAttributes.Override | MemberAttributes.Public;
		context.codeMappings = Maps.newHashMap();

		context.compileUnit.getPackages().add(context.codePackage);
		context.codePackage.getTypes().add(context.generatedClass);
		context.generatedClass.getMembers().add(context.targetMethod);

//		context.codePackage.getImports().addAll(Iterables.transform(host.getPackageImports()));

		return context;
	}

	public void addDesignTypeHelperStatement(final CodeSnippetStatement statement) {

	}

	@TODO
	public int addCodeMapping(final SourceLocation sourceLocation, final int generatedCodeStart, final int generatedCodeLength) {
		if (generatedCodeLength == Integer.MAX_VALUE) {
			// Throw Exception
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

	@TODO
	public void bufferStatementFragment(final String fragment, final Span sourceSpan) {
		if (sourceSpan != null && currentBuffer.getLinePragmaSpan() == null) {
//			currentBuffer.setLinePragmaSpan(sourceSpan);
//			// Pad the output as necessary
//			int start = currentBuffer.getBuilder().length;
//			if ()
		}
		currentBuffer.getBuilder().append(fragment);
	}

	public void markStartOfGeneratedCode() {
		currentBuffer.markStart();
	}

	public void markEndOfGeneratedCode() {
		currentBuffer.markEnd();
	}

	@TODO
	public void flushBufferedStatement() {

	}

	public void addStatement(final String generatedCode) {
		addStatement(generatedCode, null);
	}

	@TODO
	public void addStatement(final String body, final CodeLinePragma pragma) {

	}

	@TODO
	public void ensureExpressionHelperVariable() {

	}

	@TODO
	public IDisposable changeStatementCollection(final Function<String, CodeLinePragma> collector) {
		return null;
	}

	@TODO
	public void addContextCall(final Span contentSpan, final String methodName, final boolean isLiteral) {

	}

	@TODO
	CodeWriter createCodeWriter() {
		assert codeWriterFactory != null;
		if (codeWriterFactory == null) {
			// Throw Exception
		}
		return codeWriterFactory.invoke();
	}

	String buildCodeString(final IVoidAction<CodeWriter> action) {
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

	public IAction<CodeWriter> getCodeWriterFactory() {
		return codeWriterFactory;
	}

	public void setCodeWriterFactory(IAction<CodeWriter> codeWriterFactory) {
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

	public Function<String, CodeLinePragma> getStatementCollector() {
		return statementCollector;
	}

	public void setStatementCollector(Function<String, CodeLinePragma> statementCollector) {
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
