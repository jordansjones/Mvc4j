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

package nextmethod.codedom.compiler;

import java.io.Closeable;
import java.io.Flushable;
import java.io.PrintWriter;
import java.util.Locale;

public class IndentingPrintWriter implements Closeable, Flushable, AutoCloseable {

	public static final String DefaultTabString = "    ";

	private PrintWriter writer;
	private String tabString;
	private int indent;
	private boolean newline;

	public IndentingPrintWriter(final PrintWriter writer) {
		this(writer, DefaultTabString);
	}

	public IndentingPrintWriter(final PrintWriter writer, final String tabString) {
		this.writer = writer;
		this.tabString = DefaultTabString;
		newline = true;
	}

	public int getIndent() {
		return indent;
	}

	public void setIndent(final int indent) {
		this.indent = Math.max(indent, 0);
	}

	/**
	 * Prints an object.  The string produced by the <code>{@link
	 * String#valueOf(Object)}</code> method is translated into bytes
	 * according to the platform's default character encoding, and these bytes
	 * are written in exactly the manner of the <code>{@link #write(int)}</code>
	 * method.
	 *
	 * @param      obj   The <code>Object</code> to be printed
	 * @see        Object#toString()
	 */
	public void print(final Object obj) {
		outputTabs();
		writer.print(obj);
	}

	/**
	 * Writes a portion of a string.
	 * @param s A String
	 * @param off Offset from which to start writing characters
	 * @param len Number of characters to write
	 */
	public void write(final String s, final int off, final int len) {
		outputTabs();
		writer.write(s, off, len);
	}

	/**
	 * Writes a string.  This method cannot be inherited from the Writer class
	 * because it must suppress I/O exceptions.
	 * @param s String to be written
	 */
	public void write(final String s) {
		outputTabs();
		writer.write(s);
	}

	/**
	 * Writes a formatted string to this writer using the specified format
	 * string and arguments.  If automatic flushing is enabled, calls to this
	 * method will flush the output buffer.
	 *
	 * <p> The locale always used is the one returned by {@link
	 * java.util.Locale#getDefault() Locale.getDefault()}, regardless of any
	 * previous invocations of other formatting methods on this object.
	 *
	 * @param  format
	 *         A format string as described in <a
	 *         href="../util/Formatter.html#syntax">Format string syntax</a>.
	 *
	 * @param  args
	 *         Arguments referenced by the format specifiers in the format
	 *         string.  If there are more arguments than format specifiers, the
	 *         extra arguments are ignored.  The number of arguments is
	 *         variable and may be zero.  The maximum number of arguments is
	 *         limited by the maximum dimension of a Java array as defined by
	 *         <cite>The Java&trade; Virtual Machine Specification</cite>.
	 *         The behaviour on a
	 *         <tt>null</tt> argument depends on the <a
	 *         href="../util/Formatter.html#syntax">conversion</a>.
	 *
	 * @throws  java.util.IllegalFormatException
	 *          If a format string contains an illegal syntax, a format
	 *          specifier that is incompatible with the given arguments,
	 *          insufficient arguments given the format string, or other
	 *          illegal conditions.  For specification of all possible
	 *          formatting errors, see the <a
	 *          href="../util/Formatter.html#detail">Details</a> section of the
	 *          Formatter class specification.
	 *
	 * @throws  NullPointerException
	 *          If the <tt>format</tt> is <tt>null</tt>
	 *
	 * @return  This writer
	 *
	 * @since  1.5
	 */
	public PrintWriter format(final String format, final Object... args) {
		outputTabs();
		return writer.format(format, args);
	}

	/**
	 * Prints a double-precision floating-point number.  The string produced by
	 * <code>{@link String#valueOf(double)}</code> is translated into
	 * bytes according to the platform's default character encoding, and these
	 * bytes are written in exactly the manner of the <code>{@link
	 * #write(int)}</code> method.
	 *
	 * @param      d   The <code>double</code> to be printed
	 * @see        Double#toString(double)
	 */
	public void print(final double d) {
		outputTabs();
		writer.print(d);
	}

	/**
	 * Prints an integer.  The string produced by <code>{@link
	 * String#valueOf(int)}</code> is translated into bytes according
	 * to the platform's default character encoding, and these bytes are
	 * written in exactly the manner of the <code>{@link #write(int)}</code>
	 * method.
	 *
	 * @param      i   The <code>int</code> to be printed
	 * @see        Integer#toString(int)
	 */
	public void print(final int i) {
		outputTabs();
		writer.print(i);
	}

