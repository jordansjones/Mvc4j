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
import java.util.Optional;
import javax.annotation.Nonnull;

import com.google.common.collect.Iterables;
import nextmethod.base.Strings;
import nextmethod.codedom.CodePackage;
import nextmethod.codedom.CodePackageImport;
import nextmethod.codedom.CodePackageImportCollection;
import nextmethod.web.razor.parser.syntaxtree.Span;

import static nextmethod.base.TypeHelpers.typeAs;

public class AddImportCodeGenerator extends SpanCodeGenerator {

    private final String pkg;
    private int keywordLength;

    public AddImportCodeGenerator(@Nonnull final String pkg, final int keywordLength) {
        this.pkg = pkg;
        this.keywordLength = keywordLength;
    }

    @Override
    public void generateCode(@Nonnull Span target, @Nonnull CodeGeneratorContext context) {
        // Try to find the package in the existing imports
        String pkg = this.pkg;
        if (!Strings.isNullOrEmpty(pkg) && Character.isWhitespace(pkg.charAt(0))) {
            pkg = pkg.substring(1);
        }
        final String packageName = pkg;

        final CodePackageImportCollection importCollection = context.getCodePackage().getImports();
        final Iterable<CodePackageImport> imports = Iterables.filter(
                                                                        importCollection, input -> input != null &&
                                                                                                   packageName.trim()
                                                                                                              .equalsIgnoreCase(
                                                                                                                                   input
                                                                                                                                       .getPackage()
                                                                                                                               )
                                                                    );

        CodePackageImport packageImport = Iterables.getFirst(imports, null);

        if (packageImport == null) {
            packageImport = new CodePackageImport(packageName);
            context.getCodePackage().getImports().add(packageImport);
        }

        final String importPackage = pkg;

        final CodePackage imprt = context.getCodePackage();
        final Optional<CodePackageImport> importOptional = imprt.getImports()
                                                                .stream()
                                                                .filter(
                                                                           x -> Strings.nullToEmpty(x.getPackage())
                                                                                       .equals(importPackage.trim())
                                                                       )
                                                                .findFirst();

        final CodePackageImport codePackageImport;
        if (!importOptional.isPresent()) {
            codePackageImport = new CodePackageImport(importPackage);
            context.getCodePackage().getImports().add(codePackageImport);
        }
        else {
            codePackageImport = importOptional.get();
        }

        codePackageImport.setLinePragma(context.generateLinePragma(target));
    }

    public String getPackage() {
        return pkg;
    }

    public int getPackageKeywordLength() {
        return keywordLength;
    }

    public void setPackageKeywordLength(int keywordLength) {
        this.keywordLength = keywordLength;
    }

    @Override
    public String toString() {
        return "Import:" + pkg + ";KwdLen:" + keywordLength;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        final AddImportCodeGenerator other = typeAs(obj, AddImportCodeGenerator.class);
        return other != null && Objects.equals(pkg, other.pkg) && keywordLength == other.keywordLength;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkg, keywordLength);
    }
}
