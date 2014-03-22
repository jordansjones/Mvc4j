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

package nextmethod.web.razor.generator;

import javax.annotation.Nonnull;

import com.google.common.collect.Iterables;
import nextmethod.base.Delegates;
import nextmethod.base.Strings;
import nextmethod.codedom.CodeConstructor;
import nextmethod.codedom.CodePackageImport;
import nextmethod.codedom.CodePackageImportCollection;
import nextmethod.codedom.CodeTypeReference;
import nextmethod.codedom.MemberAttributes;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.generator.internal.CodeWriter;
import nextmethod.web.razor.parser.ParserVisitor;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.parser.syntaxtree.Span;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.common.Mvc4jCommonResources.CommonResources;

public abstract class RazorCodeGenerator extends ParserVisitor {

    private CodeGeneratorContext context;

    // Data pull from constructor
    private final String className;
    private final String rootPackageName;
    private final String sourceFileName;
    private final RazorEngineHost host;

    // Generation Settings
    private boolean generateLinePragmas;
    private boolean designTimeMode;

    protected RazorCodeGenerator(@Nonnull final String className, @Nonnull final String rootPackageName,
                                 @Nonnull final String sourceFileName, @Nonnull final RazorEngineHost host
                                ) {
        checkArgument(!Strings.isNullOrEmpty(className), CommonResources().argumentCannotBeNullOrEmpty());
        this.className = className;
        this.rootPackageName = checkNotNull(rootPackageName);
        this.sourceFileName = sourceFileName;
        this.host = checkNotNull(host);
        this.generateLinePragmas = !Strings.isNullOrEmpty(sourceFileName);
    }

    protected Delegates.IFunc<CodeWriter> getCodeWriterFactory() {
        return null;
    }

    @Override
    public void visitStartBlock(@Nonnull final Block block) {
        checkNotNull(block).getCodeGenerator().generateStartBlockCode(block, getContext());
    }

    @Override
    public void visitEndBlock(@Nonnull final Block block) {
        checkNotNull(block).getCodeGenerator().generateEndBlockCode(block, getContext());
    }

    @Override
    public void visitSpan(@Nonnull final Span span) {
        checkNotNull(span).getCodeGenerator().generateCode(span, getContext());
    }

    @Override
    public void onComplete() {
        context.flushBufferedStatement();
    }

    private void ensureContextInitialized() {
        if (context == null) {
            context = CodeGeneratorContext.create(
                                                     host, getCodeWriterFactory(), className, rootPackageName,
                                                     sourceFileName, generateLinePragmas
                                                 );
            initialize(context);
        }
    }

    protected void initialize(@Nonnull final CodeGeneratorContext context) {
        final CodePackageImportCollection packageImports = context.getCodePackage().getImports();
        final Iterable<CodePackageImport> imports = Iterables.transform(
                                                                           host.getPackageImports(),
                                                                           input -> Strings.isNullOrEmpty(input)
                                                                                    ? null
                                                                                    : new CodePackageImport(input)
                                                                       );
        imports.forEach(packageImports::add);

        if (!Strings.isNullOrEmpty(host.getDefaultBaseClass())) {
            context.getGeneratedClass().getBaseTypes().add(new CodeTypeReference(host.getDefaultBaseClass()));
        }
        // Generate explicit parameter-less constructor on Razor generated class
        final CodeConstructor codeConstructor = new CodeConstructor();
        codeConstructor.setAttributes(MemberAttributes.Public);
        context.getGeneratedClass().getMembers().add(codeConstructor);
    }

    public CodeGeneratorContext getContext() {
        ensureContextInitialized();
        return context;
    }

    public String getClassName() {
        return className;
    }

    public String getRootPackageName() {
        return rootPackageName;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public RazorEngineHost getHost() {
        return host;
    }

    public boolean isGenerateLinePragmas() {
        return generateLinePragmas;
    }

    public void setGenerateLinePragmas(final boolean generateLinePragmas) {
        this.generateLinePragmas = generateLinePragmas;
    }

    public boolean isDesignTimeMode() {
        return designTimeMode;
    }

    public void setDesignTimeMode(final boolean designTimeMode) {
        this.designTimeMode = designTimeMode;
    }
}
