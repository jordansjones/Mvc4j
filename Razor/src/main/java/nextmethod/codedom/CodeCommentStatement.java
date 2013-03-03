/*
 * Copyright 2013 Jordan S. Jones <jordansjones@gmail.com>
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

import nextmethod.annotations.Internal;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 *
 */
public class CodeCommentStatement extends CodeStatement implements Serializable {

	private static final long serialVersionUID = 7771477564329434234L;

	private CodeComment comment;

	public CodeCommentStatement() {
	}

	public CodeCommentStatement(final CodeComment comment) {
		this.comment = comment;
	}

	public CodeCommentStatement(final String text) {
		this(new CodeComment(text));
	}

	public CodeCommentStatement(final String text, final boolean docComment) {
		this(new CodeComment(text, docComment));
	}

	public CodeComment getComment() {
		return comment;
	}

	public void setComment(final CodeComment comment) {
		this.comment = comment;
	}

	@Override
	@Internal
	public void accept(@Nonnull final ICodeDomVisitor visitor) {
		visitor.visit(this);
	}
}
