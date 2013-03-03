package nextmethod.web.razor.generator;

public enum ExpressionRenderingMode {

	// Indicates that expressions should be written to the output stream
	WriteToOutput,

	// Inidicates that expressions should simply be placed as-is in the code, and the context in which
	// the code exists will be used to render it
	InjectCode
}
