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

public class CodeStatement extends CodeObject implements Serializable {

	private static final long serialVersionUID = -5647665217729816196L;

	private CodeLinePragma linePragma;

	CodeDirectiveCollection endDirectives;
	CodeDirectiveCollection startDirectives;

	public CodeLinePragma getLinePragma() {
		return linePragma;
	}

	public void setLinePragma(final CodeLinePragma linePragma) {
		this.linePragma = linePragma;
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
