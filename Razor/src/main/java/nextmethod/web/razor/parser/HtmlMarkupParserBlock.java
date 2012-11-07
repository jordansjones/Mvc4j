package nextmethod.web.razor.parser;

import com.google.common.base.Strings;
import com.google.common.collect.Queues;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.KeyValue;
import nextmethod.web.razor.editor.EditorHints;
import nextmethod.web.razor.editor.SingleLineMarkupEditHandler;
import nextmethod.web.razor.generator.AttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.DynamicAttributeBlockCodeGenerator;
import nextmethod.web.razor.generator.LiteralAttributeCodeGenerator;
import nextmethod.web.razor.generator.ResolveUrlCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbol;
import nextmethod.web.razor.tokenizer.symbols.HtmlSymbolType;
import nextmethod.web.razor.tokenizer.symbols.SymbolExtensions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Deque;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

/**
 *
 */
final class HtmlMarkupParserBlock extends HtmlMarkupParserDelegate {

	private SourceLocation lastTagStart = SourceLocation.Zero;
	private HtmlSymbol bufferedOpenAngle;

	public HtmlMarkupParserBlock(@Nonnull final HtmlMarkupParser parser) {
		super(parser);
	}

	protected void parseBlock() {
		if (getContext() == null) {
			throw new UnsupportedOperationException(RazorResources().parserContextNotSet());
		}

		try (IDisposable d = pushSpanConfig(defaultMarkupSpanDelegate)) {
			try (IDisposable d2 = getContext().startBlock(BlockType.Markup)) {
				if (!nextToken()) {
					return;
				}

				acceptWhile(isSpacingToken(true));

				if (isCurrentSymbol(HtmlSymbolType.OpenAngle)) {
					// "<" => Implicit Tag Block
					tagBlock(Queues.<KeyValue<HtmlSymbol, SourceLocation>>newArrayDeque());
				}
				else if (isCurrentSymbol(HtmlSymbolType.Transition)) {
					// "@" => Explicit Tag/Single Line Block OR Template
					output(SpanKind.Markup);

					// Definitely have a transition span
					doAssert(HtmlSymbolType.Transition);
					acceptAndMoveNext();
					getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
					getSpan().setCodeGenerator(SpanCodeGenerator.Null);
					output(SpanKind.Transition);
					if (at(HtmlSymbolType.Transition)) {
						getSpan().setCodeGenerator(SpanCodeGenerator.Null);
						acceptAndMoveNext();
						output(SpanKind.MetaCode);
					}
					afterTransition();
				}
				else {
					getContext().onError(
						getCurrentSymbol().getStart(),
						RazorResources().parseErrorMarkupBlockMustStartWithTag()
					);
				}
				output(SpanKind.Markup);
			}
		}
	}

	protected void afterTransition() {
		// "@:" => Explicit Single Line Block
		if (isCurrentSymbol(HtmlSymbolType.Text) && getCurrentSymbol().getContent().length() > 0 && getCurrentSymbol().getContent().charAt(0) == ':') {
			// Split the token
			final KeyValue<HtmlSymbol, HtmlSymbol> split = getLanguage().splitSymbol(getCurrentSymbol(), 1, HtmlSymbolType.Colon);

			// The first part (key) is added to this span and we return a MetaCode span
			accept(split.getKey());
			getSpan().setCodeGenerator(SpanCodeGenerator.Null);
			output(SpanKind.MetaCode);
			if (split.getValue() != null) {
				accept(split.getValue());
			}
			nextToken();
			singleLineMarkup();
		}
		else if (isCurrentSymbol(HtmlSymbolType.OpenAngle)) {
			tagBlock(Queues.<KeyValue<HtmlSymbol, SourceLocation>>newArrayDeque());
		}
	}

	protected void singleLineMarkup() {
		// Parse until a newline, it's that simple!
		// First, signal to code parser that whitespace is significant to us.
		final boolean old = getContext().isWhiteSpaceIsSignificantToAncestorBlock();
		getContext().setWhiteSpaceIsSignificantToAncestorBlock(true);
		getSpan().setEditHandler(new SingleLineMarkupEditHandler(getLanguage().createTokenizeStringDelegate()));
		skipToAndParseCode(HtmlSymbolType.NewLine);
		if (!isEndOfFile() && isCurrentSymbol(HtmlSymbolType.NewLine)) {
			acceptAndMoveNext();
			getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
		}
		putCurrentBack();
		getContext().setWhiteSpaceIsSignificantToAncestorBlock(old);
		output(SpanKind.Markup);
	}

