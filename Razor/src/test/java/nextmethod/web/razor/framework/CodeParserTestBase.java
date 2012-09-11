package nextmethod.web.razor.framework;

import nextmethod.web.razor.parser.ParserBase;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 *
 */
public abstract class CodeParserTestBase extends ParserTestBase {

	protected abstract Set<String> getKeywordSet();

	@Override
	protected ParserBase selectActiveParser(@Nonnull final ParserBase codeParser, @Nonnull final ParserBase markupParser) {
		return codeParser;
	}
}
