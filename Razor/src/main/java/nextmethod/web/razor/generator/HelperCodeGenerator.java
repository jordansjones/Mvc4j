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

import java.util.Objects;
import javax.annotation.Nonnull;

import nextmethod.base.Delegates;
import nextmethod.base.IDisposable;
import nextmethod.base.Strings;
import nextmethod.codedom.CodeLinePragma;
import nextmethod.codedom.CodeSnippetTypeMember;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.generator.internal.CodeWriter;
import nextmethod.web.razor.parser.syntaxtree.Block;
import nextmethod.web.razor.text.LocationTagged;

import static nextmethod.base.TypeHelpers.typeAs;

public class HelperCodeGenerator extends BlockCodeGenerator {

    private static final String HelperWriterName = "__razor_helper_writer";

    private final LocationTagged<String> signature;

    private LocationTagged<String> footer;
    private boolean headerComplete;

    private CodeWriter writer;
    private String oldWriter;
    private IDisposable statementCollectorToken;

    public HelperCodeGenerator(@Nonnull final LocationTagged<String> signature, final boolean headerComplete) {
        this.signature = signature;
        this.headerComplete = headerComplete;
    }

    @Override
    public void generateStartBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
        writer = context.createCodeWriter();
        final RazorEngineHost host = context.getHost();
        final GeneratedClassContext generatedClassContext = host.getGeneratedClassContext();

        final String prefix = context.buildCodeString(
            input -> {
                assert input != null;

                input.writeHelperHeaderPrefix(
                    generatedClassContext.getTemplateTypeName(),
                    host.isStaticHelpers()
                );
            }
        );

        writer.writeLinePragma(
            context.generateLinePragma(
                signature.getLocation(),
                prefix.length(),
                signature.getValue().length()
            )
        );
        writer.writeSnippet(prefix);
        writer.writeSnippet(signature.getValue());
        if (headerComplete) {
            writer.writeHelperHeaderSuffix(generatedClassContext.getTemplateTypeName());
        }
        writer.writeLinePragma(null);
        if (headerComplete) {
            writer.writeReturn();
            writer.writeStartConstructor(generatedClassContext.getTemplateTypeName());
            writer.writeStartLambdaDelegate(HelperWriterName);
        }

        statementCollectorToken = context.changeStatementCollection(addStatementToHelperAction);
        oldWriter = context.getTargetWriterName();
        context.setTargetWriterName(HelperWriterName);
    }

    @Override
    public void generateEndBlockCode(@Nonnull final Block target, @Nonnull final CodeGeneratorContext context) {
        statementCollectorToken.close();
        if (headerComplete) {
            writer.writeEndLambdaDelegate();
            writer.writeEndConstructor();
            writer.writeEndStatement();
        }

        if (footer != null && !Strings.isNullOrEmpty(footer.getValue())) {
            writer.writeLinePragma(context.generateLinePragma(footer.getLocation(), 0, footer.getValue().length()));
            writer.writeSnippet(footer.getValue());
            writer.writeLinePragma();
        }
        writer.writeHelperTrailer();
        context.getGeneratedClass().getMembers().add(new CodeSnippetTypeMember(writer.getContent()));
        context.setTargetWriterName(oldWriter);
    }

    private Delegates.IAction2<String, CodeLinePragma> addStatementToHelperAction = (statement, pragma) -> {
        assert statement != null;

        if (pragma != null) {
            writer.writeLinePragma(pragma);
        }
        writer.writeSnippet(statement);
        writer.writeLine(); // CodeDOM normally inserts an extra line so we need to do so here.
        if (pragma != null) {
            writer.writeLinePragma();
        }
    };

    public LocationTagged<String> getSignature() {
        return signature;
    }

    public boolean isHeaderComplete() {
        return headerComplete;
    }

    public LocationTagged<String> getFooter() {
        return footer;
    }

    public void setFooter(final LocationTagged<String> footer) {
        this.footer = footer;
    }

    @Override
    public String toString() {
        return String.format(
            "Helper:%s;%s", signature, (headerComplete
                                        ? "C"
                                        : "I")
        );
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object obj) {
        final HelperCodeGenerator other = typeAs(obj, HelperCodeGenerator.class);
        return other != null
               && super.equals(other)
               && headerComplete == other.headerComplete
               && signature.equals(other.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            super.hashCode(),
            signature
        );
    }
}
