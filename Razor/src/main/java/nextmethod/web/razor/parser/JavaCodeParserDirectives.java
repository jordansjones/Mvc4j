package nextmethod.web.razor.parser;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Iterables;
import nextmethod.base.Debug;
import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.NotImplementedException;
import nextmethod.web.razor.editor.AutoCompleteEditHandler;
import nextmethod.web.razor.editor.EditorHints;
import nextmethod.web.razor.generator.*;
import nextmethod.web.razor.parser.syntaxtree.AcceptedCharacters;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.BlockType;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.text.LocationTagged;
import nextmethod.web.razor.text.SourceLocation;
import nextmethod.web.razor.tokenizer.symbols.ISymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbol;
import nextmethod.web.razor.tokenizer.symbols.JavaSymbolType;
import nextmethod.web.razor.tokenizer.symbols.SymbolExtensions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.EnumSet;
import java.util.Objects;

import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

// TODO
final class JavaCodeParserDirectives extends JavaCodeParserDelegate {

	JavaCodeParserDirectives(final JavaCodeParser parser) {
		super(parser);
		setupDirectives();
	}

	private void setupDirectives() {
		mapDirectives(inheritsDirectiveDelegate, SyntaxConstants.Java.InheritsKeyword);
		mapDirectives(functionsDirectiveDelegate, SyntaxConstants.Java.FunctionsKeyword);
		mapDirectives(sectionDirectiveDelegate, SyntaxConstants.Java.SectionKeyword);
		mapDirectives(helperDirectiveDelegate, SyntaxConstants.Java.HelperKeyword);
		mapDirectives(layoutDirectiveDelegate, SyntaxConstants.Java.LayoutKeyword);
		mapDirectives(sessionStateDirectiveDelegate, SyntaxConstants.Java.SessionStateKeyword);
	}

	protected void mapDirectives(final Delegates.IAction handler, final String... directives) {
		for (String directive : directives) {
			delegate.directiveParsers.put(directive, handler);
			delegate.keywords.add(directive);
		}
	}

	protected void layoutDirective() {
		assertDirective(SyntaxConstants.Java.LayoutKeyword);
		acceptAndMoveNext();
		getContext().getCurrentBlock().setType(BlockType.Directive);

		// Accept spaces, but not newlines
		final boolean foundSomeWhitespace = at(JavaSymbolType.WhiteSpace);
		acceptWhile(JavaSymbolType.WhiteSpace);
		output(SpanKind.MetaCode, foundSomeWhitespace ? AcceptedCharacters.SetOfNone : AcceptedCharacters.Any);

		// First non-whitespace character starts the Layout Page, then newline ends it
		acceptUntil(JavaSymbolType.NewLine);
		getSpan().setCodeGenerator(new SetLayoutCodeGenerator(SymbolExtensions.getContent(getSpan()).toString()));
		getSpan().getEditHandler().setEditorHints(EditorHints.LayoutPage, EditorHints.VirtualPath);
		final boolean foundNewline = optional(JavaSymbolType.NewLine);
		addMarkerSymbolIfNecessary();
		output(SpanKind.MetaCode, foundNewline ? AcceptedCharacters.SetOfNone : AcceptedCharacters.Any);
	}
	protected final Delegates.IAction layoutDirectiveDelegate = new Delegates.IAction() {
		@Override
		public void invoke() { layoutDirective(); }
	};

	protected void sessionStateDirective() {
		assertDirective(SyntaxConstants.Java.SessionStateKeyword);
		acceptAndMoveNext();
		sessionStateDirectiveCore();
	}
	protected final Delegates.IAction sessionStateDirectiveDelegate = new Delegates.IAction() {
		@Override
		public void invoke() { sessionStateDirective(); }
	};

	protected void sessionStateDirectiveCore() {
		sessionStateTypeDirective(RazorResources().getString("parseError.sessionDirectiveMissingValue"), new Delegates.IFunc2<String, String, SpanCodeGenerator>() {
			@Override
			public SpanCodeGenerator invoke(@Nullable String key, @Nullable String value) {
				assert key != null;
				assert value != null;
				return new RazorDirectiveAnnotationCodeGenerator(key, value);
			}
		});
	}

