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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialAssignment;

public class JavaCodeGeneratorTypeSupplier extends ParameterSupplier {

    private static String[] javaCodeGeneratorTestTypes = {
        "NestedCodeBlocks",
        "CodeBlock",
        "ExplicitExpression",
        "MarkupInCodeBlock",
        "Blocks",
        "ImplicitExpression",
        "Imports",
        "ExpressionsInCode",
        "FunctionsBlock",
        "Templates",
        "Sections",
        "RazorComments",
        "Helpers",
        "HelpersMissingCloseParen",
        "HelpersMissingOpenBrace",
        "HelpersMissingOpenParen",
        "NestedHelpers",
        "InlineBlocks",
        "LayoutDirective",
        "ConditionalAttributes",
        "ResolveUrl"
    };

    @Override
    public List<PotentialAssignment> getValueSources(final ParameterSignature sig) {
        return Arrays.stream(javaCodeGeneratorTestTypes)
            .map(x -> PotentialAssignment.forValue(x, x))
            .collect(Collectors.toList());
    }
}
