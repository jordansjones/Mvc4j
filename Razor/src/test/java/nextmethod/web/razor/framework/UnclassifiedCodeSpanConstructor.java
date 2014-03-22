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

package nextmethod.web.razor.framework;

import java.util.Set;
import javax.annotation.Nonnull;

import nextmethod.base.NotImplementedException;
import nextmethod.web.razor.editor.ImplicitExpressionEditorHandler;
import nextmethod.web.razor.generator.AddImportCodeGenerator;
import nextmethod.web.razor.generator.ExpressionCodeGenerator;
import nextmethod.web.razor.generator.ISpanCodeGenerator;
import nextmethod.web.razor.generator.RazorDirectiveAnnotationCodeGenerator;
import nextmethod.web.razor.generator.SetBaseTypeCodeGenerator;
import nextmethod.web.razor.generator.SpanCodeGenerator;
import nextmethod.web.razor.generator.StatementCodeGenerator;
import nextmethod.web.razor.generator.TypeMemberCodeGenerator;
import nextmethod.web.razor.parser.ParserVisitor;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;
import nextmethod.web.razor.parser.syntaxtree.SyntaxTreeNode;
import nextmethod.web.razor.text.SourceLocation;

public class UnclassifiedCodeSpanConstructor extends SyntaxTreeNode implements ISpanConstructor {

    final SpanConstructor self;

    public UnclassifiedCodeSpanConstructor(SpanConstructor self) {
        this.self = self;
    }

    public SpanConstructor asMetaCode() {
        self.getBuilder().setKind(SpanKind.MetaCode);
        return self;
    }

    public SpanConstructor asStatement() {
        return self.with(new StatementCodeGenerator());
    }

    public SpanConstructor asExpression() {
        return self.with(new ExpressionCodeGenerator());
    }

    public SpanConstructor asImplicitExpression(final Set<String> keywords) {
        return asImplicitExpression(keywords, false);
    }

    public SpanConstructor asImplicitExpression(final Set<String> keywords, final boolean acceptTrailingDot) {
        return self.with(
                            new ImplicitExpressionEditorHandler(
                                                                   SpanConstructor.testTokenizer, keywords,
                                                                   acceptTrailingDot
                            )
                        ).with(new ExpressionCodeGenerator());
    }

    public SpanConstructor asFunctionsBody() {
        return self.with(new TypeMemberCodeGenerator());
    }

    public SpanConstructor asPackageImport(final String pkg, final int pkgKeywordLength) {
        return self.with(new AddImportCodeGenerator(pkg, pkgKeywordLength));
    }

    public SpanConstructor asNamespaceImport(final String pkg, final int pkgKeywordLength) {
        return asPackageImport(pkg, pkgKeywordLength);
    }

    public SpanConstructor hidden() {
        return self.with(SpanCodeGenerator.Null);
    }

    public SpanConstructor asBaseType(final String baseType) {
        return self.with(new SetBaseTypeCodeGenerator(baseType));
    }

    public SpanConstructor asRazorDirectiveAnnotation(final String key, final String value) {
        return self.with(new RazorDirectiveAnnotationCodeGenerator(key, value));
    }

    public SpanConstructor as(final ISpanCodeGenerator codeGen) {
        return self.with(codeGen);
    }

    @Override
    public Span build() {
        return self.build();
    }

    @Override
    public boolean isBlock() {
        throw new NotImplementedException();
    }

    @Override
    public int getLength() {
        throw new NotImplementedException();
    }

    @Override
    public SourceLocation getStart() {
        throw new NotImplementedException();
    }

    @Override
    public void accept(@Nonnull final ParserVisitor visitor) {
        throw new NotImplementedException();
    }

    @Override
    public boolean equivalentTo(@Nonnull final SyntaxTreeNode node) {
        throw new NotImplementedException();
    }
}
