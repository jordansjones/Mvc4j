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

package nextmethod.codedom.compiler;


import nextmethod.base.SystemHelpers;

public class CodeGeneratorOptions {

    private boolean blankLinesBetweenMembers;
    private String bracingStyle;
    private boolean elseOnClosingProperty;
    private String indentString;
    private boolean verbatimOrder;
    private String newlineString = SystemHelpers.newLine();

    public boolean isBlankLinesBetweenMembers() {
        return blankLinesBetweenMembers;
    }

    public void setBlankLinesBetweenMembers(final boolean blankLinesBetweenMembers) {
        this.blankLinesBetweenMembers = blankLinesBetweenMembers;
    }

    public String getBracingStyle() {
        return bracingStyle;
    }

    public void setBracingStyle(final String bracingStyle) {
        this.bracingStyle = bracingStyle;
    }

    public boolean isElseOnClosingProperty() {
        return elseOnClosingProperty;
    }

    public void setElseOnClosingProperty(final boolean elseOnClosingProperty) {
        this.elseOnClosingProperty = elseOnClosingProperty;
    }

    public String getIndentString() {
        return indentString;
    }

    public void setIndentString(final String indentString) {
        this.indentString = indentString;
    }

    public boolean isVerbatimOrder() {
        return verbatimOrder;
    }

    public void setVerbatimOrder(final boolean verbatimOrder) {
        this.verbatimOrder = verbatimOrder;
    }

    public String getNewlineString() {
        return newlineString;
    }

    public void setNewlineString(final String newlineString) {
        this.newlineString = newlineString;
    }
}
