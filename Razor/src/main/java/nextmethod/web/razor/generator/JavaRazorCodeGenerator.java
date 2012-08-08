package nextmethod.web.razor.generator;

import nextmethod.base.Delegates;
import nextmethod.codedom.CodeSnippetTypeMember;
import nextmethod.web.razor.RazorEngineHost;

import javax.annotation.Nonnull;

public class JavaRazorCodeGenerator extends RazorCodeGenerator {

	private static final String HiddenLinePragma = "#line hidden";


	protected JavaRazorCodeGenerator(@Nonnull final String className, @Nonnull final String rootPackageName, @Nonnull final String sourceFileName, @Nonnull final RazorEngineHost host) {
		super(className, rootPackageName, sourceFileName, host);
	}

	@Override
	public Delegates.IFunc<CodeWriter> getCodeWriterFactory() {
		return new Delegates.IFunc<CodeWriter>() {
			@Override
			public CodeWriter invoke() {
				return new JavaCodeWriter();
			}
		};
	}

	@Override
	protected void initialize(@Nonnull final CodeGeneratorContext context) {
		super.initialize(context);
		context.getGeneratedClass().getMembers().add(0, new CodeSnippetTypeMember(HiddenLinePragma));
	}
}