	protected void tagBlock(@Nonnull final Deque<KeyValue<HtmlSymbol, SourceLocation>> tags) {
		// SKip Whitespace and Text
		boolean complete = false;
		do {
			skipToAndParseCode(HtmlSymbolType.OpenAngle);
			if (isEndOfFile()) {
				endTagBlock(tags, true);
			}
			else {
				bufferedOpenAngle = null;
				lastTagStart = getCurrentLocation();
				doAssert(HtmlSymbolType.OpenAngle);
				bufferedOpenAngle = getCurrentSymbol();
				final SourceLocation tagStart = getCurrentLocation();
				if (!nextToken()) {
					accept(bufferedOpenAngle);
					endTagBlock(tags, false);
				}
				else {
					complete = afterTagStart(tagStart, tags);
				}
			}
		}
		while(tags.size() > 0);

		endTagBlock(tags, complete);
	}

	protected boolean afterTagStart (@Nonnull final SourceLocation tagStart, @Nonnull final Deque<KeyValue<HtmlSymbol, SourceLocation>> tags) {
		if (!isEndOfFile()) {
			switch (getCurrentSymbol().getType()) {
				case Solidus:
					// End Tag
					return endTag(tagStart, tags);
				case Bang:
					// Comment
					accept(bufferedOpenAngle);
					return bangTag();
				case QuestionMark:
					// XML PI
					accept(bufferedOpenAngle);
					return xmlPI();
				default:
					// Start Tag
					return startTag(tags);
			}
		}
		if (tags.isEmpty()) {
			getContext().onError(
				getCurrentLocation(),
				RazorResources().parseErrorOuterTagMissingName()
			);
		}
		return false;
	}

	protected boolean xmlPI() {
		// Accept "?"
		doAssert(HtmlSymbolType.QuestionMark);
		acceptAndMoveNext();
		return acceptUntilAll(HtmlSymbolType.QuestionMark, HtmlSymbolType.CloseAngle);
	}

	protected boolean bangTag() {
		// Accept "!"
		doAssert(HtmlSymbolType.Bang);
		acceptAndMoveNext();
		if (isCurrentSymbol(HtmlSymbolType.DoubleHyphen)) {
			acceptAndMoveNext();
			return acceptUntilAll(HtmlSymbolType.DoubleHyphen, HtmlSymbolType.CloseAngle);
		}
		else if (isCurrentSymbol(HtmlSymbolType.LeftBracket)) {
			acceptAndMoveNext();
			return cdata();
		}
		acceptAndMoveNext();
		return acceptUntilAll(HtmlSymbolType.CloseAngle);
	}

	protected boolean cdata() {
		if (isCurrentSymbol(HtmlSymbolType.Text) && "cdata".equalsIgnoreCase(getCurrentSymbol().getContent())) {
			acceptAndMoveNext();
			if (isCurrentSymbol(HtmlSymbolType.LeftBracket)) {
				return acceptUntilAll(HtmlSymbolType.RightBracket, HtmlSymbolType.RightBracket, HtmlSymbolType.CloseAngle);
			}
		}
		return false;
	}

	protected boolean endTag(@Nonnull final SourceLocation tagStart, @Nonnull final Deque<KeyValue<HtmlSymbol, SourceLocation>> tags) {
		// Accept "/" and move next
		doAssert(HtmlSymbolType.Solidus);
		final HtmlSymbol solidus = getCurrentSymbol();
		if (!nextToken()) {
			accept(bufferedOpenAngle);
			accept(solidus);
			return false;
		}
		String tagName = "";
		if (at(HtmlSymbolType.Text)) {
			tagName = getCurrentSymbol().getContent();
		}
		final boolean matched = removeTag(tags, tagName, tagStart);

		if (tags.isEmpty() && SyntaxConstants.TextTagName.equalsIgnoreCase(tagName) && matched) {
			output(SpanKind.Markup);
			return endTextTag(solidus);
		}

		accept(bufferedOpenAngle);
		accept(solidus);

		acceptUntil(HtmlSymbolType.CloseAngle);

		// Accept the ">"
		return optional(HtmlSymbolType.CloseAngle);
	}

