package nextmethod.web.razor.generator;

import nextmethod.base.EventArgs;
import nextmethod.codedom.CodeCompileUnit;

import java.util.Objects;

public class CodeGenerationCompleteEventArgs extends EventArgs {

	private final CodeCompileUnit generatedCode;
	private final String virtualPath;
	private final String physicalPath;

	public CodeGenerationCompleteEventArgs(final String virtualPath, final String physicalPath, final CodeCompileUnit generatedCode) {
		Objects.requireNonNull(virtualPath);
		Objects.requireNonNull(generatedCode);

		this.virtualPath = virtualPath;
		this.physicalPath = physicalPath;
		this.generatedCode = generatedCode;
	}

	public CodeCompileUnit getGeneratedCode() {
		return generatedCode;
	}

	public String getVirtualPath() {
		return virtualPath;
	}

	public String getPhysicalPath() {
		return physicalPath;
	}
}
