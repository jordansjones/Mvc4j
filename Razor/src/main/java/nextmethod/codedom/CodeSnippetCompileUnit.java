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
public class CodeSnippetCompileUnit extends CodeCompileUnit implements Serializable {

	private static final long serialVersionUID = 7451302001198865681L;

	private CodeLinePragma linePragma;
	private String value;

	public CodeSnippetCompileUnit() {
	}

	public CodeSnippetCompileUnit(final String value) {
		this.value = value;
	}

	public CodeLinePragma getLinePragma() {
		return linePragma;
	}

	public void setLinePragma(final CodeLinePragma linePragma) {
		this.linePragma = linePragma;
	}

	public String getValue() {
		return value == null ? Strings.Empty : value;
	}

	public void setValue(final String value) {
		this.value = value;
	}
}