	protected boolean endTextTag(@Nonnull final HtmlSymbol solidus) {
		final SourceLocation start = bufferedOpenAngle.getStart();

		accept(bufferedOpenAngle);
		accept(solidus);

		doAssert(HtmlSymbolType.Text);
		acceptAndMoveNext();

		final boolean seenCloseAngle = optional(HtmlSymbolType.CloseAngle);

		if (!seenCloseAngle) {
			getContext().onError(
				start,
				RazorResources().parseErrorTextTagCannotContainAttributes()
			);
		}
		else {
			getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
		}

		getSpan().setCodeGenerator(SpanCodeGenerator.Null);
		output(SpanKind.Transition);
		return seenCloseAngle;
	}

	protected boolean isTagRecoveryStopPoint(@Nonnull final HtmlSymbol sym) {
		return sym.isTypeOr(
			HtmlSymbolType.CloseAngle,
			HtmlSymbolType.Solidus,
			HtmlSymbolType.OpenAngle,
			HtmlSymbolType.SingleQuote,
			HtmlSymbolType.DoubleQuote
		);
	}
	protected final Delegates.IFunc1<HtmlSymbol, Boolean> isTagRecoveryStopPointDelegate = new Delegates.IFunc1<HtmlSymbol, Boolean>() {
		@Override
		public Boolean invoke(@Nullable final HtmlSymbol input1) {
			return input1 != null && isTagRecoveryStopPoint(input1);
		}
	};

	protected void tagContent() {
		if (!at(HtmlSymbolType.WhiteSpace)) {
			// We should be right after the tag name, so if there's no whitespace, something is wrong
			recoverToEndOfTag();
		}
		else {
			// We are here ($): <tag$ foo="bar" biz="~/Baz" />
			while (!isEndOfFile() && !isEndOfTag()) {
				beforeAttribute();
			}
		}
	}

	protected boolean isEndOfTag() {
		if (at(HtmlSymbolType.Solidus)) {
			if (nextIs(HtmlSymbolType.CloseAngle)) {
				return true;
			}
			acceptAndMoveNext();
		}
		return at(HtmlSymbolType.CloseAngle) || at(HtmlSymbolType.OpenAngle);
	}

	protected void beforeAttribute() {
		// http://dev.w3.org/html5/spec/tokenization.html#before-attribute-name-state
		// Capture whitespace
		final Iterable<HtmlSymbol> whitespace = readWhile(new Delegates.IFunc1<HtmlSymbol, Boolean>() {
			@Override
			public Boolean invoke(@Nullable final HtmlSymbol symbol) {
				return symbol != null && symbol.isTypeOr(HtmlSymbolType.WhiteSpace, HtmlSymbolType.NewLine);
			}
		});

		if (at(HtmlSymbolType.Transition)) {
			// Transition outside of attribute value => switch to recovery mode
			accept(whitespace);
			recoverToEndOfTag();
			return;
		}

		// http://dev.w3.org/html5/spec/tokenization.html#attribute-name-state
		// Read the 'name' (i.e. read until the '=' or whitespace/newline)
		Iterable<HtmlSymbol> name = Collections.emptyList();
		if (at(HtmlSymbolType.Text)) {
			name = readWhile(new Delegates.IFunc1<HtmlSymbol, Boolean>() {
				@Override
				public Boolean invoke(@Nullable final HtmlSymbol symbol) {
					return symbol != null
						&& !symbol.isType(HtmlSymbolType.WhiteSpace)
						&& !symbol.isType(HtmlSymbolType.NewLine)
						&& !symbol.isType(HtmlSymbolType.Equals)
						&& !symbol.isType(HtmlSymbolType.CloseAngle)
						&& !symbol.isType(HtmlSymbolType.OpenAngle)
						&& (!symbol.isType(HtmlSymbolType.Solidus) || !nextIs(HtmlSymbolType.CloseAngle))
						;
				}
			});
		}
		else {
			// Unexpected character in tag, enter recovery
			accept(whitespace);
			recoverToEndOfTag();
			return;
		}

		if (!at(HtmlSymbolType.Equals)) {
			// Saw a space or newline after the name, so just skip this attribute and continue around the loop
			accept(whitespace);
			accept(name);
			return;
		}

		output(SpanKind.Markup);

		// Start a new markup block for the attribute
		try (IDisposable d = getContext().startBlock(BlockType.Markup)) {
			attributePrefix(whitespace, name);
		}
	}

