package nextmethod.web.razor.parser;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.NotImplementedException;
import nextmethod.web.razor.generator.MarkupCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.HtmlTokenizer;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 */
public class HtmlMarkupParser extends TokenizerBackedParser<HtmlTokenizer, HtmlSymbol, HtmlSymbolType> {

    protected final ImmutableSet<String> voidElements = ImmutableSet.<String>builder()
            .add("area")
            .add("base")
            .add("br")
            .add("col")
            .add("command")
            .add("embed")
            .add("hr")
            .add("img")
            .add("input")
            .add("keygen")
            .add("link")
            .add("meta")
            .add("param")
            .add("source")
            .add("track")
            .add("wbr")
            .build();

	protected final HtmlMarkupParserBlock blockParser;

	public HtmlMarkupParser() {
		this.blockParser = new HtmlMarkupParserBlock(this);
	}

	public ImmutableSet<String> getVoidElements() {
        return voidElements;
    }

    @Override
	protected LanguageCharacteristics<HtmlTokenizer, HtmlSymbol, HtmlSymbolType> getLanguage() {
		return HtmlLanguageCharacteristics.Instance;
	}

	@Override
	protected ParserBase getOtherParser() {
		return getContext().getCodeParser();
	}

	@Override
	public void buildSpan(@Nonnull final SpanBuilder span, @Nonnull final SourceLocation start, @Nonnull final String content) {
		span.setKind(SpanKind.Markup);
		span.setCodeGenerator(new MarkupCodeGenerator());
		super.buildSpan(span, start, content);
	}

	@Override
	protected void outputSpanBeforeRazorComment() {
		output(SpanKind.Markup);
	}

	protected void skipToAndParseCode(@Nonnull final HtmlSymbolType type) {
		skipToAndParseCode(new Delegates.IFunc1<HtmlSymbol, Boolean>() {
			@Override
			public Boolean invoke(@Nullable final HtmlSymbol symbol) {
				return symbol != null && symbol.getType() == type;
			}
		});
	}

	protected void skipToAndParseCode(@Nonnull final Delegates.IFunc1<HtmlSymbol, Boolean> condition) {
		HtmlSymbol last = null;
		boolean startOfLine = false;
		while (!isEndOfFile() && Boolean.FALSE.equals(condition.invoke(getCurrentSymbol()))) {
			if (at(HtmlSymbolType.NewLine)) {
				if (last != null) {
					accept(last);
				}

				// Mark the start of a new line
				startOfLine = true;
				last = null;
				acceptAndMoveNext();
			}
			else if (at(HtmlSymbolType.Transition)) {
				final HtmlSymbol transition = getCurrentSymbol();
				nextToken();
				if (at(HtmlSymbolType.Transition)) {
					if (last != null) {
						accept(last);
						last = null;
					}
					output(SpanKind.Markup);
					accept(transition);
					getSpan().setCodeGenerator(SpanCodeGenerator.Null);
					output(SpanKind.Markup);
					acceptAndMoveNext();
					continue; // While loop
				}
				else {
					if (!isEndOfFile()) {
						putCurrentBack();
					}
					putBack(transition);
				}

				// Handle whitespace rewriting
				if (last != null) {
					if (!getContext().isDesignTimeMode() && last.getType() == HtmlSymbolType.WhiteSpace && startOfLine) {
						// Put the whitespace back too
						startOfLine = false;
						putBack(last);
						last = null;
					}
					else {
						// Accept last
						accept(last);
						last = null;
					}
				}

				otherParserBlock();
			}
			else if (at(HtmlSymbolType.RazorCommentTransition)) {
				if (last != null) {
					accept(last);
					last = null;
				}
				addMarkerSymbolIfNecessary();
				output(SpanKind.Markup);
				razorComment();
			}
			else {
				// As long as we see whitespace, we're still at the "start" of the line
				startOfLine &= at(HtmlSymbolType.WhiteSpace);

				// If there's a last token, accept it
				if (last != null) {
					accept(last);
					last = null;
				}

				// Advance
				last = getCurrentSymbol();
				nextToken();
			}
		}

		if (last != null) {
			accept(last);
		}
	}

	protected static Delegates.IFunc1<HtmlSymbol, Boolean> isSpacingToken(final boolean includeNewLines) {
		return new Delegates.IFunc1<HtmlSymbol, Boolean>() {
			@Override
			public Boolean invoke(@Nullable final HtmlSymbol symbol) {
				return symbol != null && (
					symbol.getType() == HtmlSymbolType.WhiteSpace
					|| (includeNewLines && symbol.getType() == HtmlSymbolType.NewLine)
				);
			}
		};
	}

	protected void otherParserBlock() {
		addMarkerSymbolIfNecessary();
		output(SpanKind.Markup);
		try(IDisposable d = pushSpanConfig()) {
			getContext().switchActiveParser();
			getContext().getCodeParser().parseBlock();
			getContext().switchActiveParser();
		}
		initialize(getSpan());
		nextToken();
	}

	@Override
	public void parseBlock() {
		this.blockParser.parseBlock();
	}
}
