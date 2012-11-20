package nextmethod.web.razor.editor.internal;

import com.google.common.collect.ImmutableList;
import nextmethod.annotations.Internal;
import nextmethod.threading.CancellationToken;
import nextmethod.web.razor.text.TextChange;

@Internal
final class WorkParcel {

	private final ImmutableList<TextChange> changes;
	private final CancellationToken cancelToken;

	WorkParcel(final ImmutableList<TextChange> changes, final CancellationToken cancelToken) {
		this.changes = changes;
		this.cancelToken = cancelToken;
	}

	public ImmutableList<TextChange> getChanges() {
		return changes;
	}

	public CancellationToken getCancelToken() {
		return cancelToken;
	}
}