	/**
	 * Prints a character and then terminates the line.  This method behaves as
	 * though it invokes <code>{@link #print(char)}</code> and then <code>{@link
	 * #println()}</code>.
	 *
	 * @param x the <code>char</code> value to be printed
	 */
	public void println(final char x) {
		outputTabs();
		writer.println(x);
		newline = true;
	}

	/**
	 * Prints an array of characters.  The characters are converted into bytes
	 * according to the platform's default character encoding, and these bytes
	 * are written in exactly the manner of the <code>{@link #write(int)}</code>
	 * method.
	 *
	 * @param      s   The array of chars to be printed
	 *
	 * @throws  NullPointerException  If <code>s</code> is <code>null</code>
	 */
	public void print(final char[] s) {
		outputTabs();
		writer.print(s);
	}

	/**
	 * Prints a character.  The character is translated into one or more bytes
	 * according to the platform's default character encoding, and these bytes
	 * are written in exactly the manner of the <code>{@link
	 * #write(int)}</code> method.
	 *
	 * @param      c   The <code>char</code> to be printed
	 */
	public void print(final char c) {
		outputTabs();
		writer.print(c);
	}

	/**
	 * Prints a double-precision floating-point number and then terminates the
	 * line.  This method behaves as though it invokes <code>{@link
	 * #print(double)}</code> and then <code>{@link #println()}</code>.
	 *
	 * @param x the <code>double</code> value to be printed
	 */
	public void println(final double x) {
		outputTabs();
		writer.println(x);
		newline = true;
	}

	/**
	 * Prints a floating-point number.  The string produced by <code>{@link
	 * String#valueOf(float)}</code> is translated into bytes
	 * according to the platform's default character encoding, and these bytes
	 * are written in exactly the manner of the <code>{@link #write(int)}</code>
	 * method.
	 *
	 * @param      f   The <code>float</code> to be printed
	 * @see        Float#toString(float)
	 */
	public void print(final float f) {
		outputTabs();
		writer.print(f);
	}

	/**
	 * Prints a boolean value and then terminates the line.  This method behaves
	 * as though it invokes <code>{@link #print(boolean)}</code> and then
	 * <code>{@link #println()}</code>.
	 *
	 * @param x the <code>boolean</code> value to be printed
	 */
	public void println(final boolean x) {
		outputTabs();
		writer.println(x);
		newline = true;
	}

	/**
	 * Prints a string.  If the argument is <code>null</code> then the string
	 * <code>"null"</code> is printed.  Otherwise, the string's characters are
	 * converted into bytes according to the platform's default character
	 * encoding, and these bytes are written in exactly the manner of the
	 * <code>{@link #write(int)}</code> method.
	 *
	 * @param      s   The <code>String</code> to be printed
	 */
	public void print(final String s) {
		outputTabs();
		writer.print(s);
	}

	/**
	 * Prints a long integer.  The string produced by <code>{@link
	 * String#valueOf(long)}</code> is translated into bytes
	 * according to the platform's default character encoding, and these bytes
	 * are written in exactly the manner of the <code>{@link #write(int)}</code>
	 * method.
	 *
	 * @param      l   The <code>long</code> to be printed
	 * @see        Long#toString(long)
	 */
	public void print(final long l) {
		outputTabs();
		writer.print(l);
	}

	/**
	 * Prints an integer and then terminates the line.  This method behaves as
	 * though it invokes <code>{@link #print(int)}</code> and then <code>{@link
	 * #println()}</code>.
	 *
	 * @param x the <code>int</code> value to be printed
	 */
	public void println(final int x) {
		outputTabs();
		writer.println(x);
		newline = true;
	}

	/**
	 * Prints a boolean value.  The string produced by <code>{@link
	 * String#valueOf(boolean)}</code> is translated into bytes
	 * according to the platform's default character encoding, and these bytes
	 * are written in exactly the manner of the <code>{@link
	 * #write(int)}</code> method.
	 *
	 * @param      b   The <code>boolean</code> to be printed
	 */
	public void print(final boolean b) {
		outputTabs();
		writer.print(b);
	}