	protected void attributePrefix(@Nonnull final Iterable<HtmlSymbol> whitespace, @Nonnull final Iterable<HtmlSymbol> nameSymbols) {
		// First, determine if this is a 'data-' attribute (since those can't use conditional attributes)
		final LocationTagged<String> name = SymbolExtensions.getContent(nameSymbols, getSpan().getStart());
		final boolean attributeCanBeConditional = !Strings.nullToEmpty(name.getValue()).toLowerCase().startsWith("data-");

		// Accept the whitespace and name
		accept(whitespace);
		accept(nameSymbols);
		doAssert(HtmlSymbolType.Equals); // We should be at "="
		acceptAndMoveNext();
		HtmlSymbolType quote = HtmlSymbolType.Unknown;
		if (at(HtmlSymbolType.SingleQuote) || at(HtmlSymbolType.DoubleQuote)) {
			quote = getCurrentSymbol().getType();
			acceptAndMoveNext();
		}

		// New now have the prefix: (i.e. '      foo="')
		final LocationTagged<String> prefix = SymbolExtensions.getContent(getSpan());

		if (attributeCanBeConditional) {
			getSpan().setCodeGenerator(SpanCodeGenerator.Null); // The block code generator will render the prefix
			output(SpanKind.Markup);

			// Read the values
			while (!isEndOfFile() && !isEndOfAttributeValue(quote, getCurrentSymbol())) {
				attributeValue(quote);
			}

			// Capture the suffix
			LocationTagged<String> suffix = new LocationTagged<String>("", getCurrentLocation());
			if (quote != HtmlSymbolType.Unknown && at(quote)) {
				suffix = SymbolExtensions.getContent(getCurrentSymbol());
				acceptAndMoveNext();
			}

			if (!getSpan().getSymbols().isEmpty()) {
				getSpan().setCodeGenerator(SpanCodeGenerator.Null); // Again, block code generator will render the suffix
				output(SpanKind.Markup);
			}

			// Create the block code generator
			getContext().getCurrentBlock().setCodeGenerator(new AttributeBlockCodeGenerator(name.getValue(), prefix, suffix));
		}
		else {
			final HtmlSymbolType fQuote = quote; // This allows use to use "quote" in the following IFunc1 "delegate"
			// Not a "conditional" attribute, so just read the value
			skipToAndParseCode(new Delegates.IFunc1<HtmlSymbol, Boolean>() {
				@Override
				public Boolean invoke(@Nullable final HtmlSymbol input1) {
					return input1 != null && isEndOfAttributeValue(fQuote, input1);
				}
			});
			if (quote != HtmlSymbolType.Unknown) {
				optional(quote);
			}
			output(SpanKind.Markup);
		}
	}

