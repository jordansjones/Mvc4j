package nextmethod.web.razor.generator;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import nextmethod.base.Delegates;
import nextmethod.codedom.CodeConstructor;
import nextmethod.codedom.CodePackageImport;
import nextmethod.codedom.CodeTypeReference;
import nextmethod.codedom.MemberAttributes;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.generator.internal.CodeWriter;
import nextmethod.web.razor.parser.ParserVisitor;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.Span;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class RazorCodeGenerator extends ParserVisitor {

	private CodeGeneratorContext context;

	// Data pull from constructor
	private final String className;
	private final String rootPackageName;
	private final String sourceFileName;
	private final RazorEngineHost host;

	// Generation Settings
	private boolean generateLinePragmas;
	private boolean designTimeMode;

	protected RazorCodeGenerator(@Nonnull final String className, @Nonnull final String rootPackageName, @Nonnull final String sourceFileName, @Nonnull final RazorEngineHost host) {
		this.className = className;
		this.rootPackageName = rootPackageName;
		this.sourceFileName = sourceFileName;
		this.host = host;
		this.generateLinePragmas = Strings.isNullOrEmpty(sourceFileName);
	}

	protected Delegates.IFunc<CodeWriter> getCodeWriterFactory() {
		return null;
	}

	@Override
	public void visitStartBlock(@Nonnull final Block block) {
		block.getCodeGenerator().generateStartBlockCode(block, getContext());
	}

	@Override
	public void visitEndBlock(@Nonnull final Block block) {
		block.getCodeGenerator().generateEndBlockCode(block, getContext());
	}

	@Override
	public void visitSpan(@Nonnull final Span span) {
		span.getCodeGenerator().generateCode(span, getContext());
	}

	@Override
	public void onComplete() {
		context.flushBufferedStatement();
	}

	private void ensureContextInitialized() {
		if (context == null) {
			context = CodeGeneratorContext.create(host, getCodeWriterFactory(), className, rootPackageName, sourceFileName, generateLinePragmas);
			initialize(context);
		}
	}

	protected void initialize(@Nonnull final CodeGeneratorContext context) {
		final Iterable<CodePackageImport> imports = Iterables.transform(host.getPackageImports(), new Function<String, CodePackageImport>() {
			@Override
			public CodePackageImport apply(@Nullable final String input) {
				return Strings.isNullOrEmpty(input) ? null : new CodePackageImport(input);
			}
		});
		Iterables.addAll(context.getCodePackage().getImports(), imports);

		if (!Strings.isNullOrEmpty(host.getDefaultBaseClass())) {
			context.getGeneratedClass().getBaseTypes().add(new CodeTypeReference(host.getDefaultBaseClass()));
		}
		// Generate explicit parameter-less constructor on Razor generated class
		final CodeConstructor codeConstructor = new CodeConstructor();
		codeConstructor.setAttributes(MemberAttributes.Public);
		context.getGeneratedClass().getMembers().add(codeConstructor);
	}

	public CodeGeneratorContext getContext() {
		ensureContextInitialized();
		return context;
	}

	public String getClassName() {
		return className;
	}

	public String getRootPackageName() {
		return rootPackageName;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public RazorEngineHost getHost() {
		return host;
	}

	public boolean isGenerateLinePragmas() {
		return generateLinePragmas;
	}

	public void setGenerateLinePragmas(final boolean generateLinePragmas) {
		this.generateLinePragmas = generateLinePragmas;
	}

	public boolean isDesignTimeMode() {
		return designTimeMode;
	}

	public void setDesignTimeMode(final boolean designTimeMode) {
		this.designTimeMode = designTimeMode;
	}
}
