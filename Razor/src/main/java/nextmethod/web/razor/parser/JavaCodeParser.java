package nextmethod.web.razor.parser;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import nextmethod.base.Delegates;
import nextmethod.base.OutParam;
import nextmethod.web.razor.tokenizer.JavaTokenizer;
import nextmethod.web.razor.tokenizer.symbols.JavaKeyword;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

public class JavaCodeParser extends TokenizerBackedParser<JavaTokenizer, JavaSymbol, JavaSymbolType> {

	static final int UsingKeywordLength = 5;
	static final ImmutableSet<String> DefaultKeywords = ImmutableSet.<String>builder()
		.add("if")
		.add("do")
		.add("try")
		.add("for")
		.add("while")
		.add("switch")
		.add("lock")
		.add("try")
		.add("section")
		.add("inherits")
		.add("helper")
		.add("functions")
		.add("package")
		.add("class")
		.add("layout")
		.add("sessionstate")
		.build();

	private Map<String, Delegates.IAction> directiveParsers = Maps.newHashMap();
	private Map<JavaKeyword, Delegates.IAction1<Boolean>> keywordParsers = Maps.newHashMap();

	protected final Set<String> keywords;
	private boolean isNested;

	public JavaCodeParser() {
		this.keywords = Sets.newHashSet();
		setupKeywords();
		setupDirectives();
	}

	private void setupKeywords() {

	}

	private void mapKeywords(final Delegates.IAction1<Boolean> handler, final JavaKeyword... keywords) {
		mapKeywords(handler, true, keywords);
	}

	private void mapKeywords(final Delegates.IAction1<Boolean> handler, boolean topLevel, final JavaKeyword... keywords) {
		for (JavaKeyword keyword : keywords) {
			keywordParsers.put(keyword, handler);
			if (topLevel) {
				this.keywords.add(JavaLanguageCharacteristics.getKeyword(keyword));
			}
		}
	}

	private void setupDirectives() {

	}

	protected void mapDirectives(final Delegates.IAction handler, final String... directives) {
		for (String directive : directives) {
			directiveParsers.put(directive, handler);
			this.keywords.add(directive);
		}
	}

	protected boolean tryGetDirectiveHandler(final String directive, @Nonnull final OutParam<Delegates.IAction> handler) {
		if (directiveParsers.containsKey(directive)) {
			handler.set(directiveParsers.get(directive));
			return true;
		}
		return false;
	}

	@Override
	protected LanguageCharacteristics<JavaTokenizer, JavaSymbol, JavaSymbolType> getLanguage() {
		return JavaLanguageCharacteristics.Instance;
	}

	@Override
	protected ParserBase getOtherParser() {
		return getContext().getMarkupParser();
	}

	@Override
	public void parseBlock() {
	}
}