	protected void attributeValue(@Nonnull final HtmlSymbolType quote) {
		final SourceLocation prefixStart = getCurrentLocation();
		final Iterable<HtmlSymbol> prefix = readWhile(new Delegates.IFunc1<HtmlSymbol, Boolean>() {
			@Override
			public Boolean invoke(@Nullable final HtmlSymbol symbol) {
				return symbol != null && (symbol.isTypeOr(HtmlSymbolType.WhiteSpace, HtmlSymbolType.NewLine));
			}
		});
		accept(prefix);

		if (at(HtmlSymbolType.Transition)) {
			final SourceLocation valueStart = getCurrentLocation();
			putCurrentBack();

			// Output the prefix but as a null-span. DynamicAttributeBlockCodeGenerator will render it
			getSpan().setCodeGenerator(SpanCodeGenerator.Null);

			// Dynamic value, start a new block and set the code generator
			try(IDisposable d = getContext().startBlock(BlockType.Markup)) {
				getContext().getCurrentBlock().setCodeGenerator(new DynamicAttributeBlockCodeGenerator(SymbolExtensions.getContent(prefix, prefixStart), valueStart));

				otherParserBlock();
			}
		}
		else if (at(HtmlSymbolType.Text) && getCurrentSymbol().getContent().length() > 0 && getCurrentSymbol().getContent().charAt(0) == '~' && nextIs(HtmlSymbolType.Solidus)) {
			// Virtual Path value
			final SourceLocation valueStart = getCurrentLocation();
			virtualPath();
			getSpan().setCodeGenerator(LiteralAttributeCodeGenerator.fromValueGenerator(
				SymbolExtensions.getContent(prefix, prefixStart),
				new LocationTagged<SpanCodeGenerator>(new ResolveUrlCodeGenerator(), valueStart)
			));
		}
		else {
			// Literal value
			// 'quote' should be "Unknown" if not quoted and symbols coming from the tokenizer should never have "Unknown" type.
			final Iterable<HtmlSymbol> value = readWhile(new Delegates.IFunc1<HtmlSymbol, Boolean>() {
				@Override
				public Boolean invoke(@Nullable final HtmlSymbol symbol) {
					return symbol != null && (
						symbol.getType() != HtmlSymbolType.WhiteSpace
							&& symbol.getType() != HtmlSymbolType.NewLine
							&& symbol.getType() != HtmlSymbolType.Transition
							// This condition checks for the end of the attribute value (it repeats some of the checks above but for now, that's ok)
							&& !isEndOfAttributeValue(quote, symbol)
					);
				}
			});

			accept(value);
			getSpan().setCodeGenerator(LiteralAttributeCodeGenerator.fromValue(
				SymbolExtensions.getContent(prefix, prefixStart),
				SymbolExtensions.getContent(value, prefixStart)
			));
		}
		output(SpanKind.Markup);
	}

	protected boolean isEndOfAttributeValue(@Nonnull final HtmlSymbolType quote, @Nullable final HtmlSymbol sym) {
		return isEndOfFile()
			|| sym == null
			|| (
				quote != HtmlSymbolType.Unknown
				? sym.getType() == quote // if quoted, just waite for the quote
				: isUnquotedEndOfAttributeValue(sym)
			);
	}

	protected boolean isUnquotedEndOfAttributeValue(@Nonnull final HtmlSymbol sym) {
		// If unquoted, we have a larger set of terminating characters:
		// http://dev.w3.org/html5/spec/tokenization.html#attribute-value-unquoted-state
		// Also we need to detect "/" and ">"
		return sym.isTypeOr(
			HtmlSymbolType.DoubleQuote,
			HtmlSymbolType.SingleQuote,
			HtmlSymbolType.OpenAngle,
			HtmlSymbolType.Equals
			)
			|| (sym.isType(HtmlSymbolType.Solidus) && nextIs(HtmlSymbolType.CloseAngle))
			|| sym.isTypeOr(
			HtmlSymbolType.CloseAngle,
			HtmlSymbolType.WhiteSpace,
			HtmlSymbolType.NewLine
		);
	}

	protected void virtualPath() {
		doAssert(HtmlSymbolType.Text);
		if (Debug.isAssertEnabled())
			assert getCurrentSymbol().getContent().length() > 0 && getCurrentSymbol().getContent().charAt(0) == '~';

		// Parse until a transition symbol, whitespace, newline, or quote. We support only a fairly minimal subset of Virtual Paths
		acceptUntil(HtmlSymbolType.Transition, HtmlSymbolType.WhiteSpace, HtmlSymbolType.NewLine, HtmlSymbolType.SingleQuote, HtmlSymbolType.DoubleQuote);

		// Output a Virtual Path span
		getSpan().getEditHandler().setEditorHints(EditorHints.VirtualPath);
	}

	protected void recoverToEndOfTag() {
		// Accept until ">", "/", or "<", but parse code
		while (!isEndOfFile()) {
			skipToAndParseCode(isTagRecoveryStopPointDelegate);
			if (!isEndOfFile()) {
				ensureCurrent();
				switch (getCurrentSymbol().getType()) {
					case SingleQuote:
					case DoubleQuote:
						parseQuoted();
						break;
					case OpenAngle:
						// Another "<" means this tag is invalid.
					case Solidus:
						// Empty Tag
					case CloseAngle:
						// End of tag
						return;
					default:
						acceptAndMoveNext();
						break;
				}
			}
		}
	}

