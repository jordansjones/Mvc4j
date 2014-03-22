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

package nextmethod.codedom;

import nextmethod.base.Strings;

public class CodeTypeMember extends CodeObject {

    private String name;
    private MemberAttributes attributes;
    private CodeCommentStatementCollection comments;
    private CodeAnnotationDeclarationCollection customAnnotations;
    private CodeLinePragma linePragma;
    CodeDirectiveCollection endDirectives;
    CodeDirectiveCollection startDirectives;

    public CodeTypeMember() {
        this.attributes = MemberAttributes.valueOf(MemberAttributes.Private.val | MemberAttributes.Final.val);
    }

    public MemberAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(final MemberAttributes value) {
        attributes = value;
    }

    public CodeCommentStatementCollection getComments() {
        if (comments == null) {
            comments = new CodeCommentStatementCollection();
        }
        return comments;
    }

    public String getName() {
        return name == null
               ? Strings.Empty
               : name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public CodeLinePragma getLinePragma() {
        return linePragma;
    }

    public void setLinePragma(final CodeLinePragma linePragma) {
        this.linePragma = linePragma;
    }

    public CodeAnnotationDeclarationCollection getCustomAnnotations() {
        if (customAnnotations == null) {
            customAnnotations = new CodeAnnotationDeclarationCollection();
        }
        return customAnnotations;
    }

    public void setCustomAnnotations(final CodeAnnotationDeclarationCollection customAnnotations) {
        this.customAnnotations = customAnnotations;
    }

    public CodeDirectiveCollection getEndDirectives() {
        if (endDirectives == null) {
            endDirectives = new CodeDirectiveCollection();
        }
        return endDirectives;
    }

    public CodeDirectiveCollection getStartDirectives() {
        if (startDirectives == null) {
            startDirectives = new CodeDirectiveCollection();
        }
        return startDirectives;
    }
}
