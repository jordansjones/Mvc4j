/*
 * Copyright 2014 Jordan S. Jones <jordansjones@gmail.com>
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

import nextmethod.base.SystemHelpers;

@SuppressWarnings("UnusedDeclaration")
public class IndentingPrintWriter implements Closeable, Flushable, AutoCloseable {

    private PrintWriter writer;
    private String tabString;
    private String newlineString;
    private int indent;
    private boolean newline;

    public IndentingPrintWriter(final PrintWriter writer) {
        this(writer, CodeGeneratorOptions.DefaultIndentString, SystemHelpers.newLine());
    }

    public IndentingPrintWriter(final PrintWriter writer, final String tabString, final String newlineString) {
        this.writer = writer;
        this.tabString = tabString;
        this.newlineString = newlineString;
        this.newline = true;
    }

    public int getIndent() {
        return indent;
    }

    public IndentingPrintWriter setIndent(final int indent) {
        this.indent = Math.max(indent, 0);
        return this;
    }

    /**
     * Prints an object.  The string produced by the <code>{@link
     * String#valueOf(Object)}</code> method is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param obj The <code>Object</code> to be printed
     *
     * @see Object#toString()
     */
    public IndentingPrintWriter write(final Object obj) {
        outputTabs();
        writer.print(obj);
        return this;
    }

    /**
     * Writes a portion of a string.
     *
     * @param s   A String
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     */
    public IndentingPrintWriter write(final String s, final int off, final int len) {
        outputTabs();
        writer.write(s, off, len);
        return this;
    }

    /**
     * Writes a formatted string to this writer using the specified format
     * string and arguments.  If automatic flushing is enabled, calls to this
     * method will flush the output buffer.
     * <p>
     * <p> The locale always used is the one returned by {@link
     * java.util.Locale#getDefault() Locale.getDefault()}, regardless of any
     * previous invocations of other formatting methods on this object.
     *
     * @param format A format string as described in <a
     *               href="../util/Formatter.html#syntax">Format string syntax</a>.
     * @param args   Arguments referenced by the format specifiers in the format
     *               string.  If there are more arguments than format specifiers, the
     *               extra arguments are ignored.  The number of arguments is
     *               variable and may be zero.  The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by
     *               <cite>The Java&trade; Virtual Machine Specification</cite>.
     *               The behaviour on a
     *               <tt>null</tt> argument depends on the <a
     *               href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @return This writer
     *
     * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format
     *                                          specifier that is incompatible with the given arguments,
     *                                          insufficient arguments given the format string, or other
     *                                          illegal conditions.  For specification of all possible
     *                                          formatting errors, see the <a
     *                                          href="../util/Formatter.html#detail">Details</a> section of the
     *                                          Formatter class specification.
     * @throws NullPointerException             If the <tt>format</tt> is <tt>null</tt>
     * @since 1.5
     */
    public IndentingPrintWriter format(final String format, final Object... args) {
        outputTabs();
        writer.format(format, args);
        return this;
    }

    /**
     * Prints a double-precision floating-point number.  The string produced by
     * <code>{@link String#valueOf(double)}</code> is translated into
     * bytes according to the platform's default character encoding, and these
     * bytes are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param d The <code>double</code> to be printed
     *
     * @see Double#toString(double)
     */
    public IndentingPrintWriter write(final double d) {
        outputTabs();
        writer.print(d);
        return this;
    }

    /**
     * Prints an integer.  The string produced by <code>{@link
     * String#valueOf(int)}</code> is translated into bytes according
     * to the platform's default character encoding.
     *
     * @param i The <code>int</code> to be printed
     *
     * @see Integer#toString(int)
     */
    public IndentingPrintWriter write(final int i) {
        outputTabs();
        writer.print(i);
        return this;
    }

    /**
     * Prints a character and then terminates the line.  This method behaves as
     * though it invokes <code>{@link #write(char)}</code> and then <code>{@link
     * #writeLine()}</code>.
     *
     * @param x the <code>char</code> value to be printed
     */
    public IndentingPrintWriter writeLine(final char x) {
        outputTabs();
        writer.print(x);
        return writeLine();
    }

    /**
     * Prints an array of characters.  The characters are converted into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param s The array of chars to be printed
     *
     * @throws NullPointerException If <code>s</code> is <code>null</code>
     */
    public IndentingPrintWriter write(final char[] s) {
        outputTabs();
        writer.print(s);
        return this;
    }

    /**
     * Prints a character.  The character is translated into one or more bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param c The <code>char</code> to be printed
     */
    public IndentingPrintWriter write(final char c) {
        outputTabs();
        writer.print(c);
        return this;
    }

    /**
     * Prints a double-precision floating-point number and then terminates the
     * line.  This method behaves as though it invokes <code>{@link
     * #write(double)}</code> and then <code>{@link #writeLine()}</code>.
     *
     * @param x the <code>double</code> value to be printed
     */
    public IndentingPrintWriter writeLine(final double x) {
        outputTabs();
        writer.print(x);
        return writeLine();
    }

    /**
     * Prints a floating-point number.  The string produced by <code>{@link
     * String#valueOf(float)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param f The <code>float</code> to be printed
     *
     * @see Float#toString(float)
     */
    public IndentingPrintWriter write(final float f) {
        outputTabs();
        writer.print(f);
        return this;
    }

    /**
     * Prints a boolean value and then terminates the line.  This method behaves
     * as though it invokes <code>{@link #write(boolean)}</code> and then
     * <code>{@link #writeLine()}</code>.
     *
     * @param x the <code>boolean</code> value to be printed
     */
    public IndentingPrintWriter writeLine(final boolean x) {
        outputTabs();
        writer.print(x);
        return writeLine();
    }

    /**
     * Prints a string.  If the argument is <code>null</code> then the string
     * <code>"null"</code> is printed.  Otherwise, the string's characters are
     * converted into bytes according to the platform's default character
     * encoding, and these bytes are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
     *
     * @param s The <code>String</code> to be printed
     */
    public IndentingPrintWriter write(final String s) {
        outputTabs();
        writer.print(s);
        return this;
    }

    /**
     * Prints a long integer.  The string produced by <code>{@link
     * String#valueOf(long)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param l The <code>long</code> to be printed
     *
     * @see Long#toString(long)
     */
    public IndentingPrintWriter write(final long l) {
        outputTabs();
        writer.print(l);
        return this;
    }

    /**
     * Prints an integer and then terminates the line.  This method behaves as
     * though it invokes <code>{@link #write(int)}</code> and then <code>{@link
     * #writeLine()}</code>.
     *
     * @param x the <code>int</code> value to be printed
     */
    public IndentingPrintWriter writeLine(final int x) {
        outputTabs();
        writer.print(x);
        return writeLine();
    }

    /**
     * Prints a boolean value.  The string produced by <code>{@link
     * String#valueOf(boolean)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param b The <code>boolean</code> to be printed
     */
    public IndentingPrintWriter write(final boolean b) {
        outputTabs();
        writer.print(b);
        return this;
    }

    /**
     * Writes a formatted string to this writer using the specified format
     * string and arguments.  If automatic flushing is enabled, calls to this
     * method will flush the output buffer.
     *
     * @param l      The {@linkplain java.util.Locale locale} to apply during
     *               formatting.  If <tt>l</tt> is <tt>null</tt> then no localization
     *               is applied.
     * @param format A format string as described in <a
     *               href="../util/Formatter.html#syntax">Format string syntax</a>.
     * @param args   Arguments referenced by the format specifiers in the format
     *               string.  If there are more arguments than format specifiers, the
     *               extra arguments are ignored.  The number of arguments is
     *               variable and may be zero.  The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by
     *               <cite>The Java&trade; Virtual Machine Specification</cite>.
     *               The behaviour on a
     *               <tt>null</tt> argument depends on the <a
     *               href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @return This writer
     *
     * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format
     *                                          specifier that is incompatible with the given arguments,
     *                                          insufficient arguments given the format string, or other
     *                                          illegal conditions.  For specification of all possible
     *                                          formatting errors, see the <a
     *                                          href="../util/Formatter.html#detail">Details</a> section of the
     *                                          formatter class specification.
     * @throws NullPointerException             If the <tt>format</tt> is <tt>null</tt>
     * @since 1.5
     */
    public IndentingPrintWriter format(final Locale l, final String format, final Object... args) {
        outputTabs();
        writer.format(l, format, args);
        return this;
    }

    /**
     * Prints a floating-point number and then terminates the line.  This method
     * behaves as though it invokes <code>{@link #write(float)}</code> and then
     * <code>{@link #writeLine()}</code>.
     *
     * @param x the <code>float</code> value to be printed
     */
    public IndentingPrintWriter writeLine(final float x) {
        outputTabs();
        writer.print(x);
        return writeLine();
    }

    /**
     * Prints a long integer and then terminates the line.  This method behaves
     * as though it invokes <code>{@link #write(long)}</code> and then
     * <code>{@link #writeLine()}</code>.
     *
     * @param x the <code>long</code> value to be printed
     */
    public IndentingPrintWriter writeLine(final long x) {
        outputTabs();
        writer.print(x);
        return writeLine();
    }

    /**
     * Terminates the current line by writing the line separator string.  The
     * line separator string is defined by the system property
     * <code>line.separator</code>, and is not necessarily a single newline
     * character (<code>'\n'</code>).
     */
    public IndentingPrintWriter writeLine() {
        outputTabs();
        writer.print(this.newlineString);
        newline = true;
        return this;
    }

    /**
     * Prints an Object and then terminates the line.  This method calls
     * at first String.valueOf(x) to get the printed object's string value,
     * then behaves as
     * though it invokes <code>{@link #write(String)}</code> and then
     * <code>{@link #writeLine()}</code>.
     *
     * @param x The <code>Object</code> to be printed.
     */
    public IndentingPrintWriter writeLine(final Object x) {
        outputTabs();
        writer.print(x);
        return writeLine();
    }

    /**
     * Flushes the stream if it's not closed and checks its error state.
     *
     * @return <code>true</code> if the print stream has encountered an error,
     * either on the underlying output stream or during a format
     * conversion.
     */
    public boolean checkError() {
        return writer.checkError();
    }

    /**
     * Prints an array of characters and then terminates the line.  This method
     * behaves as though it invokes <code>{@link #write(char[])}</code> and then
     * <code>{@link #writeLine()}</code>.
     *
     * @param x the array of <code>char</code> values to be printed
     */
    public IndentingPrintWriter writeLine(final char[] x) {
        outputTabs();
        writer.print(x);
        return writeLine();
    }

    /**
     * A convenience method to write a formatted string to this writer using
     * the specified format string and arguments.  If automatic flushing is
     * enabled, calls to this method will flush the output buffer.
     * <p>
     * <p> An invocation of this method of the form <tt>out.write(l, format,
     * args)</tt> behaves in exactly the same way as the invocation
     * <p>
     * <pre>
     *     out.format(l, format, args) </pre>
     *
     * @param l      The {@linkplain java.util.Locale locale} to apply during
     *               formatting.  If <tt>l</tt> is <tt>null</tt> then no localization
     *               is applied.
     * @param format A format string as described in <a
     *               href="../util/Formatter.html#syntax">Format string syntax</a>.
     * @param args   Arguments referenced by the format specifiers in the format
     *               string.  If there are more arguments than format specifiers, the
     *               extra arguments are ignored.  The number of arguments is
     *               variable and may be zero.  The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by
     *               <cite>The Java&trade; Virtual Machine Specification</cite>.
     *               The behaviour on a
     *               <tt>null</tt> argument depends on the <a
     *               href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @return This writer
     *
     * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format
     *                                          specifier that is incompatible with the given arguments,
     *                                          insufficient arguments given the format string, or other
     *                                          illegal conditions.  For specification of all possible
     *                                          formatting errors, see the <a
     *                                          href="../util/Formatter.html#detail">Details</a> section of the
     *                                          formatter class specification.
     * @throws NullPointerException             If the <tt>format</tt> is <tt>null</tt>
     * @since 1.5
     */
    public IndentingPrintWriter write(final Locale l, final String format, final Object... args) {
        outputTabs();
        writer.printf(l, format, args);
        return this;
    }

    /**
     * A convenience method to write a formatted string to this writer using
     * the specified format string and arguments.  If automatic flushing is
     * enabled, calls to this method will flush the output buffer.
     * <p>
     * <p> An invocation of this method of the form <tt>out.write(format,
     * args)</tt> behaves in exactly the same way as the invocation
     * <p>
     * <pre>
     *     out.format(format, args) </pre>
     *
     * @param format A format string as described in <a
     *               href="../util/Formatter.html#syntax">Format string syntax</a>.
     * @param args   Arguments referenced by the format specifiers in the format
     *               string.  If there are more arguments than format specifiers, the
     *               extra arguments are ignored.  The number of arguments is
     *               variable and may be zero.  The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by
     *               <cite>The Java&trade; Virtual Machine Specification</cite>.
     *               The behaviour on a
     *               <tt>null</tt> argument depends on the <a
     *               href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @return This writer
     *
     * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format
     *                                          specifier that is incompatible with the given arguments,
     *                                          insufficient arguments given the format string, or other
     *                                          illegal conditions.  For specification of all possible
     *                                          formatting errors, see the <a
     *                                          href="../util/Formatter.html#detail">Details</a> section of the
     *                                          formatter class specification.
     * @throws NullPointerException             If the <tt>format</tt> is <tt>null</tt>
     * @since 1.5
     */
    public IndentingPrintWriter write(final String format, final Object... args) {
        outputTabs();
        writer.printf(format, args);
        return this;
    }

    /**
     * Writes A Portion of an array of characters.
     *
     * @param buf Array of characters
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     */
    public IndentingPrintWriter write(final char[] buf, final int off, final int len) {
        outputTabs();
        writer.write(buf, off, len);
        return this;
    }

    /**
     * Prints a String and then terminates the line.  This method behaves as
     * though it invokes <code>{@link #write(String)}</code> and then
     * <code>{@link #writeLine()}</code>.
     *
     * @param x the <code>String</code> value to be printed
     */
    public IndentingPrintWriter writeLine(final String x) {
        outputTabs();
        writer.print(x);
        return writeLine();
    }

    /**
     * A convenience method to write a formatted string to this writer using
     * the specified format string and arguments and then terminates the line.  If automatic flushing is
     * enabled, calls to this method will flush the output buffer.
     * <p>
     * <p> An invocation of this method of the form <tt>out.writeLine(l, format,
     * args)</tt> behaves in exactly the same way as the invocation
     * <p>
     * <pre>
     *     out.format(l, format, args) </pre>
     *
     * @param l      The {@linkplain java.util.Locale locale} to apply during
     *               formatting.  If <tt>l</tt> is <tt>null</tt> then no localization
     *               is applied.
     * @param format A format string as described in <a
     *               href="../util/Formatter.html#syntax">Format string syntax</a>.
     * @param args   Arguments referenced by the format specifiers in the format
     *               string.  If there are more arguments than format specifiers, the
     *               extra arguments are ignored.  The number of arguments is
     *               variable and may be zero.  The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by
     *               <cite>The Java&trade; Virtual Machine Specification</cite>.
     *               The behaviour on a
     *               <tt>null</tt> argument depends on the <a
     *               href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @return This writer
     *
     * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format
     *                                          specifier that is incompatible with the given arguments,
     *                                          insufficient arguments given the format string, or other
     *                                          illegal conditions.  For specification of all possible
     *                                          formatting errors, see the <a
     *                                          href="../util/Formatter.html#detail">Details</a> section of the
     *                                          formatter class specification.
     * @throws NullPointerException             If the <tt>format</tt> is <tt>null</tt>
     * @since 1.5
     */
    public IndentingPrintWriter writeLine(final Locale l, final String format, final Object... args) {
        outputTabs();
        writer.printf(l, format, args);
        return writeLine();
    }

    /**
     * A convenience method to write a formatted string to this writer using
     * the specified format string and arguments and then terminates the line.  If automatic flushing is
     * enabled, calls to this method will flush the output buffer.
     * <p>
     * <p> An invocation of this method of the form <tt>out.write(format,
     * args)</tt> behaves in exactly the same way as the invocation
     * <p>
     * <pre>
     *     out.format(format, args) </pre>
     *
     * @param format A format string as described in <a
     *               href="../util/Formatter.html#syntax">Format string syntax</a>.
     * @param args   Arguments referenced by the format specifiers in the format
     *               string.  If there are more arguments than format specifiers, the
     *               extra arguments are ignored.  The number of arguments is
     *               variable and may be zero.  The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by
     *               <cite>The Java&trade; Virtual Machine Specification</cite>.
     *               The behaviour on a
     *               <tt>null</tt> argument depends on the <a
     *               href="../util/Formatter.html#syntax">conversion</a>.
     *
     * @return This writer
     *
     * @throws java.util.IllegalFormatException If a format string contains an illegal syntax, a format
     *                                          specifier that is incompatible with the given arguments,
     *                                          insufficient arguments given the format string, or other
     *                                          illegal conditions.  For specification of all possible
     *                                          formatting errors, see the <a
     *                                          href="../util/Formatter.html#detail">Details</a> section of the
     *                                          formatter class specification.
     * @throws NullPointerException             If the <tt>format</tt> is <tt>null</tt>
     * @since 1.5
     */
    public IndentingPrintWriter writeLine(final String format, final Object... args) {
        outputTabs();
        writer.printf(format, args);
        return writeLine();
    }

    /**
     * Flushes the stream.
     *
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