	/**
	 * Writes a formatted string to this writer using the specified format
	 * string and arguments.  If automatic flushing is enabled, calls to this
	 * method will flush the output buffer.
	 *
	 * @param  l
	 *         The {@linkplain java.util.Locale locale} to apply during
	 *         formatting.  If <tt>l</tt> is <tt>null</tt> then no localization
	 *         is applied.
	 *
	 * @param  format
	 *         A format string as described in <a
	 *         href="../util/Formatter.html#syntax">Format string syntax</a>.
	 *
	 * @param  args
	 *         Arguments referenced by the format specifiers in the format
	 *         string.  If there are more arguments than format specifiers, the
	 *         extra arguments are ignored.  The number of arguments is
	 *         variable and may be zero.  The maximum number of arguments is
	 *         limited by the maximum dimension of a Java array as defined by
	 *         <cite>The Java&trade; Virtual Machine Specification</cite>.
	 *         The behaviour on a
	 *         <tt>null</tt> argument depends on the <a
	 *         href="../util/Formatter.html#syntax">conversion</a>.
	 *
	 * @throws  java.util.IllegalFormatException
	 *          If a format string contains an illegal syntax, a format
	 *          specifier that is incompatible with the given arguments,
	 *          insufficient arguments given the format string, or other
	 *          illegal conditions.  For specification of all possible
	 *          formatting errors, see the <a
	 *          href="../util/Formatter.html#detail">Details</a> section of the
	 *          formatter class specification.
	 *
	 * @throws  NullPointerException
	 *          If the <tt>format</tt> is <tt>null</tt>
	 *
	 * @return  This writer
	 *
	 * @since  1.5
	 */
	public PrintWriter format(final Locale l, final String format, final Object... args) {
		outputTabs();
		return writer.format(l, format, args);
	}

	/**
	 * Prints a floating-point number and then terminates the line.  This method
	 * behaves as though it invokes <code>{@link #print(float)}</code> and then
	 * <code>{@link #println()}</code>.
	 *
	 * @param x the <code>float</code> value to be printed
	 */
	public void println(final float x) {
		outputTabs();
		writer.println(x);
		newline = true;
	}

	/**
	 * Prints a long integer and then terminates the line.  This method behaves
	 * as though it invokes <code>{@link #print(long)}</code> and then
	 * <code>{@link #println()}</code>.
	 *
	 * @param x the <code>long</code> value to be printed
	 */
	public void println(final long x) {
		outputTabs();
		writer.println(x);
		newline = true;
	}

	/**
	 * Terminates the current line by writing the line separator string.  The
	 * line separator string is defined by the system property
	 * <code>line.separator</code>, and is not necessarily a single newline
	 * character (<code>'\n'</code>).
	 */
	public void println() {
		outputTabs();
		writer.println();
		newline = true;
	}

	/**
	 * Prints an Object and then terminates the line.  This method calls
	 * at first String.valueOf(x) to get the printed object's string value,
	 * then behaves as
	 * though it invokes <code>{@link #print(String)}</code> and then
	 * <code>{@link #println()}</code>.
	 *
	 * @param x  The <code>Object</code> to be printed.
	 */
	public void println(final Object x) {
		outputTabs();
		writer.println(x);
		newline = true;
	}

	/**
	 * Writes a single character.
	 * @param c int specifying a character to be written.
	 */
	public void write(final int c) {
		outputTabs();
		writer.write(c);
	}

	/**
	 * Flushes the stream if it's not closed and checks its error state.
	 *
	 * @return <code>true</code> if the print stream has encountered an error,
	 *          either on the underlying output stream or during a format
	 *          conversion.
	 */
	public boolean checkError() {
		return writer.checkError();
	}

	/**
	 * Prints an array of characters and then terminates the line.  This method
	 * behaves as though it invokes <code>{@link #print(char[])}</code> and then
	 * <code>{@link #println()}</code>.
	 *
	 * @param x the array of <code>char</code> values to be printed
	 */
	public void println(final char[] x) {
		outputTabs();
		writer.println(x);
		newline = true;
	}

