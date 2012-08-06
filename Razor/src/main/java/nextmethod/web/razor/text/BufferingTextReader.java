package nextmethod.web.razor.text;

import com.google.common.collect.Queues;
import nextmethod.base.IAction;
import nextmethod.base.IDisposable;
import nextmethod.web.razor.utils.DisposableAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Deque;

import static com.google.common.base.Preconditions.checkNotNull;
import static nextmethod.web.razor.resources.Mvc4jRazorResources.RazorResources;

public class BufferingTextReader extends LookaheadTextReader {

	private final Deque<BacktrackContext> backtrackStack = Queues.newArrayDeque();

	private int currentBufferPosition;
	private int currentCharacter;
	private SourceLocationTracker locationTracker;

	private StringBuilder buffer;
	private boolean buffering;
	private TextReader innerReader;

	public BufferingTextReader(@Nullable final TextReader source) {
		this.innerReader = checkNotNull(source);
		this.locationTracker = new SourceLocationTracker();

		updateCurrentCharacter();
	}

	@Override
	public SourceLocation getCurrentLocation() {
		return locationTracker.getCurrentLocation();
	}

	protected int getCurrentCharacter() {
		return currentCharacter;
	}

	@Override
	public int read() {
		final int ch = getCurrentCharacter();
		nextCharacter();
		return ch;
	}

	@Override
	public int peek() {
		return getCurrentCharacter();
	}

	@Override
	protected void dispose(boolean disposing) {
		if (disposing)
			innerReader.close();

		super.dispose(disposing);
	}

	@Override
	public IDisposable beginLookahead() {
		if (buffer == null)
			buffer = new StringBuilder();

		if (!buffering) {
			// We're not already buffering, so we need to expand the buffer to hold the first character
			expandBuffer();
			buffering = true;
		}
		final BacktrackContext context = new BacktrackContext(currentBufferPosition, getCurrentLocation());
		backtrackStack.push(context);

		return new DisposableAction(new IAction<Object>() {
			@Override
			public Object invoke() {
				endLookahead(context);
				return null;
			}
		});
	}

	@Override
	public void cancelBacktrack() {
		if (backtrackStack.isEmpty())
			throw new UnsupportedOperationException(RazorResources().getString("cancelBacktrack.must.be.called.within.lookahead"));

		// Ust pop the current backtrack context so that when the lookahead ends, it won't be backtracked
		backtrackStack.pop();
	}

	private void endLookahead(@Nonnull final BacktrackContext context) {
		// If the specified context is not the one on the stack, it was popped by a call to doNotBacktrack
		if (!backtrackStack.isEmpty() && (backtrackStack.peek() == context)) {
			backtrackStack.pop();
			currentBufferPosition = context.getBufferIndex();
			locationTracker.setCurrentLocation(context.getLocation());

			updateCurrentCharacter();
		}
	}

	protected void nextCharacter() {
		final int prevChar = getCurrentCharacter();
		if (prevChar == -1)
			return; // We're at the end of the source

		if (buffering) {
			if (currentBufferPosition >= buffer.length() - 1) {
				// If there are no more lookaheads (thus no need to continue with the buffer) we can just clean up the buffer
				if (backtrackStack.isEmpty()) {
					// Reset the buffer
					buffer.delete(0, buffer.length());
					currentBufferPosition = 0;
					buffering = false;
				}
				else if (!expandBuffer()) {
					// Failed to expand the buffer, because we're at the end of the source
					currentBufferPosition = buffer.length(); // Force the position past the end of the buffer
				}
			}
			else {
				// Not at the end yet, just advance the buffer pointer
				currentBufferPosition++;
			}
		}
		else {
			// Just act like normal
			innerReader.read(); // Don't care about the return value, peek() is used to get the characters from the source
		}

		updateCurrentCharacter();
		locationTracker.updateLocation((char) prevChar, (char) getCurrentCharacter());
	}

	protected boolean expandBuffer() {
		// Pull another character into the buffer and update the position
		final int ch = innerReader.read();

		// Only append the character to the buffer if there actually is one
		if (ch != -1) {
			buffer.append((char) ch);
			currentBufferPosition = buffer.length() - 1;
			return true;
		}
		return false;
	}

	private void updateCurrentCharacter() {
		if (buffering && currentBufferPosition < buffer.length()) {
			// Read from the buffer
			currentCharacter = (int) buffer.charAt(currentBufferPosition);
		}
		else {
			// No buffer? Peed from the source
			currentCharacter = innerReader.peek();
		}
	}


	private class BacktrackContext {

		private final int bufferIndex;
		private final SourceLocation location;

		private BacktrackContext(int bufferIndex, SourceLocation location) {
			this.bufferIndex = bufferIndex;
			this.location = location;
		}

		public int getBufferIndex() {
			return bufferIndex;
		}

		public SourceLocation getLocation() {
			return location;
		}
	}
}
