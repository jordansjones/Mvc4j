package nextmethod.web.razor.parser;

import com.google.common.base.Equivalence;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.web.razor.ParserResults;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.RazorError;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.text.ITextDocument;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.text.TextDocumentReader;
import nextmethod.web.razor.utils.DisposableAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Deque;
import java.util.EnumSet;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.base.Debug.isAssertEnabled;
import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class ParserContext {

	private Optional<Integer> ownerTaskId;
	private boolean terminated = false;

	private final Deque<BlockBuilder> blockStack = Queues.newArrayDeque();

	private final List<RazorError> errors;
	private final TextDocumentReader source;
	private final ParserBase codeParser;
	private final ParserBase markupParser;
	private ParserBase activeParser;

	private Span lastSpan;

	private boolean designTimeMode;
	private boolean whiteSpaceIsSignificantToAncestorBlock;

	public ParserContext(@Nonnull final ITextDocument source, @Nonnull final ParserBase codeParser, @Nonnull final ParserBase markupParser, @Nonnull final ParserBase activeParser) {
		checkNotNull(source, "source");
		checkNotNull(codeParser, "codeParser");
		checkNotNull(markupParser, "markupParser");
		checkNotNull(activeParser, "activeParser");

		if (activeParser != codeParser && activeParser != markupParser) {
			throw new IllegalArgumentException(RazorResources().getString("activeParser.must.be.code.or.markup.parser"));
		}

		captureOwnerTask();

		this.source = new TextDocumentReader(source);
		this.codeParser = codeParser;
		this.markupParser = markupParser;
		this.activeParser = activeParser;
		this.errors = Lists.newArrayList();
	}

	public ParserBase getMarkupParser() {
		return this.markupParser;
	}

	public List<RazorError> getErrors() {
		return this.errors;
	}

	public ITextDocument getSource() {
		return this.source;
	}

	public BlockBuilder getCurrentBlock() {
		return blockStack.peek();
	}

	public Span getLastSpan() {
		return lastSpan;
	}

	public boolean isWhiteSpaceIsSignificantToAncestorBlock() {
		return whiteSpaceIsSignificantToAncestorBlock;
	}

	public EnumSet<AcceptedCharacters> getLastAcceptedCharacters() {
		return lastSpan == null ? EnumSet.of(AcceptedCharacters.None) : lastSpan.getEditHandler().getAcceptedCharacters();
	}

	public Deque<BlockBuilder> getBlockStack() {
		return blockStack;
	}

	public char getCurrentCharacter() {
		if (terminated) return '\0';
		if (isAssertEnabled() && checkInfiniteLoop()) return '\0';
		final int ch = source.peek();
		return ch == -1 ? '\0' : (char) ch;
	}

	public boolean isEndOfFile() {
		return terminated || source.peek() == -1;
	}

	public void addSpan(@Nonnull final Span span) {
		ensureNotTerminated();
		if (blockStack.size() == 0) {
			throw new UnsupportedOperationException(RazorResources().getString("parserContext.noCurrentBlock"));
		}
		blockStack.peek().getChildren().add(span);
		lastSpan = span;
	}

	/**
	 * Starts a block of the specified type
	 * @param blockType    The type of the block to start
	 */
	public IDisposable startBlock(@Nonnull final BlockType blockType) {
		ensureNotTerminated();
		assertOnOwnerTask();
		final BlockBuilder blockBuilder = new BlockBuilder();
		blockBuilder.setType(blockType);
		blockStack.push(blockBuilder);
		return new DisposableAction(new Delegates.IAction() {
			@Override
			public void invoke() {
				endBlock();
			}
		});
	}

	/**
	 * Starts a new block
	 * @return
	 */
	public IDisposable startBlock() {
		ensureNotTerminated();
		assertOnOwnerTask();
		blockStack.push(new BlockBuilder());
		return new DisposableAction(new Delegates.IAction() {
			@Override
			public void invoke() {
				endBlock();
			}
		});
	}

	/**
	 * Ends the current block
	 */
	public void endBlock() {
		ensureNotTerminated();
		assertOnOwnerTask();
		if (blockStack.isEmpty()) {
			throw new UnsupportedOperationException(RazorResources().getString("endBlock.called.without.matching.startBlock"));
		}
		if (blockStack.size() > 1) {
			final BlockBuilder block = blockStack.pop();
			blockStack.peek().getChildren().add(block.build());
		}
		else {
			// If we're at 1, terminate the parser
			terminated = true;
		}
	}

	/**
	 * Gets a boolean indicating if any of the ancestors of the current block is of the specified type
	 */
	public boolean isWithin(@Nonnull final BlockType type) {
		return Iterables.any(blockStack, new Predicate<BlockBuilder>() {
			@Override
			public boolean apply(@Nullable final BlockBuilder input) {
				return input != null && input.getType().isPresent() && input.getType().get() == type;
			}
		});
	}

	public void switchActiveParser() {
		ensureNotTerminated();
		assertOnOwnerTask();
		if (Equivalence.<ParserBase>identity().equivalent(activeParser, codeParser)) {
			activeParser = markupParser;
		}
		else {
			activeParser = codeParser;
		}
	}

	public void onError(@Nonnull final SourceLocation location, @Nonnull final String message) {
		ensureNotTerminated();
		assertOnOwnerTask();
		errors.add(new RazorError(message, location));
	}

	public void onError(@Nonnull final SourceLocation location, @Nonnull final String message, @Nonnull Object... args) {
		ensureNotTerminated();
		assertOnOwnerTask();
		onError(location, String.format(message, args));
	}

	public ParserResults completeParse() {
		if (blockStack.isEmpty()) {
			throw new UnsupportedOperationException(RazorResources().getString("parserContext.cannotCompleteTree.noRootBlock"));
		}
		if (blockStack.size() != 1) {
			throw new UnsupportedOperationException(RazorResources().getString("parserContext.cannotCompleteTree.outstandingBlocks"));
		}

		return new ParserResults(blockStack.pop().build(), errors);
	}

	void captureOwnerTask() {
		if (Debug.isAssertEnabled()) {
			// TODO
//			if (Task.CurrentId != null) {
//				ownerTaskId = Task.CurrentId
//			}
		}
	}

	void assertOnOwnerTask() {
		if (Debug.isAssertEnabled()) {
			// TODO
//			assert ownerTaskId == Task.CurrentId;
		}
	}

	void assertCurrent(final char expected) {
		if (Debug.isAssertEnabled()) {
			assert getCurrentCharacter() == expected;
		}
	}

	private void ensureNotTerminated() {
		if (terminated) {
			throw new UnsupportedOperationException(RazorResources().getString("parserContext.parseComplete"));
		}
	}


	private DebugParserContext debugParserContext = null;

	private boolean checkInfiniteLoop() {
		if (debugParserContext == null)
			debugParserContext = new DebugParserContext(this);

		return debugParserContext.checkInfiniteLoop();
	}

	private final class DebugParserContext {

		private static final int InfiniteLoopCountThreshold = 1000;
		private int infiniteLoopGuardCount = 0;
		private Optional<SourceLocation> infiniteLoopGuardLocation = Optional.absent();

		private final ParserContext parserContext;

		private DebugParserContext(final ParserContext parserContext) {
			this.parserContext = parserContext;
		}

		private String getUnparsed() {
			final String remaining = parserContext.source.readToEnd();
			parserContext.source.setPosition(parserContext.source.getPosition() - remaining.length());
			return remaining;
		}

		private boolean checkInfiniteLoop() {
			// Infinite loop guard
			//  Basically, if this property is accessed 1000 times in a row without having advanced the source reader to the next position, we
			//  cause a parser error
			if (infiniteLoopGuardLocation.isPresent()) {
				if (parserContext.source.getLocation().equals(infiniteLoopGuardLocation.get())) {
					infiniteLoopGuardCount++;
					if (infiniteLoopGuardCount > InfiniteLoopCountThreshold) {
						Debug.fail("An internal parser error is causing an infinite loop at this location.");
						terminated = true;
						return true;
					}
				}
				else {
					infiniteLoopGuardCount = 0;
				}
			}
			infiniteLoopGuardLocation = Optional.fromNullable(source.getLocation());
			return false;
		}
	}

}
