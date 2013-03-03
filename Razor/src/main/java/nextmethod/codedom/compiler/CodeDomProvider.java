package nextmethod.codedom.compiler;

import nextmethod.base.NotImplementedException;
import nextmethod.codedom.CodeCompileUnit;

import javax.annotation.Nonnull;
import java.io.PrintWriter;

// TODO
public abstract class CodeDomProvider {


	protected abstract ICodeGenerator createGenerator();

	public void generateCodeFromCompileUnit(@Nonnull final CodeCompileUnit compileUnit, @Nonnull final PrintWriter writer, @Nonnull final CodeGeneratorOptions options) {
		throw new NotImplementedException();
	}

}
