package nextmethod.web.razor.editor.internal;

import nextmethod.annotations.Internal;
import nextmethod.base.Debug;

@Internal
abstract class BaseThreadState {

	private long threadId = -1;

	protected BaseThreadState() {}

	protected void setThreadId(final long threadId) {
		this.threadId = threadId;
	}

	protected void ensureOnThread() {
		if (Debug.isAssertEnabled()) {
			assert threadId != -1;
			assert Thread.currentThread().getId() == threadId;
		}
	}

	protected void ensureNotOnThread() {
		if (Debug.isAssertEnabled()) {
			assert threadId != -1;
			assert Thread.currentThread().getId() != threadId;
		}
	}
}
