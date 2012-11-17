package nextmethod.web.razor.parser.partialparsing;

import nextmethod.base.IEventHandler;
import nextmethod.web.razor.DocumentParseCompleteEventArgs;
import nextmethod.web.razor.PartialParseResult;
import nextmethod.web.razor.RazorCodeLanguage;
import nextmethod.web.razor.RazorEditorParser;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.StringTextBuffer;
import nextmethod.web.razor.framework.ParserTestBase;
import nextmethod.web.razor.generator.GeneratedClassContext;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.text.ITextBuffer;
import nextmethod.web.razor.text.TextChange;

import javax.annotation.Nonnull;
import java.util.EnumSet;

import static junit.framework.Assert.assertEquals;

public abstract class PartialParsingTestBase<TLanguage extends RazorCodeLanguage> {

	private static final String TestLinePragmaFileName = "C:\\This\\Path\\Is\\Just\\For\\Line\\Pragmas.rzhtml";

	protected void runFullReparseTest(final TextChange change) {
		runFullReparseTest(change, EnumSet.noneOf(PartialParseResult.class));
	}

	protected void runFullReparseTest(final TextChange change, final EnumSet<PartialParseResult> additionalFlags) {
		final EnumSet<PartialParseResult> expected = EnumSet.of(PartialParseResult.Rejected);
		expected.addAll(additionalFlags);
		final TestParserManager manager = createParserManager();
		manager.initializeWithDocument(change.getOldBuffer());

		final EnumSet<PartialParseResult> result = manager.checkForStructureChangesAndWait(change);

		assertEquals(expected, result);
		assertEquals(2, manager.parseCount);
	}

	protected void runPartialParseTest(final TextChange change, final Block newTreeRoot) {
		runPartialParseTest(change, newTreeRoot, EnumSet.noneOf(PartialParseResult.class));
	}

	protected void runPartialParseTest(final TextChange change, final Block newTreeRoot, final EnumSet<PartialParseResult> additionalFlags) {
		final EnumSet<PartialParseResult> expected = EnumSet.of(PartialParseResult.Accepted);
		expected.addAll(additionalFlags);
		final TestParserManager manager = createParserManager();
		manager.initializeWithDocument(change.getOldBuffer());

		final EnumSet<PartialParseResult> results = manager.checkForStructureChangesAndWait(change);

		assertEquals(expected, results);
		assertEquals(1, manager.parseCount);
		ParserTestBase.evaluateParseTree(manager.parser.getCurrentParseTree(), newTreeRoot);
	}

	protected void runTypeKeywordTest(final String keyword) {
		final String before = "@" + keyword.substring(0, keyword.length() - 1);
		final String after = "@" + keyword;
		final StringTextBuffer changed = new StringTextBuffer(after);
		final StringTextBuffer old = new StringTextBuffer(before);
		runFullReparseTest(new TextChange(keyword.length(), 0, old, 1, changed), PartialParseResult.setOf(PartialParseResult.SpanContextChanged));
	}

	protected TestParserManager createParserManager() {
		final RazorEngineHost host = createHost();
		final RazorEditorParser parser = new RazorEditorParser(host, TestLinePragmaFileName);
		return new TestParserManager(parser);
	}

	protected abstract TLanguage createNewLanguage();

	protected RazorEngineHost createHost() {
		final RazorEngineHost host = new RazorEngineHost(createNewLanguage());
		host.setGeneratedClassContext(new GeneratedClassContext(
			"Execute",
			"Write",
			"WriteLiteral",
			"WriteTo",
			"WriteLiteralTo",
			"Template",
			"DefineSection"
		));
		host.setDesignTimeMode(true);
		return host;
	}

	// TODO
	protected class TestParserManager {

		public RazorEditorParser parser;
//		public ManualResetEventSlim
		public int parseCount;

		public TestParserManager(final RazorEditorParser parser) {
			this.parseCount = 0;
			this.parser = parser;

			parser.setDocumentParseCompleteHandler(new IEventHandler<DocumentParseCompleteEventArgs>() {
				@Override
				public void handleEvent(@Nonnull final Object sender, @Nonnull final DocumentParseCompleteEventArgs e) {

				}
			});
		}

		public void initializeWithDocument(final ITextBuffer startDocument) {
			checkForStructureChangesAndWait(new TextChange(0, 0, new StringTextBuffer(""), startDocument.getLength(), startDocument));
		}

		public EnumSet<PartialParseResult> checkForStructureChangesAndWait(final TextChange change) {
			final EnumSet<PartialParseResult> result = parser.checkForStructureChanges(change);
			if (result.contains(PartialParseResult.Rejected)) {
				waitForParse();
			}
			return result;
		}

		public void waitForParse() {

		}
	}
}
