package nextmethod.web.razor.parser.partialparsing;

import com.google.common.util.concurrent.Monitor;
import nextmethod.base.Delegates;
import nextmethod.base.IEventHandler;
import nextmethod.base.Strings;
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
import nextmethod.web.razor.utils.MiscUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.Assert.assertEquals;
import static nextmethod.web.razor.utils.MiscUtils.createTestFilePath;

public abstract class PartialParsingTestBase<TLanguage extends RazorCodeLanguage> {

	private static final String TestLinePragmaFileName = createTestFilePath(
		"This",
		"Path",
		"Is",
		"Just",
		"For",
		"Line",
		"Pragmas.rzhtml"
	);

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
		assertEquals(2, manager.parseCount.get());
	}

	protected void runPartialParseTest(final TextChange change, final Block newTreeRoot) {
		runPartialParseTest(change, newTreeRoot, EnumSet.noneOf(PartialParseResult.class));
	}

	protected void runPartialParseTest(final TextChange change, final Block newTreeRoot, final PartialParseResult... additionalFlags) {
		final EnumSet<PartialParseResult> enumSet;
		if (additionalFlags.length == 1) {
			enumSet = EnumSet.of(additionalFlags[0]);
		}
		else {
			enumSet = EnumSet.noneOf(PartialParseResult.class);
			for (PartialParseResult flag : additionalFlags) {
				enumSet.add(flag);
			}
		}
		runPartialParseTest(change, newTreeRoot, enumSet);
	}

	protected void runPartialParseTest(final TextChange change, final Block newTreeRoot, final EnumSet<PartialParseResult> additionalFlags) {
		final EnumSet<PartialParseResult> expected = EnumSet.of(PartialParseResult.Accepted);
		expected.addAll(additionalFlags);
		final TestParserManager manager = createParserManager();
		manager.initializeWithDocument(change.getOldBuffer());

		final EnumSet<PartialParseResult> results = manager.checkForStructureChangesAndWait(change);

		assertEquals(expected, results);
		assertEquals(1, manager.parseCount.get());
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

	protected class TestParserManager {

		private final Monitor parserMonitor = new Monitor();
		private final Monitor.Guard parseCompleteGuard = new Monitor.Guard(parserMonitor) {
			@Override
			public boolean isSatisfied() {
				return parseComplete.get();
			}
		};
		public final RazorEditorParser parser;
		public final AtomicBoolean parseComplete;
		public final AtomicInteger parseCount = new AtomicInteger(0);

		public TestParserManager(final RazorEditorParser parser) {
			this.parser = parser;
			this.parseComplete = new AtomicBoolean(false);

			parser.setDocumentParseCompleteHandler(new IEventHandler<DocumentParseCompleteEventArgs>() {
				@Override
				public void handleEvent(@Nonnull final Object sender, @Nonnull final DocumentParseCompleteEventArgs e) {
					parserMonitor.enter();
					try {
						parseCount.incrementAndGet();
						parseComplete.set(true);
					}
					finally {
						parserMonitor.leave();
					}
				}
			});
		}

		public void initializeWithDocument(final ITextBuffer startDocument) {
			checkForStructureChangesAndWait(new TextChange(0, 0, new StringTextBuffer(Strings.Empty), startDocument.getLength(), startDocument));
		}

		public EnumSet<PartialParseResult> checkForStructureChangesAndWait(final TextChange change) {
			final EnumSet<PartialParseResult> result = parser.checkForStructureChanges(change);
			if (result.contains(PartialParseResult.Rejected)) {
				waitForParse();
			}
			return result;
		}

		public void waitForParse() {
			// Wait for parse to finish
			MiscUtils.DoWithTimeoutIfNotDebugging(new Delegates.IFunc1<Long, Boolean>() {
				@Override
				public Boolean invoke(@Nullable final Long timeoutInMillis) {
					assert timeoutInMillis != null;
					boolean wasSignaled = parserMonitor.enterWhenUninterruptibly(parseCompleteGuard, timeoutInMillis, TimeUnit.MILLISECONDS);
					try {
						parseComplete.set(false);
					}
					finally {
						if (parserMonitor.isOccupiedByCurrentThread()) {
							parserMonitor.leave();
						}
					}
					return wasSignaled;
				}
			});
		}
	}

}
