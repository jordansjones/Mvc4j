package nextmethod.codedom.compiler;

import nextmethod.base.NotImplementedException;
import nextmethod.codedom.CodeCompileUnit;

import javax.annotation.Nonnull;
import java.io.Writer;

// TODO
public abstract class CodeDomProvider {

	public void generateCodeFromCompileUnit(@Nonnull final CodeCompileUnit compileUnit, @Nonnull final Writer writer, @Nonnull final CodeGeneratorOptions options) {
		throw new NotImplementedException();
	}

}
