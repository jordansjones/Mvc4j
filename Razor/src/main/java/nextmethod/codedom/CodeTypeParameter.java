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

public class CodeTypeParameter extends CodeObject implements Serializable {

	private static final long serialVersionUID = -3281627702147597031L;

	CodeTypeReferenceCollection constraints;
	CodeAnnotationDeclarationCollection customAnnotations;
	boolean hasConstructorConstraint;
	String name;

	public CodeTypeParameter() {
	}

	public CodeTypeParameter(final String name) {
		this.name = name;
	}

	public CodeTypeReferenceCollection getConstraints() {
		if (constraints == null) {
			constraints = new CodeTypeReferenceCollection();
		}
		return constraints;
	}

	public CodeAnnotationDeclarationCollection getCustomAnnotations() {
		if (customAnnotations == null) {
			customAnnotations = new CodeAnnotationDeclarationCollection();
		}
		return customAnnotations;
	}

	public boolean hasConstructorConstraint() {
		return hasConstructorConstraint;
	}

	public void setHasConstructorConstraint(final boolean hasConstructorConstraint) {
		this.hasConstructorConstraint = hasConstructorConstraint;
	}

	public String getName() {
		return name == null ? Strings.Empty : name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
