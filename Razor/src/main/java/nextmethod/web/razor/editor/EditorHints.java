package nextmethod.web.razor.editor;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * Used within {@link SpanEditHandler#getEditorHints()}
 *
 * @see SpanEditHandler#getEditorHints()
 */
public enum EditorHints {

	/**
	 * The default (Markup or Code) editor behavior for Statement completion should be used.
	 * Editors can always use the default behavior, even if the span is labeled with a different CompletionType.
	 */
	None,

	/**
	 * Indicates that Virtual Path completion should be used for this span if the editor supports it.
	 * Editors need not support this mode of completion, and will use the {@linkplain EditorHints#None default behavior}.
	 */
	VirtualPath,

	/**
	 * Indicates that this span's content contains the path to the layout page for this document.
	 */
	LayoutPage,;

	public static final EnumSet<EditorHints> Any = EnumSet.allOf(EditorHints.class);
	public static final EnumSet<EditorHints> NotAny = EnumSet.noneOf(EditorHints.class);

	public static EnumSet<EditorHints> setOf(@Nullable final EditorHints... values) {
		if (values == null || values.length < 1) return NotAny;

		final EditorHints first = values[0];
		if (values.length == 1) return EnumSet.of(first);
		return EnumSet.of(first, Arrays.copyOfRange(values, 1, values.length));
	}
}
