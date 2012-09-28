package nextmethod.web.razor.parser;

import nextmethod.base.Debug;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.HtmlTokenizer;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import nextmethod.web.razor.tokenizer.symbols.KnownSymbolType;

import javax.annotation.Nonnull;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
public class HtmlLanguageCharacteristics extends LanguageCharacteristics<HtmlTokenizer, HtmlSymbol, HtmlSymbolType> {

	public static final HtmlLanguageCharacteristics Instance = new HtmlLanguageCharacteristics();

	@Override
	public String getSample(@Nonnull final HtmlSymbolType type) {
		switch (type) {
			case Text:
				return RazorResources().getString("htmlSymbol.text");
			case WhiteSpace:
				return RazorResources().getString("htmlSymbol.whiteSpace");
			case NewLine:
				return RazorResources().getString("htmlSymbol.newLine");
			case OpenAngle:
				return "<";
			case Bang:
				return "!";
			case QuestionMark:
				return "?";
			case DoubleHyphen:
				return "--";
			case LeftBracket:
				return "[";
			case CloseAngle:
				return ">";
			case RightBracket:
				return "]";
			case Equals:
				return "=";
			case DoubleQuote:
				return "\"";
			case SingleQuote:
				return "'";
			case Transition:
				return "@";
			case Colon:
				return ":";
			case RazorComment:
				return RazorResources().getString("htmlSymbol.razorComment");
			case RazorCommentStar:
				return "*";
			case RazorCommentTransition:
				return "@";
			default:
				return RazorResources().getString("symbol.unknown");
		}
	}

	@Override
	public HtmlTokenizer createTokenizer(@Nonnull final ITextDocument source) {
		return new HtmlTokenizer(source);
	}

	@Override
	public HtmlSymbolType flipBracket(@Nonnull final HtmlSymbolType bracket) {
		switch (bracket) {
			case LeftBracket:
				return HtmlSymbolType.RightBracket;
			case OpenAngle:
				return HtmlSymbolType.CloseAngle;
			case RightBracket:
				return HtmlSymbolType.LeftBracket;
			case CloseAngle:
				return HtmlSymbolType.OpenAngle;
			default:
				Debug.fail("flipBracket must be called with a bracket character");
				return HtmlSymbolType.Unknown;
		}
	}

	@Override
	public HtmlSymbol createMarkerSymbol(@Nonnull final SourceLocation location) {
		return new HtmlSymbol(location, "", HtmlSymbolType.Unknown);
	}

	@Override
	public HtmlSymbolType getKnownSymbolType(@Nonnull final KnownSymbolType type) {
		switch (type) {
			case CommentStart:
				return HtmlSymbolType.RazorCommentTransition;
			case CommentStar:
				return HtmlSymbolType.RazorCommentStar;
			case CommentBody:
				return HtmlSymbolType.RazorComment;
			case Identifier:
				return HtmlSymbolType.Text;
			case Keyword:
				return HtmlSymbolType.Text;
			case NewLine:
				return HtmlSymbolType.NewLine;
			case Transition:
				return HtmlSymbolType.Transition;
			case WhiteSpace:
				return HtmlSymbolType.WhiteSpace;
			default:
				return HtmlSymbolType.Unknown;
		}
	}

	@Override
	protected HtmlSymbol createSymbol(@Nonnull final SourceLocation location, @Nonnull final String content, @Nonnull final HtmlSymbolType htmlSymbolType, @Nonnull final Iterable<RazorError> errors) {
		return new HtmlSymbol(location, content, htmlSymbolType, errors);
	}
}
