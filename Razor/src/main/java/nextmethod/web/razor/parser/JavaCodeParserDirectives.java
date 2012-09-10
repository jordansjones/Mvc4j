package nextmethod.web.razor.parser;

import nextmethod.base.Delegates;
import nextmethod.base.NotImplementedException;
import nextmethod.web.razor.generator.SpanCodeGenerator;

import javax.annotation.Nonnull;

// TODO
final class JavaCodeParserDirectives {

	private final JavaCodeParser parser;

	JavaCodeParserDirectives(final JavaCodeParser parser) {
		this.parser = parser;
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
			parser.directiveParsers.put(directive, handler);
			parser.keywords.add(directive);
		}
	}

	protected void layoutDirective() {

	}
	protected final Delegates.IAction layoutDirectiveDelegate = new Delegates.IAction() {
		@Override
		public void invoke() { layoutDirective(); }
	};

	protected void sessionStateDirective() {

	}
	protected final Delegates.IAction sessionStateDirectiveDelegate = new Delegates.IAction() {
		@Override
		public void invoke() { sessionStateDirective(); }
	};

	protected void sessionStateDirectiveCore() {

	}

	protected void sessionStateTypeDirective(@Nonnull final String noValueError, @Nonnull final Delegates.IFunc2<String, String, SpanCodeGenerator> createCodeGenerator) {

	}

	protected boolean validSessionStateValue() {
		throw new NotImplementedException();
	}

	protected void helperDirective() {

	}
	protected final Delegates.IAction helperDirectiveDelegate = new Delegates.IAction() {
		@Override
		public void invoke() { helperDirective(); }
	};

	protected void sectionDirective() {

	}
	protected final Delegates.IAction sectionDirectiveDelegate = new Delegates.IAction() {
		@Override
		public void invoke() { sectionDirective(); }
	};

	protected void functionsDirective() {

	}
	protected final Delegates.IAction functionsDirectiveDelegate = new Delegates.IAction() {
		@Override
		public void invoke() { functionsDirective(); }
	};

	protected void inheritsDirective() {

	}
	protected final Delegates.IAction inheritsDirectiveDelegate = new Delegates.IAction() {
		@Override
		public void invoke() { inheritsDirective(); }
	};

	protected void assertDirective(@Nonnull final String directive) {

	}

	protected void inheritsDirectiveCore() {

	}

	protected void baseTypeDirective(@Nonnull final String noTypeNameError, @Nonnull final Delegates.IFunc1<String, SpanCodeGenerator> createCodeGenerator) {

	}

}