	protected void sessionStateTypeDirective(@Nonnull final String noValueError, @Nonnull final Delegates.IFunc2<String, String, SpanCodeGenerator> createCodeGenerator) {
		// Set the block type
		getContext().getCurrentBlock().setType(BlockType.Directive);

		// Accept whitespace
		final JavaSymbol remainingWs = acceptSingleWhiteSpaceCharacter();

		if (!getSpan().getSymbols().isEmpty()) {
			getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
		}

		output(SpanKind.MetaCode);

		if (remainingWs != null) {
			accept(remainingWs);
		}
		acceptWhile(isSpacingToken(false, true));

		// Parse a Type name
		if (!validSessionStateValue()) {
			getContext().onError(getCurrentLocation(), noValueError);
		}

		// Pull out the type name
		final ImmutableCollection<ISymbol> symbols = getSpan().getSymbols();
		final StringBuilder sb = new StringBuilder(symbols.size());
		for (ISymbol symbol : symbols) {
			final String s = Strings.nullToEmpty(symbol.getContent()).trim();
			sb.append(s);
		}
		final String sessionStateValue = sb.toString();

		// Setup code generation
		final SpanCodeGenerator generator = createCodeGenerator.invoke(SyntaxConstants.Java.SessionStateKeyword, sessionStateValue);
		assert generator != null;
		getSpan().setCodeGenerator(generator);

		// Output the span and finish the block
		completeBlock();
		output(SpanKind.Code);
	}

	protected boolean validSessionStateValue() {
		return optional(JavaSymbolType.Identifier);
	}

	// NOTE: Maybe look at excessive coupling?
	protected void helperDirective() {
		final boolean nested = getContext().isWithin(BlockType.Helper);

		// Set the block and span type
		getContext().getCurrentBlock().setType(BlockType.Helper);

		// Verify we're on "helper" and accept
		assertDirective(SyntaxConstants.Java.HelperKeyword);
		final Block block = new Block(getCurrentSymbol().getContent().toLowerCase(), getCurrentLocation());
		acceptAndMoveNext();

		if (nested) {
			getContext().onError(getCurrentLocation(), RazorResources().getString("parseError.helpers.cannot.be.nested"));
		}

		// Accept a single whitespace character if present, if not, we should stop now
		if (!at(JavaSymbolType.WhiteSpace)) {
			final String error;
			if (at(JavaSymbolType.NewLine)) {
				error = RazorResources().getString("errorComponent.newline");
			}
			else if (isEndOfFile()) {
				error = RazorResources().getString("errorComponent.endOfFile");
			}
			else {
				error = String.format(RazorResources().getString("errorComponent.character"), getCurrentSymbol().getContent());
			}

			getContext().onError(
				getCurrentLocation(),
				RazorResources().getString("parseError.unexpected.character.at.helper.name.start"),
				error
			);
			putCurrentBack();
			output(SpanKind.MetaCode);
			return;
		}

		final JavaSymbol remainingWs = acceptSingleWhiteSpaceCharacter();

		// Output metacode and continue
		output(SpanKind.MetaCode);
		if (remainingWs != null) {
			accept(remainingWs);
		}
		acceptWhile(isSpacingToken(false, true)); // Don't accept newlines.

		// Expecting an identifier (helper name)
		boolean errorReported = !required(JavaSymbolType.Identifier, true, RazorResources().getString("parseError.unexpected.character.at.helper.name.start"));
		if (!errorReported) {
			doAssert(JavaSymbolType.Identifier);
			acceptAndMoveNext();
		}

		acceptWhile(isSpacingToken(false, true));

		// Expecting parameter list start: "("
		final SourceLocation bracketErrorPos = getCurrentLocation();
		if (!optional(JavaSymbolType.LeftParenthesis)) {
			if (!errorReported) {
				errorReported = true;
				getContext().onError(
					getCurrentLocation(),
					RazorResources().getString("parseError.missingCharAfterHelperName"),
					"("
				);
			}
		}
		else {
			final SourceLocation bracketStart = getCurrentLocation();
			if (!balance(EnumSet.of(BalancingModes.NoErrorOnFailure), JavaSymbolType.LeftParenthesis, JavaSymbolType.RightParenthesis, bracketStart)) {
				errorReported = true;
				getContext().onError(
					bracketErrorPos,
					RazorResources().getString("parseError.unterminatedHelperParameterList")
				);
			}
			optional(JavaSymbolType.RightParenthesis);
		}

		final int bookmark = getCurrentLocation().getAbsoluteIndex();
		final Iterable<JavaSymbol> ws = readWhile(isSpacingToken(true, true));

		// Expecting a "{"
		final SourceLocation errorLocation = getCurrentLocation();
		final boolean headerComplete = at(JavaSymbolType.LeftBrace);
		if (headerComplete) {
			accept(ws);
			acceptAndMoveNext();
		}
		else {
			getContext().getSource().setPosition(bookmark);
			nextToken();
			acceptWhile(isSpacingToken(false, true));
			if (!errorReported) {
				getContext().onError(
					errorLocation,
					RazorResources().getString("parseError.missingCharAfterHelperParameters"),
					getLanguage().getSample(JavaSymbolType.LeftBrace)
				);
			}
		}

		// Grab the signature and build the code generator
		addMarkerSymbolIfNecessary();
		final LocationTagged<String> signature = SymbolExtensions.getContent(getSpan());
		final HelperCodeGenerator blockGen = new HelperCodeGenerator(signature, headerComplete);
		getContext().getCurrentBlock().setCodeGenerator(blockGen);

		// The block will generate appropriate code,
		getSpan().setCodeGenerator(SpanCodeGenerator.Null);

		if (!headerComplete) {
			completeBlock();
			output(SpanKind.Code);
			return;
		}

		getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
		output(SpanKind.Code);

		// We're valid, so parse the next block
		final AutoCompleteEditHandler bodyEditHandler = new AutoCompleteEditHandler(getLanguage().createTokenizeStringDelegate());
		try (IDisposable ignored = pushSpanConfig(delegate.defaultSpanConfigDelegate)) {
			try(IDisposable ignored2 = getContext().startBlock(BlockType.Statement)) {
				getSpan().setEditHandler(bodyEditHandler);
				delegate.parserStatements.codeBlock(false, block);
				completeBlock(true);
				output(SpanKind.Code);
			}
		}
		initialize(getSpan());

		ensureCurrent();

		getSpan().setCodeGenerator(SpanCodeGenerator.Null); // The block will generate the footer code.
		if (!optional(JavaSymbolType.RightBrace)) {
			// The } is missing, so set the initial signature span to use it as an autocomplete string
			bodyEditHandler.setAutoCompleteString("}");

			// Need to be able to accept anything to properly handle the autocomplete
			bodyEditHandler.setAcceptedCharacters(AcceptedCharacters.Any);
		}
		else {
			blockGen.setFooter(SymbolExtensions.getContent(getSpan()));
			getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
		}
		completeBlock();
		output(SpanKind.Code);
	}
	protected final Delegates.IAction helperDirectiveDelegate = new Delegates.IAction() {
		@Override
		public void invoke() { helperDirective(); }
	};

