/*
 * Copyright 2012 Jordan S. Jones
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

package nextmethod.base;

import javax.annotation.Nullable;

/**
 *
 */
public final class Strings {
	// Static Class
	private Strings() {}

	public static final String Empty = "";
	public static final String CRLF = "\r\n";
	public static final String LF = "\n";

	/**
	 * Returns the given string if it is non-null; the empty string otherwise.
	 *
	 * @param string the string to test and possibly return
	 * @return {@code string} itself if it is non-null; {@code ""} if it is null
	 */
	public static String nullToEmpty(@Nullable final String string) {
		return com.google.common.base.Strings.nullToEmpty(string);
	}

	/**
	 * Returns a string consisting of a specific number of concatenated copies of
	 * an input string. For example, {@code repeat("hey", 3)} returns the string
	 * {@code "heyheyhey"}.
	 *
	 * @param string any non-null string
	 * @param count the number of times to repeat it; a nonnegative integer
	 * @return a string containing {@code string} repeated {@code count} times
	 *     (the empty string if {@code count} is zero)
	 * @throws IllegalArgumentException if {@code count} is negative
	 */
	public static String repeat(final String string, final int count) {
		return com.google.common.base.Strings.repeat(string, count);
	}

	/**
	 * Returns {@code true} if the given string is null or is the empty string.
	 *
	 * <p>Consider normalizing your string references with {@link #nullToEmpty}.
	 * If you do, you can use {@link String#isEmpty()} instead of this
	 * method, and you won't need special null-safe forms of methods like {@link
	 * String#toUpperCase} either. Or, if you'd like to normalize "in the other
	 * direction," converting empty strings to {@code null}, you can use {@link
	 * #emptyToNull}.
	 *
	 * @param string a string reference to check
	 * @return {@code true} if the string is null or is the empty string
	 */
	public static boolean isNullOrEmpty(@Nullable final String string) {
		return com.google.common.base.Strings.isNullOrEmpty(string);
	}

	/**
	 * Returns a string, of length at least {@code minLength}, consisting of
	 * {@code string} prepended with as many copies of {@code padChar} as are
	 * necessary to reach that length. For example,
	 *
	 * <ul>
	 * <li>{@code padStart("7", 3, '0')} returns {@code "007"}
	 * <li>{@code padStart("2010", 3, '0')} returns {@code "2010"}
	 * </ul>
	 *
	 * <p>See {@link java.util.Formatter} for a richer set of formatting capabilities.
	 *
	 * @param string the string which should appear at the end of the result
	 * @param minLength the minimum length the resulting string must have. Can be
	 *     zero or negative, in which case the input string is always returned.
	 * @param padChar the character to insert at the beginning of the result until
	 *     the minimum length is reached
	 * @return the padded string
	 */
	public static String padStart(final String string, final int minLength, final char padChar) {
		return com.google.common.base.Strings.padStart(string, minLength, padChar);
	}

	/**
	 * Returns a string, of length at least {@code minLength}, consisting of
	 * {@code string} appended with as many copies of {@code padChar} as are
	 * necessary to reach that length. For example,
	 *
	 * <ul>
	 * <li>{@code padEnd("4.", 5, '0')} returns {@code "4.000"}
	 * <li>{@code padEnd("2010", 3, '!')} returns {@code "2010"}
	 * </ul>
	 *
	 * <p>See {@link java.util.Formatter} for a richer set of formatting capabilities.
	 *
	 * @param string the string which should appear at the beginning of the result
	 * @param minLength the minimum length the resulting string must have. Can be
	 *     zero or negative, in which case the input string is always returned.
	 * @param padChar the character to append to the end of the result until the
	 *     minimum length is reached
	 * @return the padded string
	 */
	public static String padEnd(final String string, final int minLength, final char padChar) {
		return com.google.common.base.Strings.padEnd(string, minLength, padChar);
	}

	/**
	 * Returns the longest string {@code prefix} such that
	 * {@code a.toString().startsWith(prefix) && b.toString().startsWith(prefix)},
	 * taking care not to split surrogate pairs. If {@code a} and {@code b} have
	 * no common prefix, returns the empty string.
	 *
	 * @since 11.0
	 */
	public static String commonPrefix(final CharSequence a, final CharSequence b) {
		return com.google.common.base.Strings.commonPrefix(a, b);
	}

	/**
	 * Returns the longest string {@code suffix} such that
	 * {@code a.toString().endsWith(suffix) && b.toString().endsWith(suffix)},
	 * taking care not to split surrogate pairs. If {@code a} and {@code b} have
	 * no common suffix, returns the empty string.
	 *
	 * @since 11.0
	 */
	public static String commonSuffix(final CharSequence a, final CharSequence b) {
		return com.google.common.base.Strings.commonSuffix(a, b);
	}

	/**
	 * Returns the given string if it is nonempty; {@code null} otherwise.
	 *
	 * @param string the string to test and possibly return
	 * @return {@code string} itself if it is nonempty; {@code null} if it is
	 *     empty or null
	 */
	@Nullable
	public static String emptyToNull(@Nullable final String string) {
		return com.google.common.base.Strings.emptyToNull(string);
	}

	public static int lastIndexOfAny(final String string, final char[] anyOf)
	{
		if (anyOf.length == 1) return string.lastIndexOf(anyOf[0]);

		int idx = -1;
		int offset = 0;
		do {
			int x = string.lastIndexOf(anyOf[offset++]);
			if (x > idx) {
				idx = x;
			}
		}
		while(offset < anyOf.length);
		return idx;
	}
}