	protected void parseQuoted() {
		final HtmlSymbolType type = getCurrentSymbol().getType();
		acceptAndMoveNext();
		parseQuoted(type);
	}

	protected void parseQuoted(@Nonnull final HtmlSymbolType type) {
		skipToAndParseCode(type);
		if (!isEndOfFile()) {
			doAssert(type);
			acceptAndMoveNext();
		}
	}

	protected boolean startTag(@Nonnull final Deque<KeyValue<HtmlSymbol, SourceLocation>> tags) {
		// If we're at text, it's the name, otherwise the name is ""
		HtmlSymbol tagName;
		if (at(HtmlSymbolType.Text)) {
			tagName = getCurrentSymbol();
		}
		else {
			tagName = new HtmlSymbol(getCurrentLocation(), "", HtmlSymbolType.Unknown);
		}

		final KeyValue<HtmlSymbol, SourceLocation> tag = KeyValue.of(tagName, lastTagStart);

		if (tags.isEmpty() && SyntaxConstants.TextTagName.equalsIgnoreCase(tag.getKey().getContent())) {
			output(SpanKind.Markup);
			getSpan().setCodeGenerator(SpanCodeGenerator.Null);

			accept(bufferedOpenAngle);
			doAssert(HtmlSymbolType.Text);

			acceptAndMoveNext();

			int bookmark = getCurrentLocation().getAbsoluteIndex();
			Iterable<HtmlSymbol> tokens = readWhile(isSpacingToken(true));
			final boolean empty = at(HtmlSymbolType.Solidus);
			if (empty) {
				accept(tokens);
				doAssert(HtmlSymbolType.Solidus);
				acceptAndMoveNext();
				bookmark = getCurrentLocation().getAbsoluteIndex();
				tokens = readWhile(isSpacingToken(true));
			}

			if (!optional(HtmlSymbolType.CloseAngle)) {
				getContext().getSource().setPosition(bookmark);
				nextToken();
				getContext().onError(tag.getValue(), RazorResources().parseErrorTextTagCannotContainAttributes());
			}
			else {
				accept(tokens);
				getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
			}

			if (!empty) {
				tags.push(tag);
			}
			output(SpanKind.Transition);
			return true;
		}
		accept(bufferedOpenAngle);
		optional(HtmlSymbolType.Text);
		return restOfTag(tag, tags);
	}

	protected boolean restOfTag(@Nonnull final KeyValue<HtmlSymbol, SourceLocation> tag, @Nonnull final Deque<KeyValue<HtmlSymbol, SourceLocation>> tags) {
		tagContent();

		// We are now at a possible end of the tag
		// Found '<', so we just abort this tag.
		if (at(HtmlSymbolType.OpenAngle)) {
			return false;
		}

		final boolean isEmpty = at(HtmlSymbolType.Solidus);
		// Found a solidus, so don't accept it but DON'T pus the tag to the stack
		if (isEmpty) {
			acceptAndMoveNext();
		}

		// Check for the '>' to determine if the tag is finished
		final boolean seenClose = optional(HtmlSymbolType.CloseAngle);
		if (!seenClose) {
			getContext().onError(tag.getValue(), RazorResources().parseErrorUnfinishedTag(tag.getKey().getContent()));
		}
		else {
			if (!isEmpty) {
				// Is this a void element?
				final String tagName = tag.getKey().getContent().trim();
				if (delegate.voidElements.contains(tagName)) {
					// Technically, void elements like "meta" are not allowed to have end tags. Just in case they do,
					// we need to look ahead at the next set of tokens. If we see "<", "/", tag name, accept it and the ">" following it
					// Place a bookmark
					int bookmark = getCurrentLocation().getAbsoluteIndex();

					// Skip whitespace
					final Iterable<HtmlSymbol> ws = readWhile(isSpacingToken(true));

					// Open Angle
					if (at(HtmlSymbolType.OpenAngle) && nextIs(HtmlSymbolType.Solidus)) {
						final HtmlSymbol openAngle = getCurrentSymbol();
						nextToken();
						doAssert(HtmlSymbolType.Solidus);
						final HtmlSymbol solidus = getCurrentSymbol();
						nextToken();
						if (at(HtmlSymbolType.Text) && tagName.equalsIgnoreCase(getCurrentSymbol().getContent())) {
							// Accept up to here
							accept(ws);
							accept(openAngle);
							accept(solidus);
							acceptAndMoveNext();

							// Accept to '>', '<', or EOF
							acceptUntil(HtmlSymbolType.CloseAngle, HtmlSymbolType.OpenAngle);
							// Accept the '>' if we saw it. And if we do see it, we're complete
							return optional(HtmlSymbolType.CloseAngle);
						}
					}

					// Go back to the bookmark and just finish this tag at the close angle
					getContext().getSource().setPosition(bookmark);
					nextToken();
				}
				else if ("script".equalsIgnoreCase(tagName)) {
					skipToEndScriptAndParseCode();
				}
				else {
					// Push the tag on to the stack
					tags.push(tag);
				}
			}
		}
		return seenClose;
	}

