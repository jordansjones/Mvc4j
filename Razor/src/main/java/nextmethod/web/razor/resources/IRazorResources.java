
package nextmethod.web.razor.resources;

import nextmethod.i18n.IResourceBundle;
import nextmethod.i18n.annotations.Bundle;

import javax.annotation.Generated;

@Generated(value = {
    "nextmethod.resourcegen.creators.MessagesI18nCreator"
}, date = "Mon Oct 22 22:33:26 MDT 2012")
@Bundle("nextmethod.web.razor.resources.RazorResources")
public interface IRazorResources
    extends IResourceBundle
{


    /**
     * Translated "The active parser must the same as either the markup or code parser.".
     * 
     * @return
     *     translated "The active parser must the same as either the markup or code parser."
     */
    @nextmethod.i18n.annotations.Key("activeParser.must.be.code.or.markup.parser")
    @nextmethod.i18n.annotations.DefaultMessage("The active parser must the same as either the markup or code parser.")
    public String activeParserMustBeCodeOrMarkupParser();

    /**
     * Translated "Block cannot be built because a Type has not been specified in the BlockBuilder".
     * 
     * @return
     *     translated "Block cannot be built because a Type has not been specified in the BlockBuilder"
     */
    @nextmethod.i18n.annotations.Key("block.type.not.specified")
    @nextmethod.i18n.annotations.DefaultMessage("Block cannot be built because a Type has not been specified in the BlockBuilder")
    public String blockTypeNotSpecified();

    /**
     * Translated "code".
     * 
     * @return
     *     translated "code"
     */
    @nextmethod.i18n.annotations.Key("blockName.code")
    @nextmethod.i18n.annotations.DefaultMessage("code")
    public String blockNameCode();

    /**
     * Translated "explicit expression".
     * 
     * @return
     *     translated "explicit expression"
     */
    @nextmethod.i18n.annotations.Key("blockName.explicitExpression")
    @nextmethod.i18n.annotations.DefaultMessage("explicit expression")
    public String blockNameExplicitExpression();

    /**
     * Translated "The "CancelBacktrack" method can be called only while in a look-ahead process started with the "BeginLookahead" method.".
     * 
     * @return
     *     translated "The "CancelBacktrack" method can be called only while in a look-ahead process started with the "BeginLookahead" method."
     */
    @nextmethod.i18n.annotations.Key("cancelBacktrack.must.be.called.within.lookahead")
    @nextmethod.i18n.annotations.DefaultMessage("The \"CancelBacktrack\" method can be called only while in a look-ahead process started with the \"BeginLookahead\" method.")
    public String cancelBacktrackMustBeCalledWithinLookahead();

    /**
     * Translated "Cannot call CreateCodeWriter, a CodeWriter was not provided to the Create method".
     * 
     * @return
     *     translated "Cannot call CreateCodeWriter, a CodeWriter was not provided to the Create method"
     */
    @nextmethod.i18n.annotations.Key("createCodeWriter.noCodeWriter")
    @nextmethod.i18n.annotations.DefaultMessage("Cannot call CreateCodeWriter, a CodeWriter was not provided to the Create method")
    public String createCodeWriterNoCodeWriter();

    /**
     * Translated ""EndBlock" was called without a matching call to "StartBlock".".
     * 
     * @return
     *     translated ""EndBlock" was called without a matching call to "StartBlock"."
     */
    @nextmethod.i18n.annotations.Key("endBlock.called.without.matching.startBlock")
    @nextmethod.i18n.annotations.DefaultMessage("\"EndBlock\" was called without a matching call to \"StartBlock\".")
    public String endBlockCalledWithoutMatchingStartBlock();

    /**
     * Translated ""{0}" character".
     * 
     * @param arg0
     * @return
     *     translated ""{0}" character"
     */
    @nextmethod.i18n.annotations.Key("errorComponent.character")
    @nextmethod.i18n.annotations.DefaultMessage("\"{0}\" character")
    public String errorComponentCharacter(final CharSequence arg0);

    /**
     * Translated ""{0}" character".
     * 
     * @return
     *     translated ""{0}" character"
     */
    @nextmethod.i18n.annotations.Key("errorComponent.character")
    @nextmethod.i18n.annotations.DefaultMessage("\"{0}\" character")
    public String errorComponentCharacter();

    /**
     * Translated "end of file".
     * 
     * @return
     *     translated "end of file"
     */
    @nextmethod.i18n.annotations.Key("errorComponent.endOfFile")
    @nextmethod.i18n.annotations.DefaultMessage("end of file")
    public String errorComponentEndOfFile();

    /**
     * Translated "line break".
     * 
     * @return
     *     translated "line break"
     */
    @nextmethod.i18n.annotations.Key("errorComponent.newline")
    @nextmethod.i18n.annotations.DefaultMessage("line break")
    public String errorComponentNewline();

    /**
     * Translated "space or line break".
     * 
     * @return
     *     translated "space or line break"
     */
    @nextmethod.i18n.annotations.Key("errorComponent.whitespace")
    @nextmethod.i18n.annotations.DefaultMessage("space or line break")
    public String errorComponentWhitespace();

    /**
     * Translated "&lt;&lt;newline sequence>>".
     * 
     * @return
     *     translated "&lt;&lt;newline sequence>>"
     */
    @nextmethod.i18n.annotations.Key("htmlSymbol.newLine")
    @nextmethod.i18n.annotations.DefaultMessage("<<newline sequence>>")
    public String htmlSymbolNewLine();

    /**
     * Translated "&lt;&lt;razor comment>>".
     * 
     * @return
     *     translated "&lt;&lt;razor comment>>"
     */
    @nextmethod.i18n.annotations.Key("htmlSymbol.razorComment")
    @nextmethod.i18n.annotations.DefaultMessage("<<razor comment>>")
    public String htmlSymbolRazorComment();

    /**
     * Translated "&lt;&lt;text>>".
     * 
     * @return
     *     translated "&lt;&lt;text>>"
     */
    @nextmethod.i18n.annotations.Key("htmlSymbol.text")
    @nextmethod.i18n.annotations.DefaultMessage("<<text>>")
    public String htmlSymbolText();

    /**
     * Translated "&lt;&lt;white space>>".
     * 
     * @return
     *     translated "&lt;&lt;white space>>"
     */
    @nextmethod.i18n.annotations.Key("htmlSymbol.whiteSpace")
    @nextmethod.i18n.annotations.DefaultMessage("<<white space>>")
    public String htmlSymbolWhiteSpace();

    /**
     * Translated "&lt;&lt;character literal>>".
     * 
     * @return
     *     translated "&lt;&lt;character literal>>"
     */
    @nextmethod.i18n.annotations.Key("javaSymbol.characterLiteral")
    @nextmethod.i18n.annotations.DefaultMessage("<<character literal>>")
    public String javaSymbolCharacterLiteral();

    /**
     * Translated "&lt;&lt;comment>>".
     * 
     * @return
     *     translated "&lt;&lt;comment>>"
     */
    @nextmethod.i18n.annotations.Key("javaSymbol.comment")
    @nextmethod.i18n.annotations.DefaultMessage("<<comment>>")
    public String javaSymbolComment();

    /**
     * Translated "&lt;&lt;identifier>>".
     * 
     * @return
     *     translated "&lt;&lt;identifier>>"
     */
    @nextmethod.i18n.annotations.Key("javaSymbol.identifier")
    @nextmethod.i18n.annotations.DefaultMessage("<<identifier>>")
    public String javaSymbolIdentifier();

    /**
     * Translated "&lt;&lt;integer literal>>".
     * 
     * @return
     *     translated "&lt;&lt;integer literal>>"
     */
    @nextmethod.i18n.annotations.Key("javaSymbol.integerLiteral")
    @nextmethod.i18n.annotations.DefaultMessage("<<integer literal>>")
    public String javaSymbolIntegerLiteral();

    /**
     * Translated "&lt;&lt;keyword>>".
     * 
     * @return
     *     translated "&lt;&lt;keyword>>"
     */
    @nextmethod.i18n.annotations.Key("javaSymbol.keyword")
    @nextmethod.i18n.annotations.DefaultMessage("<<keyword>>")
    public String javaSymbolKeyword();

    /**
     * Translated "&lt;&lt;newline sequence>>".
     * 
     * @return
     *     translated "&lt;&lt;newline sequence>>"
     */
    @nextmethod.i18n.annotations.Key("javaSymbol.newline")
    @nextmethod.i18n.annotations.DefaultMessage("<<newline sequence>>")
    public String javaSymbolNewline();

    /**
     * Translated "&lt;&lt;real literal>>".
     * 
     * @return
     *     translated "&lt;&lt;real literal>>"
     */
    @nextmethod.i18n.annotations.Key("javaSymbol.realLiteral")
    @nextmethod.i18n.annotations.DefaultMessage("<<real literal>>")
    public String javaSymbolRealLiteral();

    /**
     * Translated "&lt;&lt;string literal>>".
     * 
     * @return
     *     translated "&lt;&lt;string literal>>"
     */
    @nextmethod.i18n.annotations.Key("javaSymbol.stringLiteral")
    @nextmethod.i18n.annotations.DefaultMessage("<<string literal>>")
    public String javaSymbolStringLiteral();

    /**
     * Translated "&lt;&lt;white space>>".
     * 
     * @return
     *     translated "&lt;&lt;white space>>"
     */
    @nextmethod.i18n.annotations.Key("javaSymbol.whitespace")
    @nextmethod.i18n.annotations.DefaultMessage("<<white space>>")
    public String javaSymbolWhitespace();

    /**
     * Translated "Cannot use built-in RazorComment handler, language characteristics does not define the CommentStart, CommentStar and CommentBody known symbol types or parser does not override TokenizerBackedParser.OutputSpanBeforeRazorComment".
     * 
     * @return
     *     translated "Cannot use built-in RazorComment handler, language characteristics does not define the CommentStart, CommentStar and CommentBody known symbol types or parser does not override TokenizerBackedParser.OutputSpanBeforeRazorComment"
     */
    @nextmethod.i18n.annotations.Key("language.does.not.support.razorComment")
    @nextmethod.i18n.annotations.DefaultMessage("Cannot use built-in RazorComment handler, language characteristics does not define the CommentStart, CommentStar and CommentBody known symbol types or parser does not override TokenizerBackedParser.OutputSpanBeforeRazorComment")
    public String languageDoesNotSupportRazorComment();

    /**
     * Translated "The "@" character must be followed by a ":", "(", or a Java identifier.  If you intended to switch to markup, use an HTML start tag, for example:
     * 
     * @if(isLoggedIn) {
     *     &lt;p>Hello, @user!&lt;/p>
     * }".
     * 
     * @return
     *     translated "The "@" character must be followed by a ":", "(", or a Java identifier.  If you intended to switch to markup, use an HTML start tag, for example:
     *     
     *     @if(isLoggedIn) {
     *         &lt;p>Hello, @user!&lt;/p>
     *     }"
     */
    @nextmethod.i18n.annotations.Key("parseError.atInCode.must.be.followed.by.colon.paren.or.identifier.start")
    @nextmethod.i18n.annotations.DefaultMessage("The \"@\" character must be followed by a \":\", \"(\", or a Java identifier.  If you intended to switch to markup, use an HTML start tag, for example:\n\n@if(isLoggedIn) {\n    <p>Hello, @user!</p>\n}")
    public String parseErrorAtInCodeMustBeFollowedByColonParenOrIdentifierStart();

    /**
     * Translated "End of file was reached before the end of the block comment.  All comments started with "/*" sequence must be terminated with a matching "*<!---->/" sequence.".
     * 
     * @return
     *     translated "End of file was reached before the end of the block comment.  All comments started with "/*" sequence must be terminated with a matching "*<!---->/" sequence."
     */
    @nextmethod.i18n.annotations.Key("parseError.blockComment.not.terminated")
    @nextmethod.i18n.annotations.DefaultMessage("End of file was reached before the end of the block comment.  All comments started with \"/*\" sequence must be terminated with a matching \"*/\" sequence.")
    public String parseErrorBlockCommentNotTerminated();

    /**
     * Translated "The "{0}" block was not terminated.  All "{0}" statements must be terminated with a matching "{1}".".
     * 
     * @param arg1
     * @param arg0
     * @return
     *     translated "The "{0}" block was not terminated.  All "{0}" statements must be terminated with a matching "{1}"."
     */
    @nextmethod.i18n.annotations.Key("parseError.blockNotTerminated")
    @nextmethod.i18n.annotations.DefaultMessage("The \"{0}\" block was not terminated.  All \"{0}\" statements must be terminated with a matching \"{1}\".")
    public String parseErrorBlockNotTerminated(final CharSequence arg0, final CharSequence arg1);

    /**
     * Translated "The "{0}" block was not terminated.  All "{0}" statements must be terminated with a matching "{1}".".
     * 
     * @return
     *     translated "The "{0}" block was not terminated.  All "{0}" statements must be terminated with a matching "{1}"."
     */
    @nextmethod.i18n.annotations.Key("parseError.blockNotTerminated")
    @nextmethod.i18n.annotations.DefaultMessage("The \"{0}\" block was not terminated.  All \"{0}\" statements must be terminated with a matching \"{1}\".")
    public String parseErrorBlockNotTerminated();

    /**
     * Translated "An opening "{0}" is missing the corresponding closing "{1}".".
     * 
     * @param arg1
     * @param arg0
     * @return
     *     translated "An opening "{0}" is missing the corresponding closing "{1}"."
     */
    @nextmethod.i18n.annotations.Key("parseError.expected.closeBracket.before.eof")
    @nextmethod.i18n.annotations.DefaultMessage("An opening \"{0}\" is missing the corresponding closing \"{1}\".")
    public String parseErrorExpectedCloseBracketBeforeEof(final CharSequence arg0, final CharSequence arg1);

    /**
     * Translated "An opening "{0}" is missing the corresponding closing "{1}".".
     * 
     * @return
     *     translated "An opening "{0}" is missing the corresponding closing "{1}"."
     */
    @nextmethod.i18n.annotations.Key("parseError.expected.closeBracket.before.eof")
    @nextmethod.i18n.annotations.DefaultMessage("An opening \"{0}\" is missing the corresponding closing \"{1}\".")
    public String parseErrorExpectedCloseBracketBeforeEof();

    /**
     * Translated "The {0} block is missing a closing "{1}" character.  Make sure you have a matching "{1}" character for all the "{2}" characters within this block, and that none of the "{1}" characters are being interpreted as markup.".
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     translated "The {0} block is missing a closing "{1}" character.  Make sure you have a matching "{1}" character for all the "{2}" characters within this block, and that none of the "{1}" characters are being interpreted as markup."
     */
    @nextmethod.i18n.annotations.Key("parseError.expected.endOfBlock.before.eof")
    @nextmethod.i18n.annotations.DefaultMessage("The {0} block is missing a closing \"{1}\" character.  Make sure you have a matching \"{1}\" character for all the \"{2}\" characters within this block, and that none of the \"{1}\" characters are being interpreted as markup.")
    public String parseErrorExpectedEndOfBlockBeforeEof(final CharSequence arg0, final CharSequence arg1, final CharSequence arg2);

    /**
     * Translated "The {0} block is missing a closing "{1}" character.  Make sure you have a matching "{1}" character for all the "{2}" characters within this block, and that none of the "{1}" characters are being interpreted as markup.".
     * 
     * @return
     *     translated "The {0} block is missing a closing "{1}" character.  Make sure you have a matching "{1}" character for all the "{2}" characters within this block, and that none of the "{1}" characters are being interpreted as markup."
     */
    @nextmethod.i18n.annotations.Key("parseError.expected.endOfBlock.before.eof")
    @nextmethod.i18n.annotations.DefaultMessage("The {0} block is missing a closing \"{1}\" character.  Make sure you have a matching \"{1}\" character for all the \"{2}\" characters within this block, and that none of the \"{1}\" characters are being interpreted as markup.")
    public String parseErrorExpectedEndOfBlockBeforeEof();

    /**
     * Translated "Expected "{0}".".
     * 
     * @param arg0
     * @return
     *     translated "Expected "{0}"."
     */
    @nextmethod.i18n.annotations.Key("parseError.expected.x")
    @nextmethod.i18n.annotations.DefaultMessage("Expected \"{0}\".")
    public String parseErrorExpectedX(final CharSequence arg0);

    /**
     * Translated "Expected "{0}".".
     * 
     * @return
     *     translated "Expected "{0}"."
     */
    @nextmethod.i18n.annotations.Key("parseError.expected.x")
    @nextmethod.i18n.annotations.DefaultMessage("Expected \"{0}\".")
    public String parseErrorExpectedX();

    /**
     * Translated "Helper blocks cannot be nested within each other.".
     * 
     * @return
     *     translated "Helper blocks cannot be nested within each other."
     */
    @nextmethod.i18n.annotations.Key("parseError.helpers.cannot.be.nested")
    @nextmethod.i18n.annotations.DefaultMessage("Helper blocks cannot be nested within each other.")
    public String parseErrorHelpersCannotBeNested();

    /**
     * Translated "The 'inherits' keyword must be followed by a type name on the same line.".
     * 
     * @return
     *     translated "The 'inherits' keyword must be followed by a type name on the same line."
     */
    @nextmethod.i18n.annotations.Key("parseError.inheritsKeyword.must.be.followed.by.typeName")
    @nextmethod.i18n.annotations.DefaultMessage("The 'inherits' keyword must be followed by a type name on the same line.")
    public String parseErrorInheritsKeywordMustBeFollowedByTypeName();

    /**
     * Translated "Inline markup blocks (@&lt;p>Content&lt;/p>) cannot be nested.  Only one level of inline markup is allowed.".
     * 
     * @return
     *     translated "Inline markup blocks (@&lt;p>Content&lt;/p>) cannot be nested.  Only one level of inline markup is allowed."
     */
    @nextmethod.i18n.annotations.Key("parseError.inlineMarkup.blocks.cannot.be.nested")
    @nextmethod.i18n.annotations.DefaultMessage("Inline markup blocks (@<p>Content</p>) cannot be nested.  Only one level of inline markup is allowed.")
    public String parseErrorInlineMarkupBlocksCannotBeNested();

    /**
     * Translated "Markup in a code block must start with a tag and all start tags must be matched with end tags.  Do not use unclosed tags like "&lt;br>".  Instead use self-closing tags like "&lt;br/>".".
     * 
     * @return
     *     translated "Markup in a code block must start with a tag and all start tags must be matched with end tags.  Do not use unclosed tags like "&lt;br>".  Instead use self-closing tags like "&lt;br/>"."
     */
    @nextmethod.i18n.annotations.Key("parseError.markupBlock.must.start.with.tag")
    @nextmethod.i18n.annotations.DefaultMessage("Markup in a code block must start with a tag and all start tags must be matched with end tags.  Do not use unclosed tags like \"<br>\".  Instead use self-closing tags like \"<br/>\".")
    public String parseErrorMarkupBlockMustStartWithTag();

    /**
     * Translated "Expected a "{0}" after the helper name.".
     * 
     * @param arg0
     * @return
     *     translated "Expected a "{0}" after the helper name."
     */
    @nextmethod.i18n.annotations.Key("parseError.missingCharAfterHelperName")
    @nextmethod.i18n.annotations.DefaultMessage("Expected a \"{0}\" after the helper name.")
    public String parseErrorMissingCharAfterHelperName(final CharSequence arg0);

    /**
     * Translated "Expected a "{0}" after the helper name.".
     * 
     * @return
     *     translated "Expected a "{0}" after the helper name."
     */
    @nextmethod.i18n.annotations.Key("parseError.missingCharAfterHelperName")
    @nextmethod.i18n.annotations.DefaultMessage("Expected a \"{0}\" after the helper name.")
    public String parseErrorMissingCharAfterHelperName();

    /**
     * Translated "Expected a "{0}" after the helper parameters.".
     * 
     * @param arg0
     * @return
     *     translated "Expected a "{0}" after the helper parameters."
     */
    @nextmethod.i18n.annotations.Key("parseError.missingCharAfterHelperParameters")
    @nextmethod.i18n.annotations.DefaultMessage("Expected a \"{0}\" after the helper parameters.")
    public String parseErrorMissingCharAfterHelperParameters(final CharSequence arg0);

    /**
     * Translated "Expected a "{0}" after the helper parameters.".
     * 
     * @return
     *     translated "Expected a "{0}" after the helper parameters."
     */
    @nextmethod.i18n.annotations.Key("parseError.missingCharAfterHelperParameters")
    @nextmethod.i18n.annotations.DefaultMessage("Expected a \"{0}\" after the helper parameters.")
    public String parseErrorMissingCharAfterHelperParameters();

    /**
     * Translated "The "{0}" element was not closed.  All elements must be either self-closing or have a matching end tag.".
     * 
     * @param arg0
     * @return
     *     translated "The "{0}" element was not closed.  All elements must be either self-closing or have a matching end tag."
     */
    @nextmethod.i18n.annotations.Key("parseError.missingEndTag")
    @nextmethod.i18n.annotations.DefaultMessage("The \"{0}\" element was not closed.  All elements must be either self-closing or have a matching end tag.")
    public String parseErrorMissingEndTag(final CharSequence arg0);

    /**
     * Translated "The "{0}" element was not closed.  All elements must be either self-closing or have a matching end tag.".
     * 
     * @return
     *     translated "The "{0}" element was not closed.  All elements must be either self-closing or have a matching end tag."
     */
    @nextmethod.i18n.annotations.Key("parseError.missingEndTag")
    @nextmethod.i18n.annotations.DefaultMessage("The \"{0}\" element was not closed.  All elements must be either self-closing or have a matching end tag.")
    public String parseErrorMissingEndTag();

    /**
     * Translated "Sections cannot be empty.  The "@section" keyword must be followed by a block of markup surrounded by "'{''}'".  For example:
     * 
     * @section Sidebar {
     *    &lt;!-- Markup and text goes here -->
     * }".
     * 
     * @return
     *     translated "Sections cannot be empty.  The "@section" keyword must be followed by a block of markup surrounded by "'{''}'".  For example:
     *     
     *     @section Sidebar {
     *        &lt;!-- Markup and text goes here -->
     *     }"
     */
    @nextmethod.i18n.annotations.Key("parseError.missingOpenBraceAfterSection")
    @nextmethod.i18n.annotations.DefaultMessage("Sections cannot be empty.  The \"@section\" keyword must be followed by a block of markup surrounded by \"'{''}'\".  For example:\n\n@section Sidebar {\n   <!-- Markup and text goes here -->\n}")
    public String parseErrorMissingOpenBraceAfterSection();

    /**
     * Translated "Namespace imports and type aliases cannot be placed within code blocks.  They must immediately follow an "@" character in markup.  It is recommended that you put them at the top of the page, as in the following example:
     * 
     * @using System.Drawing;
     * @{
     *     // OK here to use types from System.Drawing in the page.
     * }".
     * 
     * @return
     *     translated "Namespace imports and type aliases cannot be placed within code blocks.  They must immediately follow an "@" character in markup.  It is recommended that you put them at the top of the page, as in the following example:
     *     
     *     @using System.Drawing;
     *     @{
     *         // OK here to use types from System.Drawing in the page.
     *     }"
     */
    @nextmethod.i18n.annotations.Key("parseError.namespaceImportAndTypeAlias.cannot.exist.within.codeBlock")
    @nextmethod.i18n.annotations.DefaultMessage("Namespace imports and type aliases cannot be placed within code blocks.  They must immediately follow an \"@\" character in markup.  It is recommended that you put them at the top of the page, as in the following example:\n\n@using System.Drawing;\n@{\n    // OK here to use types from System.Drawing in the page.\n}")
    public String parseErrorNamespaceImportAndTypeAliasCannotExistWithinCodeBlock();

    /**
     * Translated "The "Imports" keyword must be followed by a namespace or a type alias on the same line.".
     * 
     * @return
     *     translated "The "Imports" keyword must be followed by a namespace or a type alias on the same line."
     */
    @nextmethod.i18n.annotations.Key("parseError.namespaceOrTypeAliasExpected")
    @nextmethod.i18n.annotations.DefaultMessage("The \"Imports\" keyword must be followed by a namespace or a type alias on the same line.")
    public String parseErrorNamespaceOrTypeAliasExpected();

    /**
     * Translated "Outer tag is missing a name. The first character of a markup block must be an HTML tag with a valid name.".
     * 
     * @return
     *     translated "Outer tag is missing a name. The first character of a markup block must be an HTML tag with a valid name."
     */
    @nextmethod.i18n.annotations.Key("parseError.outerTagMissingName")
    @nextmethod.i18n.annotations.DefaultMessage("Outer tag is missing a name. The first character of a markup block must be an HTML tag with a valid name.")
    public String parseErrorOuterTagMissingName();

    /**
     * Translated "End of file was reached before the end of the block comment.  All comments that start with the "@*" sequence must be terminated with a matching "*@" sequence.".
     * 
     * @return
     *     translated "End of file was reached before the end of the block comment.  All comments that start with the "@*" sequence must be terminated with a matching "*@" sequence."
     */
    @nextmethod.i18n.annotations.Key("parseError.razorComment.not.terminated")
    @nextmethod.i18n.annotations.DefaultMessage("End of file was reached before the end of the block comment.  All comments that start with the \"@*\" sequence must be terminated with a matching \"*@\" sequence.")
    public String parseErrorRazorCommentNotTerminated();

    /**
     * Translated ""{0}" is a reserved word and cannot be used in implicit expressions.  An explicit expression ("@()") must be used.".
     * 
     * @param arg0
     * @return
     *     translated ""{0}" is a reserved word and cannot be used in implicit expressions.  An explicit expression ("@()") must be used."
     */
    @nextmethod.i18n.annotations.Key("parseError.reservedWord")
    @nextmethod.i18n.annotations.DefaultMessage("\"{0}\" is a reserved word and cannot be used in implicit expressions.  An explicit expression (\"@()\") must be used.")
    public String parseErrorReservedWord(final CharSequence arg0);

    /**
     * Translated ""{0}" is a reserved word and cannot be used in implicit expressions.  An explicit expression ("@()") must be used.".
     * 
     * @return
     *     translated ""{0}" is a reserved word and cannot be used in implicit expressions.  An explicit expression ("@()") must be used."
     */
    @nextmethod.i18n.annotations.Key("parseError.reservedWord")
    @nextmethod.i18n.annotations.DefaultMessage("\"{0}\" is a reserved word and cannot be used in implicit expressions.  An explicit expression (\"@()\") must be used.")
    public String parseErrorReservedWord();

    /**
     * Translated "Section blocks ("{0}") cannot be nested.  Only one level of section blocks are allowed.".
     * 
     * @param arg0
     * @return
     *     translated "Section blocks ("{0}") cannot be nested.  Only one level of section blocks are allowed."
     */
    @nextmethod.i18n.annotations.Key("parseError.sections.cannot.be.nested")
    @nextmethod.i18n.annotations.DefaultMessage("Section blocks (\"{0}\") cannot be nested.  Only one level of section blocks are allowed.")
    public String parseErrorSectionsCannotBeNested(final CharSequence arg0);

    /**
     * Translated "Section blocks ("{0}") cannot be nested.  Only one level of section blocks are allowed.".
     * 
     * @return
     *     translated "Section blocks ("{0}") cannot be nested.  Only one level of section blocks are allowed."
     */
    @nextmethod.i18n.annotations.Key("parseError.sections.cannot.be.nested")
    @nextmethod.i18n.annotations.DefaultMessage("Section blocks (\"{0}\") cannot be nested.  Only one level of section blocks are allowed.")
    public String parseErrorSectionsCannotBeNested();

    /**
     * Translated "Expected a "{0}" but found a "{1}".  Block statements must be enclosed in "'{'" and "'}'".  You cannot use single-statement control-flow statements in RZHTML pages. For example, the following is not allowed:
     * 
     * @if(isLoggedIn)
     *     &lt;p>Hello, @user&lt;/p>
     * 
     * Instead, wrap the contents of the block in "'{''}'":
     * 
     * @if(isLoggedIn) '{'
     *     &lt;p>Hello, @user&lt;/p>
     * '}'".
     * 
     * @param arg1
     * @param arg0
     * @return
     *     translated "Expected a "{0}" but found a "{1}".  Block statements must be enclosed in "'{'" and "'}'".  You cannot use single-statement control-flow statements in RZHTML pages. For example, the following is not allowed:
     *     
     *     @if(isLoggedIn)
     *         &lt;p>Hello, @user&lt;/p>
     *     
     *     Instead, wrap the contents of the block in "'{''}'":
     *     
     *     @if(isLoggedIn) '{'
     *         &lt;p>Hello, @user&lt;/p>
     *     '}'"
     */
    @nextmethod.i18n.annotations.Key("parseError.singleLine.controlFlowStatements.not.allowed")
    @nextmethod.i18n.annotations.DefaultMessage("Expected a \"{0}\" but found a \"{1}\".  Block statements must be enclosed in \"'{'\" and \"'}'\".  You cannot use single-statement control-flow statements in RZHTML pages. For example, the following is not allowed:\n\n@if(isLoggedIn)\n    <p>Hello, @user</p>\n\nInstead, wrap the contents of the block in \"'{''}'\":\n\n@if(isLoggedIn) '{'\n    <p>Hello, @user</p>\n'}'")
    public String parseErrorSingleLineControlFlowStatementsNotAllowed(final CharSequence arg0, final CharSequence arg1);

    /**
     * Translated "Expected a "{0}" but found a "{1}".  Block statements must be enclosed in "'{'" and "'}'".  You cannot use single-statement control-flow statements in RZHTML pages. For example, the following is not allowed:
     * 
     * @if(isLoggedIn)
     *     &lt;p>Hello, @user&lt;/p>
     * 
     * Instead, wrap the contents of the block in "'{''}'":
     * 
     * @if(isLoggedIn) '{'
     *     &lt;p>Hello, @user&lt;/p>
     * '}'".
     * 
     * @return
     *     translated "Expected a "{0}" but found a "{1}".  Block statements must be enclosed in "'{'" and "'}'".  You cannot use single-statement control-flow statements in RZHTML pages. For example, the following is not allowed:
     *     
     *     @if(isLoggedIn)
     *         &lt;p>Hello, @user&lt;/p>
     *     
     *     Instead, wrap the contents of the block in "'{''}'":
     *     
     *     @if(isLoggedIn) '{'
     *         &lt;p>Hello, @user&lt;/p>
     *     '}'"
     */
    @nextmethod.i18n.annotations.Key("parseError.singleLine.controlFlowStatements.not.allowed")
    @nextmethod.i18n.annotations.DefaultMessage("Expected a \"{0}\" but found a \"{1}\".  Block statements must be enclosed in \"'{'\" and \"'}'\".  You cannot use single-statement control-flow statements in RZHTML pages. For example, the following is not allowed:\n\n@if(isLoggedIn)\n    <p>Hello, @user</p>\n\nInstead, wrap the contents of the block in \"'{''}'\":\n\n@if(isLoggedIn) '{'\n    <p>Hello, @user</p>\n'}'")
    public String parseErrorSingleLineControlFlowStatementsNotAllowed();

    /**
     * Translated ""&lt;text>" and "&lt;/text>" tags cannot contain attributes.".
     * 
     * @return
     *     translated ""&lt;text>" and "&lt;/text>" tags cannot contain attributes."
     */
    @nextmethod.i18n.annotations.Key("parseError.textTagCannotContainAttributes")
    @nextmethod.i18n.annotations.DefaultMessage("\"<text>\" and \"</text>\" tags cannot contain attributes.")
    public String parseErrorTextTagCannotContainAttributes();

    /**
     * Translated "Unexpected "{0}"".
     * 
     * @param arg0
     * @return
     *     translated "Unexpected "{0}""
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpected")
    @nextmethod.i18n.annotations.DefaultMessage("Unexpected \"{0}\"")
    public String parseErrorUnexpected(final CharSequence arg0);

    /**
     * Translated "Unexpected "{0}"".
     * 
     * @return
     *     translated "Unexpected "{0}""
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpected")
    @nextmethod.i18n.annotations.DefaultMessage("Unexpected \"{0}\"")
    public String parseErrorUnexpected();

    /**
     * Translated "Unexpected {0} after helper keyword.  All helpers must have a name which starts with an "_" or alphabetic character. The remaining characters must be either "_" or alphanumeric.".
     * 
     * @param arg0
     * @return
     *     translated "Unexpected {0} after helper keyword.  All helpers must have a name which starts with an "_" or alphabetic character. The remaining characters must be either "_" or alphanumeric."
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpected.character.at.helper.name.start")
    @nextmethod.i18n.annotations.DefaultMessage("Unexpected {0} after helper keyword.  All helpers must have a name which starts with an \"_\" or alphabetic character. The remaining characters must be either \"_\" or alphanumeric.")
    public String parseErrorUnexpectedCharacterAtHelperNameStart(final CharSequence arg0);

    /**
     * Translated "Unexpected {0} after helper keyword.  All helpers must have a name which starts with an "_" or alphabetic character. The remaining characters must be either "_" or alphanumeric.".
     * 
     * @return
     *     translated "Unexpected {0} after helper keyword.  All helpers must have a name which starts with an "_" or alphabetic character. The remaining characters must be either "_" or alphanumeric."
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpected.character.at.helper.name.start")
    @nextmethod.i18n.annotations.DefaultMessage("Unexpected {0} after helper keyword.  All helpers must have a name which starts with an \"_\" or alphabetic character. The remaining characters must be either \"_\" or alphanumeric.")
    public String parseErrorUnexpectedCharacterAtHelperNameStart();

    /**
     * Translated "Unexpected {0} after section keyword.  Section names must start with an "_" or alphabetic character, and the remaining characters must be either "_" or alphanumeric.".
     * 
     * @param arg0
     * @return
     *     translated "Unexpected {0} after section keyword.  Section names must start with an "_" or alphabetic character, and the remaining characters must be either "_" or alphanumeric."
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpected.character.at.section.name.start")
    @nextmethod.i18n.annotations.DefaultMessage("Unexpected {0} after section keyword.  Section names must start with an \"_\" or alphabetic character, and the remaining characters must be either \"_\" or alphanumeric.")
    public String parseErrorUnexpectedCharacterAtSectionNameStart(final CharSequence arg0);

    /**
     * Translated "Unexpected {0} after section keyword.  Section names must start with an "_" or alphabetic character, and the remaining characters must be either "_" or alphanumeric.".
     * 
     * @return
     *     translated "Unexpected {0} after section keyword.  Section names must start with an "_" or alphabetic character, and the remaining characters must be either "_" or alphanumeric."
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpected.character.at.section.name.start")
    @nextmethod.i18n.annotations.DefaultMessage("Unexpected {0} after section keyword.  Section names must start with an \"_\" or alphabetic character, and the remaining characters must be either \"_\" or alphanumeric.")
    public String parseErrorUnexpectedCharacterAtSectionNameStart();

    /**
     * Translated ""{0}" is not valid at the start of a code block.  Only identifiers, keywords, comments, "(" and "'{'" are valid.".
     * 
     * @param arg0
     * @return
     *     translated ""{0}" is not valid at the start of a code block.  Only identifiers, keywords, comments, "(" and "'{'" are valid."
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpected.character.at.start.of.codeBlock")
    @nextmethod.i18n.annotations.DefaultMessage("\"{0}\" is not valid at the start of a code block.  Only identifiers, keywords, comments, \"(\" and \"'{'\" are valid.")
    public String parseErrorUnexpectedCharacterAtStartOfCodeBlock(final CharSequence arg0);

    /**
     * Translated ""{0}" is not valid at the start of a code block.  Only identifiers, keywords, comments, "(" and "'{'" are valid.".
     * 
     * @return
     *     translated ""{0}" is not valid at the start of a code block.  Only identifiers, keywords, comments, "(" and "'{'" are valid."
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpected.character.at.start.of.codeBlock")
    @nextmethod.i18n.annotations.DefaultMessage("\"{0}\" is not valid at the start of a code block.  Only identifiers, keywords, comments, \"(\" and \"'{'\" are valid.")
    public String parseErrorUnexpectedCharacterAtStartOfCodeBlock();

    /**
     * Translated "End-of-file was found after the "@" character.  "@" must be followed by a valid code block.  If you want to output an "@", escape it using the sequence: "@@"".
     * 
     * @return
     *     translated "End-of-file was found after the "@" character.  "@" must be followed by a valid code block.  If you want to output an "@", escape it using the sequence: "@@""
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpected.endOfFile.at.start.of.codeBlock")
    @nextmethod.i18n.annotations.DefaultMessage("End-of-file was found after the \"@\" character.  \"@\" must be followed by a valid code block.  If you want to output an \"@\", escape it using the sequence: \"@@\"")
    public String parseErrorUnexpectedEndOfFileAtStartOfCodeBlock();

    /**
     * Translated "Unexpected "{0}" keyword after "@" character.  Once inside code, you do not need to prefix constructs like "{0}" with "@".".
     * 
     * @param arg0
     * @return
     *     translated "Unexpected "{0}" keyword after "@" character.  Once inside code, you do not need to prefix constructs like "{0}" with "@"."
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpected.keyword.after.at")
    @nextmethod.i18n.annotations.DefaultMessage("Unexpected \"{0}\" keyword after \"@\" character.  Once inside code, you do not need to prefix constructs like \"{0}\" with \"@\".")
    public String parseErrorUnexpectedKeywordAfterAt(final CharSequence arg0);

    /**
     * Translated "Unexpected "{0}" keyword after "@" character.  Once inside code, you do not need to prefix constructs like "{0}" with "@".".
     * 
     * @return
     *     translated "Unexpected "{0}" keyword after "@" character.  Once inside code, you do not need to prefix constructs like "{0}" with "@"."
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpected.keyword.after.at")
    @nextmethod.i18n.annotations.DefaultMessage("Unexpected \"{0}\" keyword after \"@\" character.  Once inside code, you do not need to prefix constructs like \"{0}\" with \"@\".")
    public String parseErrorUnexpectedKeywordAfterAt();

    /**
     * Translated "Unexpected "'{'" after "@" character. Once inside the body of a code block (@if '{''}', @'{''}', etc.) you do not need to use "@'{'" to switch to code.".
     * 
     * @return
     *     translated "Unexpected "'{'" after "@" character. Once inside the body of a code block (@if '{''}', @'{''}', etc.) you do not need to use "@'{'" to switch to code."
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpected.nested.codeBlock")
    @nextmethod.i18n.annotations.DefaultMessage("Unexpected \"'{'\" after \"@\" character. Once inside the body of a code block (@if '{''}', @'{''}', etc.) you do not need to use \"@'{'\" to switch to code.")
    public String parseErrorUnexpectedNestedCodeBlock();

    /**
     * Translated "A space or line break was encountered after the "@" character.  Only valid identifiers, keywords, comments, "(" and "'{'" are valid at the start of a code block and they must occur immediately following "@" with no space in between.".
     * 
     * @return
     *     translated "A space or line break was encountered after the "@" character.  Only valid identifiers, keywords, comments, "(" and "'{'" are valid at the start of a code block and they must occur immediately following "@" with no space in between."
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpected.whiteSpace.at.start.of.codeBlock")
    @nextmethod.i18n.annotations.DefaultMessage("A space or line break was encountered after the \"@\" character.  Only valid identifiers, keywords, comments, \"(\" and \"'{'\" are valid at the start of a code block and they must occur immediately following \"@\" with no space in between.")
    public String parseErrorUnexpectedWhiteSpaceAtStartOfCodeBlock();

    /**
     * Translated "Encountered end tag "{0}" with no matching start tag.  Are your start/end tags properly balanced?".
     * 
     * @param arg0
     * @return
     *     translated "Encountered end tag "{0}" with no matching start tag.  Are your start/end tags properly balanced?"
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpectedEndTag")
    @nextmethod.i18n.annotations.DefaultMessage("Encountered end tag \"{0}\" with no matching start tag.  Are your start/end tags properly balanced?")
    public String parseErrorUnexpectedEndTag(final CharSequence arg0);

    /**
     * Translated "Encountered end tag "{0}" with no matching start tag.  Are your start/end tags properly balanced?".
     * 
     * @return
     *     translated "Encountered end tag "{0}" with no matching start tag.  Are your start/end tags properly balanced?"
     */
    @nextmethod.i18n.annotations.Key("parseError.unexpectedEndTag")
    @nextmethod.i18n.annotations.DefaultMessage("Encountered end tag \"{0}\" with no matching start tag.  Are your start/end tags properly balanced?")
    public String parseErrorUnexpectedEndTag();

    /**
     * Translated "End of file or an unexpected character was reached before the "{0}" tag could be parsed.  Elements inside markup blocks must be complete. They must either be self-closing ("&lt;br />") or have matching end tags ("&lt;p>Hello&lt;/p>").  If you intended to display a "&lt;" character, use the "&amp;lt;" HTML entity.".
     * 
     * @param arg0
     * @return
     *     translated "End of file or an unexpected character was reached before the "{0}" tag could be parsed.  Elements inside markup blocks must be complete. They must either be self-closing ("&lt;br />") or have matching end tags ("&lt;p>Hello&lt;/p>").  If you intended to display a "&lt;" character, use the "&amp;lt;" HTML entity."
     */
    @nextmethod.i18n.annotations.Key("parseError.unfinishedTag")
    @nextmethod.i18n.annotations.DefaultMessage("End of file or an unexpected character was reached before the \"{0}\" tag could be parsed.  Elements inside markup blocks must be complete. They must either be self-closing (\"<br />\") or have matching end tags (\"<p>Hello</p>\").  If you intended to display a \"<\" character, use the \"&lt;\" HTML entity.")
    public String parseErrorUnfinishedTag(final CharSequence arg0);

    /**
     * Translated "End of file or an unexpected character was reached before the "{0}" tag could be parsed.  Elements inside markup blocks must be complete. They must either be self-closing ("&lt;br />") or have matching end tags ("&lt;p>Hello&lt;/p>").  If you intended to display a "&lt;" character, use the "&amp;lt;" HTML entity.".
     * 
     * @return
     *     translated "End of file or an unexpected character was reached before the "{0}" tag could be parsed.  Elements inside markup blocks must be complete. They must either be self-closing ("&lt;br />") or have matching end tags ("&lt;p>Hello&lt;/p>").  If you intended to display a "&lt;" character, use the "&amp;lt;" HTML entity."
     */
    @nextmethod.i18n.annotations.Key("parseError.unfinishedTag")
    @nextmethod.i18n.annotations.DefaultMessage("End of file or an unexpected character was reached before the \"{0}\" tag could be parsed.  Elements inside markup blocks must be complete. They must either be self-closing (\"<br />\") or have matching end tags (\"<p>Hello</p>\").  If you intended to display a \"<\" character, use the \"&lt;\" HTML entity.")
    public String parseErrorUnfinishedTag();

    /**
     * Translated "Unknown option: "{0}".".
     * 
     * @param arg0
     * @return
     *     translated "Unknown option: "{0}"."
     */
    @nextmethod.i18n.annotations.Key("parseError.unknownOption")
    @nextmethod.i18n.annotations.DefaultMessage("Unknown option: \"{0}\".")
    public String parseErrorUnknownOption(final CharSequence arg0);

    /**
     * Translated "Unknown option: "{0}".".
     * 
     * @return
     *     translated "Unknown option: "{0}"."
     */
    @nextmethod.i18n.annotations.Key("parseError.unknownOption")
    @nextmethod.i18n.annotations.DefaultMessage("Unknown option: \"{0}\".")
    public String parseErrorUnknownOption();

    /**
     * Translated "Unterminated string literal.  Strings that start with a quotation mark (") must be terminated before the end of the line.  However, strings that start with @ and a quotation mark (@") can span multiple lines.".
     * 
     * @return
     *     translated "Unterminated string literal.  Strings that start with a quotation mark (") must be terminated before the end of the line.  However, strings that start with @ and a quotation mark (@") can span multiple lines."
     */
    @nextmethod.i18n.annotations.Key("parseError.unterminated.string.literal")
    @nextmethod.i18n.annotations.DefaultMessage("Unterminated string literal.  Strings that start with a quotation mark (\") must be terminated before the end of the line.  However, strings that start with @ and a quotation mark (@\") can span multiple lines.")
    public String parseErrorUnterminatedStringLiteral();

    /**
     * Translated "Helper parameter list is missing a closing ")".".
     * 
     * @return
     *     translated "Helper parameter list is missing a closing ")"."
     */
    @nextmethod.i18n.annotations.Key("parseError.unterminatedHelperParameterList")
    @nextmethod.i18n.annotations.DefaultMessage("Helper parameter list is missing a closing \")\".")
    public String parseErrorUnterminatedHelperParameterList();

    /**
     * Translated "Parser was started with a null Context property.  The Context property must be set BEFORE calling any methods on the parser.".
     * 
     * @return
     *     translated "Parser was started with a null Context property.  The Context property must be set BEFORE calling any methods on the parser."
     */
    @nextmethod.i18n.annotations.Key("parser.context.not.set")
    @nextmethod.i18n.annotations.DefaultMessage("Parser was started with a null Context property.  The Context property must be set BEFORE calling any methods on the parser.")
    public String parserContextNotSet();

    /**
     * Translated "Cannot complete the tree, StartBlock must be called at least once.".
     * 
     * @return
     *     translated "Cannot complete the tree, StartBlock must be called at least once."
     */
    @nextmethod.i18n.annotations.Key("parserContext.cannotCompleteTree.noRootBlock")
    @nextmethod.i18n.annotations.DefaultMessage("Cannot complete the tree, StartBlock must be called at least once.")
    public String parserContextCannotCompleteTreeNoRootBlock();

    /**
     * Translated "Cannot complete the tree, there are still open blocks.".
     * 
     * @return
     *     translated "Cannot complete the tree, there are still open blocks."
     */
    @nextmethod.i18n.annotations.Key("parserContext.cannotCompleteTree.outstandingBlocks")
    @nextmethod.i18n.annotations.DefaultMessage("Cannot complete the tree, there are still open blocks.")
    public String parserContextCannotCompleteTreeOutstandingBlocks();

    /**
     * Translated "Cannot finish span, there is no current block. Call StartBlock at least once before finishing a span".
     * 
     * @return
     *     translated "Cannot finish span, there is no current block. Call StartBlock at least once before finishing a span"
     */
    @nextmethod.i18n.annotations.Key("parserContext.noCurrentBlock")
    @nextmethod.i18n.annotations.DefaultMessage("Cannot finish span, there is no current block. Call StartBlock at least once before finishing a span")
    public String parserContextNoCurrentBlock();

    /**
     * Translated "Cannot complete action, the parser has finished. Only CompleteParse can be called to extract the final parser results after the parser has finished".
     * 
     * @return
     *     translated "Cannot complete action, the parser has finished. Only CompleteParse can be called to extract the final parser results after the parser has finished"
     */
    @nextmethod.i18n.annotations.Key("parserContext.parseComplete")
    @nextmethod.i18n.annotations.DefaultMessage("Cannot complete action, the parser has finished. Only CompleteParse can be called to extract the final parser results after the parser has finished")
    public String parserContextParseComplete();

    /**
     * Translated "Missing value for session state directive.".
     * 
     * @return
     *     translated "Missing value for session state directive."
     */
    @nextmethod.i18n.annotations.Key("parserError.sessionDirectiveMissingValue")
    @nextmethod.i18n.annotations.DefaultMessage("Missing value for session state directive.")
    public String parserErrorSessionDirectiveMissingValue();

    /**
     * Translated "The parser provided to the ParserContext was not a Markup Parser.".
     * 
     * @return
     *     translated "The parser provided to the ParserContext was not a Markup Parser."
     */
    @nextmethod.i18n.annotations.Key("parserIsNotAMarkupParser")
    @nextmethod.i18n.annotations.DefaultMessage("The parser provided to the ParserContext was not a Markup Parser.")
    public String parserIsNotAMarkupParser();

    /**
     * Translated "@section Header { ... }".
     * 
     * @return
     *     translated "@section Header { ... }"
     */
    @nextmethod.i18n.annotations.Key("sectionExample")
    @nextmethod.i18n.annotations.DefaultMessage("@section Header { ... }")
    public String sectionExample();

    /**
     * Translated "The {0} property of the {1} structure cannot be null.".
     * 
     * @param arg1
     * @param arg0
     * @return
     *     translated "The {0} property of the {1} structure cannot be null."
     */
    @nextmethod.i18n.annotations.Key("structure.member.cannotBeNull")
    @nextmethod.i18n.annotations.DefaultMessage("The {0} property of the {1} structure cannot be null.")
    public String structureMemberCannotBeNull(final CharSequence arg0, final CharSequence arg1);

    /**
     * Translated "The {0} property of the {1} structure cannot be null.".
     * 
     * @return
     *     translated "The {0} property of the {1} structure cannot be null."
     */
    @nextmethod.i18n.annotations.Key("structure.member.cannotBeNull")
    @nextmethod.i18n.annotations.DefaultMessage("The {0} property of the {1} structure cannot be null.")
    public String structureMemberCannotBeNull();

    /**
     * Translated "&lt;&lt;unknown>>".
     * 
     * @return
     *     translated "&lt;&lt;unknown>>"
     */
    @nextmethod.i18n.annotations.Key("symbol.unknown")
    @nextmethod.i18n.annotations.DefaultMessage("<<unknown>>")
    public String symbolUnknown();

    /**
     * Translated "Cannot resume this symbol. Only the symbol immediately preceding the current one can be resumed.".
     * 
     * @return
     *     translated "Cannot resume this symbol. Only the symbol immediately preceding the current one can be resumed."
     */
    @nextmethod.i18n.annotations.Key("tokenizer.cannotResumeSymbolUnlessIsPrevious")
    @nextmethod.i18n.annotations.DefaultMessage("Cannot resume this symbol. Only the symbol immediately preceding the current one can be resumed.")
    public String tokenizerCannotResumeSymbolUnlessIsPrevious();

    /**
     * Translated "In order to put a symbol back, it must have been the symbol which ended at the current position. The specified symbol ends at {0,number}, but the current position is {1,number}".
     * 
     * @param arg1
     * @param arg0
     * @return
     *     translated "In order to put a symbol back, it must have been the symbol which ended at the current position. The specified symbol ends at {0,number}, but the current position is {1,number}"
     */
    @nextmethod.i18n.annotations.Key("tokenizerView.cannotPutBack")
    @nextmethod.i18n.annotations.DefaultMessage("In order to put a symbol back, it must have been the symbol which ended at the current position. The specified symbol ends at {0,number}, but the current position is {1,number}")
    public String tokenizerViewCannotPutBack(final Number arg0, final Number arg1);

    /**
     * Translated "In order to put a symbol back, it must have been the symbol which ended at the current position. The specified symbol ends at {0,number}, but the current position is {1,number}".
     * 
     * @return
     *     translated "In order to put a symbol back, it must have been the symbol which ended at the current position. The specified symbol ends at {0,number}, but the current position is {1,number}"
     */
    @nextmethod.i18n.annotations.Key("tokenizerView.cannotPutBack")
    @nextmethod.i18n.annotations.DefaultMessage("In order to put a symbol back, it must have been the symbol which ended at the current position. The specified symbol ends at {0,number}, but the current position is {1,number}")
    public String tokenizerViewCannotPutBack();

    /**
     * Translated "[BG][{0}] Shutdown".
     * 
     * @param arg0
     * @return
     *     translated "[BG][{0}] Shutdown"
     */
    @nextmethod.i18n.annotations.Key("trace.backgroundThreadShutdown")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] Shutdown")
    public String traceBackgroundThreadShutdown(final CharSequence arg0);

    /**
     * Translated "[BG][{0}] Shutdown".
     * 
     * @return
     *     translated "[BG][{0}] Shutdown"
     */
    @nextmethod.i18n.annotations.Key("trace.backgroundThreadShutdown")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] Shutdown")
    public String traceBackgroundThreadShutdown();

    /**
     * Translated "[BG][{0}] Startup".
     * 
     * @param arg0
     * @return
     *     translated "[BG][{0}] Startup"
     */
    @nextmethod.i18n.annotations.Key("trace.backgroundThreadStart")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] Startup")
    public String traceBackgroundThreadStart(final CharSequence arg0);

    /**
     * Translated "[BG][{0}] Startup".
     * 
     * @return
     *     translated "[BG][{0}] Startup"
     */
    @nextmethod.i18n.annotations.Key("trace.backgroundThreadStart")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] Startup")
    public String traceBackgroundThreadStart();

    /**
     * Translated "[BG][{0}] {1} changes arrived".
     * 
     * @param arg1
     * @param arg0
     * @return
     *     translated "[BG][{0}] {1} changes arrived"
     */
    @nextmethod.i18n.annotations.Key("trace.changesArrived")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] {1} changes arrived")
    public String traceChangesArrived(final CharSequence arg0, final CharSequence arg1);

    /**
     * Translated "[BG][{0}] {1} changes arrived".
     * 
     * @return
     *     translated "[BG][{0}] {1} changes arrived"
     */
    @nextmethod.i18n.annotations.Key("trace.changesArrived")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] {1} changes arrived")
    public String traceChangesArrived();

    /**
     * Translated "[BG][{0}] Discarded {1} changes".
     * 
     * @param arg1
     * @param arg0
     * @return
     *     translated "[BG][{0}] Discarded {1} changes"
     */
    @nextmethod.i18n.annotations.Key("trace.changesDiscarded")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] Discarded {1} changes")
    public String traceChangesDiscarded(final CharSequence arg0, final CharSequence arg1);

    /**
     * Translated "[BG][{0}] Discarded {1} changes".
     * 
     * @return
     *     translated "[BG][{0}] Discarded {1} changes"
     */
    @nextmethod.i18n.annotations.Key("trace.changesDiscarded")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] Discarded {1} changes")
    public String traceChangesDiscarded();

    /**
     * Translated "[BG][{0}] Collecting {1} discarded changes".
     * 
     * @param arg1
     * @param arg0
     * @return
     *     translated "[BG][{0}] Collecting {1} discarded changes"
     */
    @nextmethod.i18n.annotations.Key("trace.collectedDiscardedChanges")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] Collecting {1} discarded changes")
    public String traceCollectedDiscardedChanges(final CharSequence arg0, final CharSequence arg1);

    /**
     * Translated "[BG][{0}] Collecting {1} discarded changes".
     * 
     * @return
     *     translated "[BG][{0}] Collecting {1} discarded changes"
     */
    @nextmethod.i18n.annotations.Key("trace.collectedDiscardedChanges")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] Collecting {1} discarded changes")
    public String traceCollectedDiscardedChanges();

    /**
     * Translated "Disabled".
     * 
     * @return
     *     translated "Disabled"
     */
    @nextmethod.i18n.annotations.Key("trace.disabled")
    @nextmethod.i18n.annotations.DefaultMessage("Disabled")
    public String traceDisabled();

    /**
     * Translated "[P][{0}] {3} Change in {2} milliseconds: {1}".
     * 
     * @param arg3
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     translated "[P][{0}] {3} Change in {2} milliseconds: {1}"
     */
    @nextmethod.i18n.annotations.Key("trace.editorProcessedChange")
    @nextmethod.i18n.annotations.DefaultMessage("[P][{0}] {3} Change in {2}: {1}")
    public String traceEditorProcessedChange(final CharSequence arg0, final CharSequence arg1, final CharSequence arg2, final CharSequence arg3);

    /**
     * Translated "[P][{0}] {3} Change in {2} milliseconds: {1}".
     * 
     * @return
     *     translated "[P][{0}] {3} Change in {2} milliseconds: {1}"
     */
    @nextmethod.i18n.annotations.Key("trace.editorProcessedChange")
    @nextmethod.i18n.annotations.DefaultMessage("[P][{0}] {3} Change in {2}: {1}")
    public String traceEditorProcessedChange();

    /**
     * Translated "[P][{0}] Received Change: {1}".
     * 
     * @param arg1
     * @param arg0
     * @return
     *     translated "[P][{0}] Received Change: {1}"
     */
    @nextmethod.i18n.annotations.Key("trace.editorReceivedChange")
    @nextmethod.i18n.annotations.DefaultMessage("[P][{0}] Received Change: {1}")
    public String traceEditorReceivedChange(final CharSequence arg0, final CharSequence arg1);

    /**
     * Translated "[P][{0}] Received Change: {1}".
     * 
     * @return
     *     translated "[P][{0}] Received Change: {1}"
     */
    @nextmethod.i18n.annotations.Key("trace.editorReceivedChange")
    @nextmethod.i18n.annotations.DefaultMessage("[P][{0}] Received Change: {1}")
    public String traceEditorReceivedChange();

    /**
     * Translated "Enabled".
     * 
     * @return
     *     translated "Enabled"
     */
    @nextmethod.i18n.annotations.Key("trace.enabled")
    @nextmethod.i18n.annotations.DefaultMessage("Enabled")
    public String traceEnabled();

    /**
     * Translated "[Razor] {0}".
     * 
     * @param arg0
     * @return
     *     translated "[Razor] {0}"
     */
    @nextmethod.i18n.annotations.Key("trace.format")
    @nextmethod.i18n.annotations.DefaultMessage("[Razor] {0}")
    public String traceFormat(final CharSequence arg0);

    /**
     * Translated "[Razor] {0}".
     * 
     * @return
     *     translated "[Razor] {0}"
     */
    @nextmethod.i18n.annotations.Key("trace.format")
    @nextmethod.i18n.annotations.DefaultMessage("[Razor] {0}")
    public String traceFormat();

    /**
     * Translated "[BG][{0}] no changes arrived?".
     * 
     * @param arg0
     * @return
     *     translated "[BG][{0}] no changes arrived?"
     */
    @nextmethod.i18n.annotations.Key("trace.noChangesArrived")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] no changes arrived?")
    public String traceNoChangesArrived(final CharSequence arg0);

    /**
     * Translated "[BG][{0}] no changes arrived?".
     * 
     * @return
     *     translated "[BG][{0}] no changes arrived?"
     */
    @nextmethod.i18n.annotations.Key("trace.noChangesArrived")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] no changes arrived?")
    public String traceNoChangesArrived();

    /**
     * Translated "[BG][{0}] Parse Complete in {1} milliseconds".
     * 
     * @param arg1
     * @param arg0
     * @return
     *     translated "[BG][{0}] Parse Complete in {1} milliseconds"
     */
    @nextmethod.i18n.annotations.Key("trace.parseComplete")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] Parse Complete in {1}")
    public String traceParseComplete(final CharSequence arg0, final CharSequence arg1);

    /**
     * Translated "[BG][{0}] Parse Complete in {1} milliseconds".
     * 
     * @return
     *     translated "[BG][{0}] Parse Complete in {1} milliseconds"
     */
    @nextmethod.i18n.annotations.Key("trace.parseComplete")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] Parse Complete in {1}")
    public String traceParseComplete();

    /**
     * Translated "[M][{0}] Queuing Parse for: {1}".
     * 
     * @param arg1
     * @param arg0
     * @return
     *     translated "[M][{0}] Queuing Parse for: {1}"
     */
    @nextmethod.i18n.annotations.Key("trace.queuingParse")
    @nextmethod.i18n.annotations.DefaultMessage("[M][{0}] Queuing Parse for: {1}")
    public String traceQueuingParse(final CharSequence arg0, final CharSequence arg1);

    /**
     * Translated "[M][{0}] Queuing Parse for: {1}".
     * 
     * @return
     *     translated "[M][{0}] Queuing Parse for: {1}"
     */
    @nextmethod.i18n.annotations.Key("trace.queuingParse")
    @nextmethod.i18n.annotations.DefaultMessage("[M][{0}] Queuing Parse for: {1}")
    public String traceQueuingParse();

    /**
     * Translated "[Razor] Editor Tracing {0}".
     * 
     * @param arg0
     * @return
     *     translated "[Razor] Editor Tracing {0}"
     */
    @nextmethod.i18n.annotations.Key("trace.startup")
    @nextmethod.i18n.annotations.DefaultMessage("[Razor] Editor Tracing {0}")
    public String traceStartup(final CharSequence arg0);

    /**
     * Translated "[Razor] Editor Tracing {0}".
     * 
     * @return
     *     translated "[Razor] Editor Tracing {0}"
     */
    @nextmethod.i18n.annotations.Key("trace.startup")
    @nextmethod.i18n.annotations.DefaultMessage("[Razor] Editor Tracing {0}")
    public String traceStartup();

    /**
     * Translated "[BG][{0}] Trees Compared in {1} milliseconds. Different = {2}".
     * 
     * @param arg2
     * @param arg1
     * @param arg0
     * @return
     *     translated "[BG][{0}] Trees Compared in {1} milliseconds. Different = {2}"
     */
    @nextmethod.i18n.annotations.Key("trace.treesCompared")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] Trees Compared in {1}. Different = {2}")
    public String traceTreesCompared(final CharSequence arg0, final CharSequence arg1, final CharSequence arg2);

    /**
     * Translated "[BG][{0}] Trees Compared in {1} milliseconds. Different = {2}".
     * 
     * @return
     *     translated "[BG][{0}] Trees Compared in {1} milliseconds. Different = {2}"
     */
    @nextmethod.i18n.annotations.Key("trace.treesCompared")
    @nextmethod.i18n.annotations.DefaultMessage("[BG][{0}] Trees Compared in {1}. Different = {2}")
    public String traceTreesCompared();

}
