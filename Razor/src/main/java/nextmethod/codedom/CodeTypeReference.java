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

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

// TODO
public class CodeTypeReference extends CodeObject implements Serializable {

	private static final long serialVersionUID = 7348697595800892347L;

	private String baseType;

	public CodeTypeReference() {
	}

	public CodeTypeReference(final String baseType) {
		parse(baseType);
	}

	public CodeTypeReference(final Class<?> baseType) {
		checkNotNull(baseType);

	}

	public String getBaseType() {
		return baseType;
	}

	private void parse(final String baseType) {
		// TODO
	}
}
