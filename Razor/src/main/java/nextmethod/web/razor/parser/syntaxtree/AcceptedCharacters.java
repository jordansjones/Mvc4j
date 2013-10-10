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

package nextmethod.web.razor.parser.syntaxtree;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumSet;

public enum AcceptedCharacters {

	None,
	NewLine,
	WhiteSpace,
	NonWhiteSpace,;

	public static final EnumSet<AcceptedCharacters> AllWhiteSpace = EnumSet.of(NewLine, WhiteSpace);
	public static final EnumSet<AcceptedCharacters> Any = setOf(NewLine, WhiteSpace, NonWhiteSpace);
	public static final EnumSet<AcceptedCharacters> NotAny = EnumSet.noneOf(AcceptedCharacters.class);
	public static final EnumSet<AcceptedCharacters> AnyExceptNewLine = EnumSet.of(NonWhiteSpace, WhiteSpace);

	public static final EnumSet<AcceptedCharacters> SetOfNone = EnumSet.of(None);

	public static EnumSet<AcceptedCharacters> setOf(@Nullable final AcceptedCharacters... values) {
		if (values == null || values.length < 1) return NotAny;

		final AcceptedCharacters first = values[0];
		if (values.length == 1)
			return EnumSet.of(first);

		return EnumSet.of(first, Arrays.copyOfRange(values, 1, values.length));
	}

}