	protected void skipToEndScriptAndParseCode() {
		// Special case for <script>: Skip to end of script tag and parse code
		boolean seenEndScript = false;
		while (!seenEndScript && !isEndOfFile()) {
			skipToAndParseCode(HtmlSymbolType.OpenAngle);
			final SourceLocation tagStart = getCurrentLocation();
			acceptAndMoveNext();
			acceptWhile(HtmlSymbolType.WhiteSpace);
			if (optional(HtmlSymbolType.Solidus)) {
				acceptWhile(HtmlSymbolType.WhiteSpace);
				if (at(HtmlSymbolType.Text) && "script".equals(getCurrentSymbol().getContent())) {
					// </script!
					skipToAndParseCode(HtmlSymbolType.CloseAngle);
					if (!optional(HtmlSymbolType.CloseAngle)) {
						getContext().onError(tagStart, RazorResources().parseErrorUnfinishedTag("script"));
					}
					seenEndScript = true;
				}
			}
		}
	}

	protected boolean acceptUntilAll(@Nonnull final HtmlSymbolType... endSequence) {
		while (!isEndOfFile()) {
			skipToAndParseCode(endSequence[0]);
			if (acceptAll(endSequence)) {
				return true;
			}
		}
		if (Debug.isAssertEnabled())
			assert isEndOfFile();
		getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
		return false;
	}

	@SuppressWarnings("ConstantConditions")
	protected boolean removeTag(@Nonnull final Deque<KeyValue<HtmlSymbol, SourceLocation>> tags, @Nonnull final String tagName, @Nonnull final SourceLocation tagStart) {
		KeyValue<HtmlSymbol, SourceLocation> currentTag = null;
		while (tags.size() > 0) {
			currentTag = tags.pop();
			if (tagName.equalsIgnoreCase(currentTag.getKey().getContent())) {
				// Matched the tag
				return true;
			}
		}

		if (currentTag != null) {
			getContext().onError(currentTag.getValue(), RazorResources().parseErrorMissingEndTag(currentTag.getKey().getContent()));
		}
		else {
			getContext().onError(tagStart, RazorResources().parseErrorUnexpectedEndTag(tagName));
		}
		return false;
	}

	@SuppressWarnings("ConstantConditions")
	protected void endTagBlock(@Nonnull final Deque<KeyValue<HtmlSymbol, SourceLocation>> tags, final boolean complete) {
		if (tags.size() > 0) {
			// Ended because of EOF, not matching close tag. Throw error for last tag
			while(tags.size() > 1) {
				tags.pop();
			}
			final KeyValue<HtmlSymbol, SourceLocation> tag = tags.pop();
			getContext().onError(tag.getValue(), RazorResources().parseErrorMissingEndTag(tag.getKey().getContent()));
		}
		else if (complete) {
			getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.SetOfNone);
		}

		tags.clear();

		if (!getContext().isDesignTimeMode()) {
			acceptWhile(HtmlSymbolType.WhiteSpace);
			if (!isEndOfFile() && getCurrentSymbol().getType() == HtmlSymbolType.NewLine) {
				acceptAndMoveNext();
			}
		}
		else if (AcceptedCharacters.Any.equals(getSpan().getEditHandler().getAcceptedCharacters())) {
			acceptWhile(HtmlSymbolType.WhiteSpace);
			optional(HtmlSymbolType.NewLine);
		}
		putCurrentBack();

		if (!complete) {
			addMarkerSymbolIfNecessary();
		}
		output(SpanKind.Markup);
	}

}
