package nextmethod.web.razor.parser;

import com.google.common.collect.Iterables;
import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.web.razor.framework.ParserTestBase;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockBuilder;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanBuilder;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.SeekableTextReader;
import nextmethod.web.razor.text.TextReader;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import org.junit.Test;

import javax.annotation.Nullable;
import java.io.StringReader;

import static org.junit.Assert.*;

public class ParserContextTest {

	@SuppressWarnings("ConstantConditions")
	@Test(expected = NullPointerException.class)
	public void constructorRequiresNonNullSource() {
		final JavaCodeParser parser = new JavaCodeParser();
		new ParserContext(null, parser, new HtmlMarkupParser(), parser);
	}

	@SuppressWarnings("ConstantConditions")
	@Test(expected = NullPointerException.class)
	public void constructorRequiresNonNullCodeParser() {
		new ParserContext(
			new SeekableTextReader(TextReader.Null),
			null,
			new HtmlMarkupParser(),
			new JavaCodeParser()
		);
	}

	@SuppressWarnings("ConstantConditions")
	@Test(expected = NullPointerException.class)
	public void constructorRequiresNonNullMarkupParser() {
		final JavaCodeParser codeParser = new JavaCodeParser();
		new ParserContext(
			new SeekableTextReader(TextReader.Null),
			codeParser,
			null,
			codeParser
		);
	}

