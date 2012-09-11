package nextmethod.web.razor.parser;

import com.google.common.collect.ImmutableMap;
import nextmethod.base.Debug;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.JavaTokenizer;
import nextmethod.web.razor.tokenizer.symbols.JavaKeyword;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import nextmethod.web.razor.tokenizer.symbols.KnownSymbolType;

import javax.annotation.Nonnull;
import java.util.Map;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class JavaLanguageCharacteristics extends LanguageCharacteristics<JavaTokenizer, JavaSymbol, JavaSymbolType> {

	public static final JavaLanguageCharacteristics Instance = new JavaLanguageCharacteristics();

	private static Map<JavaSymbolType, String> symbolSamples = ImmutableMap.<JavaSymbolType, String>builder()
		.put(JavaSymbolType.Arrow, "->")
		.put(JavaSymbolType.Minus, "-")
		.put(JavaSymbolType.Decrement, "--")
		.put(JavaSymbolType.MinusAssign, "-=")
		.put(JavaSymbolType.NotEqual, "!=")
		.put(JavaSymbolType.Not, "!")
		.put(JavaSymbolType.Modulo, "%")
		.put(JavaSymbolType.ModuloAssign, "%=")
		.put(JavaSymbolType.AndAssign, "&=")
		.put(JavaSymbolType.And, "&")
		.put(JavaSymbolType.DoubleAnd, "&&")
		.put(JavaSymbolType.LeftParenthesis, "(")
		.put(JavaSymbolType.RightParenthesis, ")")
		.put(JavaSymbolType.Star, "*")
		.put(JavaSymbolType.MultiplyAssign, "*=")
		.put(JavaSymbolType.Comma, ",")
		.put(JavaSymbolType.Dot, ".")
		.put(JavaSymbolType.Slash, "/")
		.put(JavaSymbolType.DivideAssign, "/=")
		.put(JavaSymbolType.DoubleColon, "::")
		.put(JavaSymbolType.Colon, ":")
		.put(JavaSymbolType.Semicolon, ";")
		.put(JavaSymbolType.QuestionMark, "?")
		.put(JavaSymbolType.NullCoalesce, "??")
		.put(JavaSymbolType.RightBracket, "]")
		.put(JavaSymbolType.LeftBracket, "[")
		.put(JavaSymbolType.XorAssign, "^=")
		.put(JavaSymbolType.Xor, "^")
		.put(JavaSymbolType.LeftBrace, "{")
		.put(JavaSymbolType.OrAssign, "|=")
		.put(JavaSymbolType.DoubleOr, "||")
		.put(JavaSymbolType.Or, "|")
		.put(JavaSymbolType.RightBrace, "}")
		.put(JavaSymbolType.Tilde, "~")
		.put(JavaSymbolType.Plus, "+")
		.put(JavaSymbolType.PlusAssign, "+=")
		.put(JavaSymbolType.Increment, "++")
		.put(JavaSymbolType.LessThan, "<")
		.put(JavaSymbolType.LessThanEqual, "<=")
		.put(JavaSymbolType.LeftShift, "<<")
		.put(JavaSymbolType.LeftShiftAssign, "<<=")
		.put(JavaSymbolType.Assign, "=")
		.put(JavaSymbolType.Equals, "==")
		.put(JavaSymbolType.GreaterThan, ">")
		.put(JavaSymbolType.GreaterThanEqual, ">=")
		.put(JavaSymbolType.RightShift, ">>")
		.put(JavaSymbolType.RightShiftAssign, ">>>")
		.put(JavaSymbolType.Hash, "#")
		.put(JavaSymbolType.Transition, "@")
		.build();

	@Override
	public String getSample(@Nonnull final JavaSymbolType javaSymbolType) {
		return getSymbolSample(javaSymbolType);
	}

	@Override
	public JavaTokenizer createTokenizer(@Nonnull final ITextDocument source) {
		return new JavaTokenizer(source);
	}

	@Override
	public JavaSymbolType flipBracket(@Nonnull final JavaSymbolType bracket) {
		switch (bracket) {
			case LeftBrace:
				return JavaSymbolType.RightBrace;

			case LeftBracket:
				return JavaSymbolType.RightBracket;

			case LeftParenthesis:
				return JavaSymbolType.RightParenthesis;

			case LessThan:
				return JavaSymbolType.GreaterThan;

			case RightBrace:
				return JavaSymbolType.LeftBrace;

			case RightBracket:
				return JavaSymbolType.LeftBracket;

			case RightParenthesis:
				return JavaSymbolType.LeftParenthesis;

			case GreaterThan:
				return JavaSymbolType.LessThan;

			default:
				Debug.fail("flipBracket must be called with a bracket character");
				return JavaSymbolType.Unknown;
		}
	}

	@Override
	public JavaSymbol createMarkerSymbol(@Nonnull final SourceLocation location) {
		return new JavaSymbol(location, "", JavaSymbolType.Unknown);
	}

	@Override
	public JavaSymbolType getKnownSymbolType(@Nonnull final KnownSymbolType type) {
		switch (type) {
			case Identifier:
				return JavaSymbolType.Identifier;

			case Keyword:
				return JavaSymbolType.Keyword;

			case NewLine:
				return JavaSymbolType.NewLine;

			case WhiteSpace:
				return JavaSymbolType.WhiteSpace;

			case Transition:
				return JavaSymbolType.Transition;

			case CommentStart:
				return JavaSymbolType.RazorCommentTransition;

			case CommentStar:
				return JavaSymbolType.RazorCommentStar;

			case CommentBody:
				return JavaSymbolType.RazorComment;

			default:
				return JavaSymbolType.Unknown;
		}
	}

	@Override
	protected JavaSymbol createSymbol(@Nonnull final SourceLocation location, @Nonnull final String content, @Nonnull final JavaSymbolType javaSymbolType, @Nonnull final Iterable<RazorError> errors) {
		return new JavaSymbol(location, content, javaSymbolType, errors);
	}

	public static String getKeyword(final JavaKeyword keyword) {
		return keyword.toString().toLowerCase();
	}

	public static String getSymbolSample(@Nonnull final JavaSymbolType type) {
		String sample;
		if (!symbolSamples.containsKey(type)) {
			switch (type) {
				case Identifier:
					return RazorResources().getString("javaSymbol.identifier");

				case Keyword:
					return RazorResources().getString("javaSymbol.keyword");

				case IntegerLiteral:
					return RazorResources().getString("javaSymbol.integerLiteral");

				case NewLine:
					return RazorResources().getString("javaSymbol.newline");

				case WhiteSpace:
					return RazorResources().getString("javaSymbol.whitespace");

				case Comment:
					return RazorResources().getString("javaSymbol.comment");

				case RealLiteral:
					return RazorResources().getString("javaSymbol.realLiteral");

				case CharacterLiteral:
					return RazorResources().getString("javaSymbol.characterLiteral");

				case StringLiteral:
					return RazorResources().getString("javaSymbol.stringLiteral");

				default:
					return RazorResources().getString("symbol.unknown");
			}
		}
		else {
			sample = symbolSamples.get(type);
		}
		return sample;
	}
}
