/*
 * Copyright 2014 Jordan S. Jones <jordansjones@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nextmethod.web.razor.parser;

import java.text.MessageFormat;
import java.util.Deque;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

import com.google.common.base.Equivalence;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import nextmethod.base.Debug;
import nextmethod.base.IDisposable;
import nextmethod.threading.Task;
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

import static com.google.common.base.Preconditions.checkArgument;
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

    public ParserContext(@Nonnull final ITextDocument source, @Nonnull final ParserBase codeParser,
                         @Nonnull final ParserBase markupParser, @Nonnull final ParserBase activeParser
                        ) {
        checkNotNull(source, "source");
        checkNotNull(codeParser, "codeParser");
        checkNotNull(markupParser, "markupParser");
        checkNotNull(activeParser, "activeParser");
        checkArgument(
                         (activeParser == codeParser) || (activeParser == markupParser),
                         RazorResources().activeParserMustBeCodeOrMarkupParser()
                     );

        captureOwnerTask();

        this.source = new TextDocumentReader(source);
        this.codeParser = codeParser;
        this.markupParser = markupParser;
        this.activeParser = activeParser;
        this.errors = Lists.newArrayList();
    }

    public ParserBase getActiveParser() {
        return activeParser;
    }

    public ParserBase getMarkupParser() {
        return this.markupParser;
    }

    public ParserBase getCodeParser() {
        return codeParser;
    }

    public List<RazorError> getErrors() {
        return this.errors;
    }

    public TextDocumentReader getSource() {
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

    public void setWhiteSpaceIsSignificantToAncestorBlock(final boolean whiteSpaceIsSignificantToAncestorBlock) {
        this.whiteSpaceIsSignificantToAncestorBlock = whiteSpaceIsSignificantToAncestorBlock;
    }

    public boolean isDesignTimeMode() {
        return designTimeMode;
    }

    public void setDesignTimeMode(final boolean designTimeMode) {
        this.designTimeMode = designTimeMode;
    }

    public EnumSet<AcceptedCharacters> getLastAcceptedCharacters() {
        return lastSpan == null
               ? AcceptedCharacters.SetOfNone
               : lastSpan.getEditHandler().getAcceptedCharacters();
    }

    public Deque<BlockBuilder> getBlockStack() {
        return blockStack;
    }

    public char getCurrentCharacter() {
        if (terminated) return '\0';
        if (isAssertEnabled() && checkInfiniteLoop()) return '\0';
        final int ch = source.peek();
        return ch == -1
               ? '\0'
               : (char) ch;
    }

    public boolean isEndOfFile() {
        return terminated || source.peek() == -1;
    }

    public void addSpan(@Nonnull final Span span) {
        ensureNotTerminated();
        if (blockStack.size() == 0) {
            throw new UnsupportedOperationException(RazorResources().parserContextNoCurrentBlock());
        }
        blockStack.peek().getChildren().add(span);
        lastSpan = span;
    }

    /**
     * Starts a block of the specified type
     *
     * @param blockType The type of the block to start
     */
    public IDisposable startBlock(@Nonnull final BlockType blockType) {
        ensureNotTerminated();
        assertOnOwnerTask();
        final BlockBuilder blockBuilder = new BlockBuilder();
        blockBuilder.setType(blockType);
        blockStack.push(blockBuilder);
        return new DisposableAction(this::endBlock);
    }

    /**
     * Starts a new block
     *
     * @return
     */
    public IDisposable startBlock() {
        ensureNotTerminated();
        assertOnOwnerTask();
        blockStack.push(new BlockBuilder());
        return new DisposableAction(this::endBlock);
    }

    /**
     * Ends the current block
     */
    public void endBlock() {
        ensureNotTerminated();
        assertOnOwnerTask();
        if (blockStack.isEmpty()) {
            throw new UnsupportedOperationException(RazorResources().endBlockCalledWithoutMatchingStartBlock());
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
        return blockStack.stream().anyMatch(input -> input != null && input.getType().isPresent() && input.getType().get() == type);
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

    public void onError(@Nonnull final SourceLocation location, @Nonnull final String message, @Nonnull Object... args
                       ) {
        ensureNotTerminated();
        assertOnOwnerTask();
        onError(location, MessageFormat.format(message, args));
    }

    public ParserResults completeParse() {
        if (blockStack.isEmpty()) {
            throw new UnsupportedOperationException(RazorResources().parserContextCannotCompleteTreeNoRootBlock());
        }
        if (blockStack.size() != 1) {
            throw new UnsupportedOperationException(RazorResources().parserContextCannotCompleteTreeOutstandingBlocks());
        }

        return new ParserResults(blockStack.pop().build(), errors);
    }

    void captureOwnerTask() {
        if (Debug.isAssertEnabled()) {
            if (Task.getCurrentId().isPresent()) {
                ownerTaskId = Task.getCurrentId();
            }
        }
    }

    void assertOnOwnerTask() {
        if (Debug.isAssertEnabled() && ownerTaskId != null) {
            assert Objects.equals(ownerTaskId, Task.getCurrentId());
        }
    }

    void assertCurrent(final char expected) {
        if (Debug.isAssertEnabled()) {
            assert getCurrentCharacter() == expected;
        }
    }

    private void ensureNotTerminated() {
        if (terminated) {
            throw new UnsupportedOperationException(RazorResources().parserContextParseComplete());
        }
    }


    private DebugParserContext debugParserContext = null;

    private boolean checkInfiniteLoop() {
        if (debugParserContext == null) {
            debugParserContext = new DebugParserContext(this);
        }

        return debugParserContext.checkInfiniteLoop();
    }

    private final class DebugParserContext {

        private static final int InfiniteLoopCountThreshold = 1000;
        private int infiniteLoopGuardCount = 0;
        private Optional<SourceLocation> infiniteLoopGuardLocation = Optional.empty();

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
            infiniteLoopGuardLocation = Optional.ofNullable(source.getLocation());
            return false;
        }
    }

}