	@SuppressWarnings("ConstantConditions")
	@Test(expected = NullPointerException.class)
	public void constructorRequiresNonNullActiveParser() {
		new ParserContext(
			new SeekableTextReader(TextReader.Null),
			new JavaCodeParser(),
			new HtmlMarkupParser(),
			null
		);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorThrowsIfActiveParserIsNotCodeOrMarkupParser() {
		new ParserContext(
			new SeekableTextReader(TextReader.Null),
			new JavaCodeParser(),
			new HtmlMarkupParser(),
			new JavaCodeParser()
		);
	}

	@Test
	public void constructorAcceptsActiveParserIfIsSameAsEitherCodeOrMarkupParser() {
		final JavaCodeParser javaCodeParser = new JavaCodeParser();
		final HtmlMarkupParser htmlMarkupParser = new HtmlMarkupParser();
		new ParserContext(new SeekableTextReader(TextReader.Null), javaCodeParser, htmlMarkupParser, javaCodeParser);
		new ParserContext(new SeekableTextReader(TextReader.Null), javaCodeParser, htmlMarkupParser, htmlMarkupParser);
	}

	@Test
	public void constructorInitializesProperties() {
		final SeekableTextReader expectedBuffer = new SeekableTextReader(TextReader.Null);
		final JavaCodeParser expectedCodeParser = new JavaCodeParser();
		final HtmlMarkupParser expectedMarkupParser = new HtmlMarkupParser();

		final ParserContext context = new ParserContext(expectedBuffer, expectedCodeParser, expectedMarkupParser, expectedCodeParser);

		assertNotNull(context.getSource());
		assertSame(expectedCodeParser, context.getCodeParser());
		assertSame(expectedMarkupParser, context.getMarkupParser());
		assertSame(expectedCodeParser, context.getActiveParser());
	}

	@Test
	public void currentCharacterReturnsCurrentCharacterInTextBuffer() {
		final ParserContext context = setupTestContext("bar", new Delegates.IAction1<TextReader>() {
			@Override
			public void invoke(@Nullable final TextReader input) {
				assert input != null;
				input.read();
			}
		});

		final char actual = context.getCurrentCharacter();
		assertEquals('a', actual);
	}

	@Test
	public void currentCharacterReturnsNullCharacterIfTextBufferAtEOF() {
		final ParserContext context = setupTestContext("bar", new Delegates.IAction1<TextReader>() {
			@Override
			public void invoke(@Nullable final TextReader input) {
				assert input != null;
				input.readToEnd();
			}
		});

		final char actual = context.getCurrentCharacter();

		assertEquals('\0', actual);
	}

	@Test
	public void endOfFileReturnsFalseIfTextBufferNotAtEOF() {
		final ParserContext context = setupTestContext("bar");

		assertFalse(context.isEndOfFile());
	}

	@Test
	public void endOfFileReturnsTrueIfTextBufferAtEOF() {
		final ParserContext context = setupTestContext("bar", new Delegates.IAction1<TextReader>() {
			@Override
			public void invoke(@Nullable final TextReader input) {
				assert input != null;
				input.readToEnd();
			}
		});

		assertTrue(context.isEndOfFile());
	}

	@Test
	public void startBlockCreatesNewBlock() {
		final ParserContext context = setupTestContext("phoo");

		context.startBlock(BlockType.Expression);

		assertEquals(1, context.getBlockStack().size());
		assertEquals(BlockType.Expression, context.getBlockStack().peek().getType().get());
	}

	@Test
	public void endBlockAddsCurrentBlockToParentBlock() {
		final ParserContext context = setupTestContext("phoo");

		context.startBlock(BlockType.Expression);
		context.startBlock(BlockType.Statement);
		context.endBlock();

		assertEquals(1, context.getBlockStack().size());
		assertEquals(BlockType.Expression, context.getBlockStack().peek().getType().get());
		assertEquals(1, context.getBlockStack().peek().getChildren().size());
		final SyntaxTreeNode first = Iterables.getFirst(context.getBlockStack().peek().getChildren(), null);
		assertNotNull(first);
		assertEquals(BlockType.Statement, ((Block) first).getType());
	}

	@Test
	public void addSpanAddsSpanToCurrentBlockBuilder() {
		final ParserContext context = setupTestContext("phoo");

		final SpanBuilder builder = new SpanBuilder().setKind(SpanKind.Code);
		builder.accept(new JavaSymbol(1, 0, 1, "foo", JavaSymbolType.Identifier));
		final Span added = builder.build();

		try (final IDisposable ignored = context.startBlock(BlockType.Functions)) {
			context.addSpan(added);
		}

		final BlockBuilder expected = new BlockBuilder().setType(BlockType.Functions);
		expected.getChildren().add(added);

		ParserTestBase.evaluateResults(context.completeParse(), expected.build());
	}

	@Test
	public void switchActiveParserSetsMarkupParserAsActiveIfCodeParserCurrentlyActive() {
		final JavaCodeParser codeParser = new JavaCodeParser();
		final HtmlMarkupParser markupParser = new HtmlMarkupParser();
		final ParserContext context = setupTestContext("barbazbiz", new Delegates.IAction1<TextReader>() {
			@Override
			public void invoke(@Nullable final TextReader input) {
				assert input != null;
				input.read();
			}
		}, codeParser, markupParser, codeParser);

		assertSame(codeParser, context.getActiveParser());

		context.switchActiveParser();

		assertSame(markupParser, context.getActiveParser());
	}

	@Test
	public void switchActiveParserSetsCodeParserAsActiveIfMarkupParserCurrentlyActive() {
		final JavaCodeParser codeParser = new JavaCodeParser();
		final HtmlMarkupParser markupParser = new HtmlMarkupParser();
		final ParserContext context = setupTestContext("barbazbiz", new Delegates.IAction1<TextReader>() {
			@Override
			public void invoke(@Nullable final TextReader input) {
				assert input != null;
				input.read();
			}
		}, codeParser, markupParser, markupParser);

		assertSame(markupParser, context.getActiveParser());

		context.switchActiveParser();

		assertSame(codeParser, context.getActiveParser());
	}



	private ParserContext setupTestContext(final String document) {
		final Delegates.IAction1<TextReader> positioningAction = new Delegates.IAction1<TextReader>() {

			@Override
			public void invoke(@Nullable final TextReader input) {
			}
		};
		return setupTestContext(document, positioningAction);
	}

	private ParserContext setupTestContext(final String document, final Delegates.IAction1<TextReader> positioningAction) {
		final JavaCodeParser codeParser = new JavaCodeParser();
		return setupTestContext(document, positioningAction, codeParser, new HtmlMarkupParser(), codeParser);
	}

	private ParserContext setupTestContext(final String document, final Delegates.IAction1<TextReader> positioningAction, final ParserBase codeParser, final ParserBase markupParser, final ParserBase activeParser) {
		final ParserContext context = new ParserContext(new SeekableTextReader(new StringReader(document)), codeParser, markupParser, activeParser);
		positioningAction.invoke(context.getSource());
		return context;
	}
}
