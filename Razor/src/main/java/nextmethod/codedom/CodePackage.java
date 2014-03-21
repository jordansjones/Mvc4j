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

// TODO
public class CodePackage extends CodeObject implements Serializable {

	private static final long serialVersionUID = 7922125490921546204L;

	private CodeCommentStatementCollection comments;
	private CodePackageImportCollection imports;
	private CodeTypeDeclarationCollection classes;
	private String name;

	public CodePackage() {
	}

	public CodePackage(final String name) {
		this.name = name;
	}

	public CodeCommentStatementCollection getComments() {
		if (comments == null) {
			comments = new CodeCommentStatementCollection();
			// TODO: PopulateComments Event
		}
		return comments;
	}

	public CodePackageImportCollection getImports() {
		if (imports == null) {
			imports = new CodePackageImportCollection();
			// TODO: PopulateImports Event
		}
		return imports;
	}

	public CodeTypeDeclarationCollection getTypes() {
		if (classes == null) {
			classes = new CodeTypeDeclarationCollection();
			// TODO: PopulateTypes Event
		}
		return classes;
	}

	public String getName() {
		return name == null ? Strings.Empty : name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
