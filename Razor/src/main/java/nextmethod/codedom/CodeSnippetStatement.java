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

public class CodeSnippetStatement extends CodeStatement implements Serializable {

	private static final long serialVersionUID = -3520176438251317371L;

	private String value;

	public CodeSnippetStatement() {
	}

	public CodeSnippetStatement(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value == null ? Strings.Empty : value;
	}

	public void setValue(final String value) {
		this.value = value;
	}
}
