package nextmethod.web.razor.text;

import nextmethod.base.IDisposable;

public abstract class LookaheadTextReader extends TextReader {

	public abstract SourceLocation getCurrentLocation();
	public abstract IDisposable beginLookahead();
	public abstract void cancelBacktrack();

}