	/**
	 * A convenience method to write a formatted string to this writer using
	 * the specified format string and arguments.  If automatic flushing is
	 * enabled, calls to this method will flush the output buffer.
	 *
	 * <p> An invocation of this method of the form <tt>out.printf(l, format,
	 * args)</tt> behaves in exactly the same way as the invocation
	 *
	 * <pre>
	 *     out.format(l, format, args) </pre>
	 *
	 * @param  l
	 *         The {@linkplain java.util.Locale locale} to apply during
	 *         formatting.  If <tt>l</tt> is <tt>null</tt> then no localization
	 *         is applied.
	 *
	 * @param  format
	 *         A format string as described in <a
	 *         href="../util/Formatter.html#syntax">Format string syntax</a>.
	 *
	 * @param  args
	 *         Arguments referenced by the format specifiers in the format
	 *         string.  If there are more arguments than format specifiers, the
	 *         extra arguments are ignored.  The number of arguments is
	 *         variable and may be zero.  The maximum number of arguments is
	 *         limited by the maximum dimension of a Java array as defined by
	 *         <cite>The Java&trade; Virtual Machine Specification</cite>.
	 *         The behaviour on a
	 *         <tt>null</tt> argument depends on the <a
	 *         href="../util/Formatter.html#syntax">conversion</a>.
	 *
	 * @throws  java.util.IllegalFormatException
	 *          If a format string contains an illegal syntax, a format
	 *          specifier that is incompatible with the given arguments,
	 *          insufficient arguments given the format string, or other
	 *          illegal conditions.  For specification of all possible
	 *          formatting errors, see the <a
	 *          href="../util/Formatter.html#detail">Details</a> section of the
	 *          formatter class specification.
	 *
	 * @throws  NullPointerException
	 *          If the <tt>format</tt> is <tt>null</tt>
	 *
	 * @return  This writer
	 *
	 * @since  1.5
	 */
	public PrintWriter printf(final Locale l, final String format, final Object... args) {
		outputTabs();
		return writer.printf(l, format, args);
	}

	/**
	 * Writes an array of characters.  This method cannot be inherited from the
	 * Writer class because it must suppress I/O exceptions.
	 * @param buf Array of characters to be written
	 */
	public void write(final char[] buf) {
		outputTabs();
		writer.write(buf);
	}

	/**
	 * A convenience method to write a formatted string to this writer using
	 * the specified format string and arguments.  If automatic flushing is
	 * enabled, calls to this method will flush the output buffer.
	 *
	 * <p> An invocation of this method of the form <tt>out.printf(format,
	 * args)</tt> behaves in exactly the same way as the invocation
	 *
	 * <pre>
	 *     out.format(format, args) </pre>
	 *
	 * @param  format
	 *         A format string as described in <a
	 *         href="../util/Formatter.html#syntax">Format string syntax</a>.
	 *
	 * @param  args
	 *         Arguments referenced by the format specifiers in the format
	 *         string.  If there are more arguments than format specifiers, the
	 *         extra arguments are ignored.  The number of arguments is
	 *         variable and may be zero.  The maximum number of arguments is
	 *         limited by the maximum dimension of a Java array as defined by
	 *         <cite>The Java&trade; Virtual Machine Specification</cite>.
	 *         The behaviour on a
	 *         <tt>null</tt> argument depends on the <a
	 *         href="../util/Formatter.html#syntax">conversion</a>.
	 *
	 * @throws  java.util.IllegalFormatException
	 *          If a format string contains an illegal syntax, a format
	 *          specifier that is incompatible with the given arguments,
	 *          insufficient arguments given the format string, or other
	 *          illegal conditions.  For specification of all possible
	 *          formatting errors, see the <a
	 *          href="../util/Formatter.html#detail">Details</a> section of the
	 *          formatter class specification.
	 *
	 * @throws  NullPointerException
	 *          If the <tt>format</tt> is <tt>null</tt>
	 *
	 * @return  This writer
	 *
	 * @since  1.5
	 */
	public PrintWriter printf(final String format, final Object... args) {
		outputTabs();
		return writer.printf(format, args);
	}

	/**
	 * Writes A Portion of an array of characters.
	 * @param buf Array of characters
	 * @param off Offset from which to start writing characters
	 * @param len Number of characters to write
	 */
	public void write(final char[] buf, final int off, final int len) {
		outputTabs();
		writer.write(buf, off, len);
	}

	/**
	 * Prints a String and then terminates the line.  This method behaves as
	 * though it invokes <code>{@link #print(String)}</code> and then
	 * <code>{@link #println()}</code>.
	 *
	 * @param x the <code>String</code> value to be printed
	 */
	public void println(final String x) {
		outputTabs();
		writer.println(x);
		newline = true;
	}

	/**
	 * Flushes the stream.
	 * @see #checkError()
	 */
	@Override
	public void flush() {
		writer.flush();
	}

	/**
	 * Closes the stream and releases any system resources associated
	 * with it. Closing a previously closed stream has no effect.
	 *
	 * @see #checkError()
	 */
	@Override
	public void close() {
		writer.close();
	}

	protected void outputTabs() {
		if (newline) {
			for (int i = 0; i < indent; i++) {
				writer.write(tabString);
			}
			newline = false;
		}
	}
}
