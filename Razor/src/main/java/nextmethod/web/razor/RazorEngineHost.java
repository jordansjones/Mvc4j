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

package nextmethod.web.razor;

import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;

import com.google.common.collect.Sets;
import nextmethod.base.Delegates;
import nextmethod.base.Strings;
import nextmethod.codedom.CodeCompileUnit;
import nextmethod.codedom.CodeMemberMethod;
import nextmethod.codedom.CodePackage;
import nextmethod.codedom.CodeTypeDeclaration;
import nextmethod.web.razor.generator.CodeGeneratorContext;
import nextmethod.web.razor.generator.GeneratedClassContext;
import nextmethod.web.razor.generator.RazorCodeGenerator;
import nextmethod.web.razor.parser.HtmlMarkupParser;
import nextmethod.web.razor.parser.ParserBase;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Defines the environment in which a Razor template will live
 * <p>
 * The host defines the following things:
 * <ul>
 * <li>What method names will be used for rendering markup, expressions etc. For example "write", "writeLiteral"</li>
 * <li>The namespace/package imports to be added to every page generated via this host</li>
 * <li>The default Base Class to inherit the generated class from</li>
 * <li>The markup, code parsers and code generators to use (the system will select defaults, but a Host gets a chance to augment them) ** See decorateNNN methods</li>
 * <li>Addional code to add to the generated code ({@link #postProcessGeneratedCode})</li>
 * </ul>
 * </p>
 */
public class RazorEngineHost {

    static final String InternalDefaultClassName = "__CompiledTemplate";
    static final String InternalDefaultPackage = "Razor";

    protected boolean designTimeMode;
    protected boolean instrumentationActive;
    protected GeneratedClassContext generatedClassContext;
    protected String instrumentedSourceFilePath = Strings.Empty;
    protected final Set<String> packageImports;
    protected String defaultBaseClass;
    protected String defaultPackage;
    protected String defaultClassName;
    protected boolean staticHelpers;
    protected boolean indentingWithTabs;

    private RazorCodeLanguage<?> codeLanguage;
    private Delegates.IFunc<ParserBase> markupParserFactory;


    private int tabSize = 4;

    protected RazorEngineHost() {
        this.generatedClassContext = GeneratedClassContext.Default;
        this.packageImports = Sets.newHashSet();
        this.designTimeMode = false;
        this.defaultPackage = InternalDefaultPackage;
        this.defaultClassName = InternalDefaultClassName;
        this.enableInstrumentation(false);
    }

    /**
     * Creates a host which uses the specified code language and the HTML markup language
     *
     * @param codeLanguage the coe language to use
     */
    public RazorEngineHost(@Nonnull final RazorCodeLanguage<?> codeLanguage) {
        this(codeLanguage, HtmlMarkupParser::new);
    }

    public RazorEngineHost(@Nonnull final RazorCodeLanguage<?> codeLanguage,
                           @Nonnull final Delegates.IFunc<ParserBase> markupParserFactory
                          ) {
        this();
        this.setCodeLanguage(codeLanguage);
        this.markupParserFactory = checkNotNull(markupParserFactory, "markupParserFactory");
    }

    /**
     * Constructs the markup parser. Must return a new instance on EVERY call to ensure thread-safety
     *
     * @return markup parser or null
     */
    public ParserBase createMarkupParser() {
        if (markupParserFactory != null) {
            return markupParserFactory.invoke();
        }
        return null;
    }

    /**
     * Gets an instance of the code parser and is provided an opportunity to decorate or replace it
     *
     * @param incomingCodeParser the code parser
     *
     * @return either the same code parser, after modifications, or a different code parser
     */
    public ParserBase decorateCodeParser(@Nonnull final ParserBase incomingCodeParser) {
        return checkNotNull(incomingCodeParser, "incomingCodeParser");
    }

    /**
     * Gets an instance of the markup parser and is provided an opportunity to decorate or replace it
     *
     * @param incomingMarkupParser the markup parser
     *
     * @return either the same markup parser, after modifications, or a different markup parser
     */
    public ParserBase decorateMarkupParser(@Nonnull final ParserBase incomingMarkupParser) {
        return checkNotNull(incomingMarkupParser, "incomingMarkupParser");
    }

    /**
     * Gets an instance of the code generator and is provided an opportunity to decorate or replace it
     *
     * @param incomingCodeGenerator the code generator
     *
     * @return either the same code generator, after modifications, or a different code generator
     */
    public RazorCodeGenerator decorateCodeGenerator(@Nonnull final RazorCodeGenerator incomingCodeGenerator) {
        return checkNotNull(incomingCodeGenerator, "incomingCodeGenerator");
    }

    /**
     * Gets the important CodeDOM nodes generated by the code generator and has a chance to add to them.
     * <p>
     * All the other parameter values can be located by traversing tree in the codeCompileUnit node,
     * they are simply provided for convenience
     * </p>
     *
     * @param context the current {@link CodeGeneratorContext}
     */
    public void postProcessGeneratedCode(@Nonnull final CodeGeneratorContext context) {
        postProcessGeneratedCode(
                                    context.getCompileUnit(),
                                    context.getCodePackage(),
                                    context.getGeneratedClass(),
                                    context.getTargetMethod()
                                );
    }

    public void postProcessGeneratedCode(@Nonnull final CodeCompileUnit codeCompileUnit,
                                         @Nonnull final CodePackage generatedPackage,
                                         @Nonnull final CodeTypeDeclaration generatedClass,
                                         @Nonnull final CodeMemberMethod executeMethod
                                        ) {
        checkNotNull(codeCompileUnit, "codeCompileUnit");
        checkNotNull(generatedPackage, "generatedPackage");
        checkNotNull(generatedClass, "generatedClass");
        checkNotNull(executeMethod, "executeMethod");
    }


    public RazorCodeLanguage<?> getCodeLanguage() {
        return codeLanguage;
    }

    protected void setCodeLanguage(@Nonnull final RazorCodeLanguage<?> codeLanguage) {
        this.codeLanguage = checkNotNull(codeLanguage, "codeLanguage");
    }

    public boolean isDesignTimeMode() {
        return designTimeMode;
    }

    public void setDesignTimeMode(boolean designTimeMode) {
        this.designTimeMode = designTimeMode;
    }

    public boolean isInstrumentationActive() {
        return !isDesignTimeMode() && instrumentationActive;
    }

    public void enableInstrumentation(final boolean setActive) {
        this.instrumentationActive = setActive;
    }

    public GeneratedClassContext getGeneratedClassContext() {
        return generatedClassContext;
    }

    public void setGeneratedClassContext(@Nonnull final GeneratedClassContext generatedClassContext) {
        this.generatedClassContext = generatedClassContext;
    }

    public String getInstrumentedSourceFilePath() {
        return instrumentedSourceFilePath;
    }

    public void setInstrumentedSourceFilePath(final String instrumentedSourceFilePath) {
        this.instrumentedSourceFilePath = instrumentedSourceFilePath;
    }

    public Collection<String> getPackageImports() {
        return packageImports;
    }

    public String getDefaultBaseClass() {
        return defaultBaseClass;
    }

    public void setDefaultBaseClass(final String defaultBaseClass) {
        this.defaultBaseClass = defaultBaseClass;
    }

    public String getDefaultClassName() {
        return defaultClassName;
    }

    public void setDefaultClassName(final String defaultClassName) {
        this.defaultClassName = defaultClassName;
    }

    public String getDefaultPackage() {
        return defaultPackage;
    }

    public void setDefaultPackage(final String defaultPackage) {
        this.defaultPackage = defaultPackage;
    }

    public boolean isStaticHelpers() {
        return staticHelpers;
    }

    public void setStaticHelpers(final boolean staticHelpers) {
        this.staticHelpers = staticHelpers;
    }

    public boolean isIndentingWithTabs() {
        return indentingWithTabs;
    }

    public void setIndentingWithTabs(final boolean indentingWithTabs) {
        this.indentingWithTabs = indentingWithTabs;
    }

    public int getTabSize() {
        return tabSize;
    }

    public void setTabSize(final int tabSize) {
        this.tabSize = Math.max(tabSize, 1);
    }
}
