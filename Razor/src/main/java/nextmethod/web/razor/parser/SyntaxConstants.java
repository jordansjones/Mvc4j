package nextmethod.web.razor.parser;

public final class SyntaxConstants {
	private SyntaxConstants() {
	}

	public static final String TextTagName = "text";
	public static final char TransitionCharacter = '@';
	public static final String TransitionString = "@";
	public static final String StartCommentSequence = "@*";
	public static final String EndCommentSequence = "*@";

	public static final class Java {
		private Java() {
		}

		public static final int UsingKeywordLength = 5;
		public static final String InheritsKeyword = "inherits";
		public static final String FunctionsKeyword = "functions";
		public static final String SectionKeyword = "section";
		public static final String HelperKeyword = "helper";
		public static final String ElseIfKeyword = "else if";
		public static final String PackageKeyword = "package";
		public static final String NamespaceKeyword = "namespace";
		public static final String ClassKeyword = "class";
		public static final String LayoutKeyword = "layout";
		public static final String SessionStateKeyword = "sessionstate";
	}
}
