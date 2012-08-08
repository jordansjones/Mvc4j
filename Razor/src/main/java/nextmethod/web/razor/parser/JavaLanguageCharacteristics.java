package nextmethod.web.razor.parser;

import com.google.common.collect.Maps;
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

public class JavaLanguageCharacteristics extends LanguageCharacteristics<JavaTokenizer, JavaSymbol, JavaSymbolType> {

	protected static final JavaLanguageCharacteristics Instance = new JavaLanguageCharacteristics();

	private static Map<JavaSymbolType, String> symbolSamples = Maps.newHashMap();

	@Override
	public String getSample(@Nonnull final JavaSymbolType javaSymbolType) {
		return null;
	}

	@Override
	public JavaTokenizer createTokenizer(@Nonnull final ITextDocument source) {
		return null;
	}

	@Override
	public JavaSymbolType flipBracket(@Nonnull final JavaSymbolType bracket) {
		return null;
	}

	@Override
	public JavaSymbol createMarkerSymbol(@Nonnull final SourceLocation location) {
		return null;
	}

	@Override
	public JavaSymbolType getKnownSymbolType(@Nonnull final KnownSymbolType type) {
		return null;
	}

	@Override
	protected JavaSymbol createSymbol(@Nonnull final SourceLocation location, @Nonnull final String content, @Nonnull final JavaSymbolType javaSymbolType, @Nonnull final Iterable<RazorError> errors) {
		return null;
	}

	public static String getKeyword(final JavaKeyword keyword) {
		return keyword.toString().toLowerCase();
	}
}
