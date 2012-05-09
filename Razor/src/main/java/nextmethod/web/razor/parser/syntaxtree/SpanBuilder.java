package nextmethod.web.razor.parser.syntaxtree;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import nextmethod.web.razor.editor.SpanEditHandler;
import nextmethod.web.razor.generator.ISpanCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.SourceLocationTracker;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 *
 */
public class SpanBuilder {

	private final SourceLocationTracker tracker = new SourceLocationTracker();
	private List<ISymbol> symbolList = Lists.newArrayList();

	private SourceLocation start;
	private SpanKind kind;
	private SpanEditHandler editHandler;
	private ISpanCodeGenerator codeGenerator;

	public SpanBuilder(@Nonnull final Span original) {
		this.kind = original.getKind();
		this.symbolList = Lists.newArrayList(original.getSymbols());
		this.editHandler = original.getEditHandler();
		this.codeGenerator = original.getCodeGenerator();
		this.start = original.getStart();
	}

	public SpanBuilder() {
		this.reset();
	}

	public void reset() {
		this.symbolList = Lists.newArrayList();
		this.editHandler = SpanEditHandler.createDefault();
		this.codeGenerator = SpanCodeGenerator.Null;
		this.start = SourceLocation.Zero;
	}

	public Span build () {
		return new Span(this);
	}

	public void clearSymbols() {
		this.symbolList.clear();
	}

	public void accept(@Nullable final ISymbol symbol) {
		if (symbol == null) return;

		if (symbolList.isEmpty()) {
			this.start = symbol.getStart();
			symbol.changeStart(SourceLocation.Zero);
			tracker.setCurrentLocation(SourceLocation.Zero);
		}
		else {
			symbol.changeStart(tracker.getCurrentLocation());
		}
		this.symbolList.add(symbol);
		this.tracker.updateLocation(symbol.getContent());
	}

	public ImmutableCollection<ISymbol> symbols() {
		return ImmutableList.copyOf(symbolList);
	}

	public SpanKind getKind() {
		return kind;
	}

	public void setKind(@Nonnull final SpanKind kind) {
		this.kind = kind;
	}

	public SourceLocation getStart() {
		return start;
	}

	public void setStart(@Nonnull final SourceLocation start) {
		this.start = start;
	}
}
