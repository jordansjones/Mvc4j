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

import nextmethod.base.Strings;

import java.io.Serializable;

/**
 *
 */
public class CodeComment extends CodeObject implements Serializable {

	private static final long serialVersionUID = -9157799393489664181L;

	private boolean docComment;
	private String text;

	public CodeComment() {
	}

	public CodeComment(final String text) {
		this.text = text;
	}

	public CodeComment(final String text, final boolean docComment) {
		this.text = text;
		this.docComment = docComment;
	}

	public boolean isDocComment() {
		return docComment;
	}

	public void setDocComment(final boolean docComment) {
		this.docComment = docComment;
	}

	public String getText() {
		return text == null ? Strings.Empty : text;
	}

	public void setText(final String text) {
		this.text = text;
	}
}