	protected void sectionDirective() {
		throw new NotImplementedException();
	}
	protected final Delegates.IAction sectionDirectiveDelegate = new Delegates.IAction() {
		@Override
		public void invoke() { sectionDirective(); }
	};

	protected void functionsDirective() {
		throw new NotImplementedException();
	}
	protected final Delegates.IAction functionsDirectiveDelegate = new Delegates.IAction() {
		@Override
		public void invoke() { functionsDirective(); }
	};

	protected void inheritsDirective() {
		// Verify we're on the right keyword and accept
		assertDirective(SyntaxConstants.Java.InheritsKeyword);
		acceptAndMoveNext();

		inheritsDirectiveCore();
	}
	protected final Delegates.IAction inheritsDirectiveDelegate = new Delegates.IAction() {
		@Override
		public void invoke() { inheritsDirective(); }
	};

	protected void assertDirective(@Nonnull final String directive) {
		doAssert(JavaSymbolType.Identifier);
		if (Debug.isAssertEnabled())
			assert Objects.equals(getCurrentSymbol().getContent(), directive);
	}

	protected void inheritsDirectiveCore() {
		baseTypeDirective(
			RazorResources().getString("parseError.inheritsKeyword.must.be.followed.by.typeName"),
			new Delegates.IFunc1<String, SpanCodeGenerator>() {
				@Override
				public SpanCodeGenerator invoke(@Nullable String baseType) {
					assert baseType != null;
					return new SetBaseTypeCodeGenerator(baseType);
				}
			}
		);
	}

	protected void baseTypeDirective(@Nonnull final String noTypeNameError, @Nonnull final Delegates.IFunc1<String, SpanCodeGenerator> createCodeGenerator) {
		throw new NotImplementedException();
	}

}
