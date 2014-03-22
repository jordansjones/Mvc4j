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

import nextmethod.base.OutParam;
import nextmethod.base.Strings;
import nextmethod.web.razor.RazorEngineHost;
import nextmethod.web.razor.parser.SyntaxConstants;
import nextmethod.web.razor.parser.syntaxtree.Span;
import nextmethod.web.razor.parser.syntaxtree.SpanKind;

import static com.google.common.base.Preconditions.checkNotNull;

final class CodeGeneratorPaddingHelper {

    private static final char[] newLineChars = {'\r', '\n'};

    // TODO
    public static int paddingCharCount(final RazorEngineHost host, final Span target, final int generatedStart) {
        return 0;
    }

    public static String padStatement(final RazorEngineHost host, final String code, final Span target,
                                      final OutParam<Integer> startGeneratedCode,
                                      final OutParam<Integer> paddingCharCount
                                     ) {
        checkNotNull(host);
        checkNotNull(target);

        int padding = calculatePadding(host, target, 0);
        // Treat statement padding specially so for brace positioning, so that in the following example:
        //  @if (foo > 0)
        //  {
        //  }
        //
        // the braces shows up under the @ rather than under the if.
        if (host.isDesignTimeMode()
            && padding > 0
            && target.getPrevious().getKind() == SpanKind.Transition
            && SyntaxConstants.TransitionString.equals(target.getPrevious().getContent())) {
            padding--;
            startGeneratedCode.set(startGeneratedCode.value() - 1);
        }
        return padInteral(host, code, padding, paddingCharCount);
    }

    public static int calculatePadding(final RazorEngineHost host, final Span target, final int generatedStart) {
        checkNotNull(host);
        checkNotNull(target);

        int padding = collectSpacesAndTabs(target, host.getTabSize()) - generatedStart;

        if (padding < 0) {
            padding = 0;
        }
        return padding;
    }

    private static int collectSpacesAndTabs(final Span target, final int tabSize) {
        Span firstSpanInLine = target;

        String currentContent = null;

        while (firstSpanInLine.getPrevious() != null) {
            // When scanning previous spans we need to break down the spans with spaces because the parser doesn't.
            // For example, a span looking line \n\n\t needs to be broken down, and we should just brab the \t.
            String previousContent = firstSpanInLine.getPrevious().getContent();
            previousContent = previousContent == null
                              ? Strings.Empty
                              : previousContent;

            int lastNewLineIndex = Strings.lastIndexOfAny(previousContent, newLineChars);
            if (lastNewLineIndex < 0) {
                firstSpanInLine = firstSpanInLine.getPrevious();
            }
            else {
                if (lastNewLineIndex != previousContent.length() - 1) {
                    firstSpanInLine = firstSpanInLine.getPrevious();
                    currentContent = previousContent.substring(lastNewLineIndex + 1);
                }
                break;
            }

        }

        // We need to walk from the beginning of the line, because space + tab(tabSize) = tabSize columns, but tab(tabSize) + space = tabSize + 1 columns.
        Span currentSpanInLine = firstSpanInLine;

        if (currentContent == null) {
            currentContent = currentSpanInLine.getContent();
        }

        int padding = 0;
        while (currentSpanInLine != target) {
            if (currentContent != null) {
                for (int i = 0; i < currentContent.length(); i++) {
                    if (currentContent.charAt(i) == '\t') {
                        // Example
                        // <space><space><tab><tab>:
                        // iter 1) 1
                        // iter 2) 2
                        // iter 3) 4 = 2 + (4 - 2)
                        // iter 4) 8 = 4 + (4 - 0)
                        padding = padding + (tabSize - (padding % tabSize));
                    }
                    else {
                        padding++;
                    }
                }
            }

            currentSpanInLine = currentSpanInLine.getNext();
            currentContent = currentSpanInLine.getContent();
        }
        return padding;
    }

    private static String padInteral(final RazorEngineHost host, final String code, final int padding,
                                     final OutParam<Integer> paddingCharCount
                                    ) {
        if (host.isDesignTimeMode() && host.isIndentingWithTabs()) {
            final OutParam<Integer> spaces = OutParam.of(0);
            int tabs = divRem(padding, host.getTabSize(), spaces);

            paddingCharCount.set(tabs + spaces.value());
            return Strings.repeat("\t", tabs) + Strings.repeat(" ", spaces.value()) + code;
        }
        else {
            paddingCharCount.set(padding);
            return Strings.padStart(code, padding + code.length(), ' ');
        }
    }


    private static int divRem(final int a, final int b, final OutParam<Integer> result) {
        result.set(a % b);
        return a / b;
    }

}
