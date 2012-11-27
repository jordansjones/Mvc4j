package nextmethod.threading;

// TODO
public class EventWaitHandle extends WaitHandle {

	public EventWaitHandle() {
		this(false);
	}

	public EventWaitHandle(final boolean initialState) {
		super(initialState);
	}
}
