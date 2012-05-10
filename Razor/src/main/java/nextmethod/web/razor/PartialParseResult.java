package nextmethod.web.razor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * The result of attempt an incremental parse.
 * <p>
 * Either the Accepted or Rejeted flag is ALWAYS set.
 * Additionally, Provisional my be set with Accepted and SpanContextChanged may be set with Rejected.
 * Provisional may NOT be set with Rejected and SpanContextChanged may NOT be set with Accepted.
 * </p>
 */
public enum PartialParseResult {

	/**
	 * Indicates that the edit could not be accepted and that a reparse is underway.
	 */
	Rejected,

	/**
	 * Indicates that the edit was accepted and has been added to the parse tree
	 */
	Accepted,

	/**
	 * Indiicates that the edit was accepted, but that a reparse should be forced when idle time is available sice the edit may be misclassified.
	 * <p>
	 * This generally occurs when a "." is typed in an Implicit Expression, since editors require that this
	 * be assigned to Code in order to properly support features like code completion. However, if no further edits
	 * occur following the ".", it should be treated as Markup
	 * </p>
	 */
	Provisional,

	/**
	 * Indicates that the edit caused a change in the span's context and that if any statement completions were active prior to starting this
	 * initial parse, they should be reinitialized.
	 */
	SpanContextChanged,

	/**
	 * Indicates that the edit requires an auto completion to occur.
	 */
	AutoCompleteBlock,;

	public static final EnumSet<PartialParseResult> Any = EnumSet.allOf(PartialParseResult.class);
	public static final EnumSet<PartialParseResult> NotAny = EnumSet.noneOf(PartialParseResult.class);

	public static EnumSet<PartialParseResult> setOf(@Nullable final PartialParseResult... values) {
		if (values == null || values.length < 1) return NotAny;

		final PartialParseResult first = values[0];
		if (values.length == 1) return EnumSet.of(first);
		return EnumSet.of(first, Arrays.copyOfRange(values, 1, values.length));
	}

}
