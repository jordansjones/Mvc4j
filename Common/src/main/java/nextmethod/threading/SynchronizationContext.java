package nextmethod.threading;

// TODO
public class SynchronizationContext {

	private boolean notificationRequired;

	private static ThreadLocal<SynchronizationContext> currentContext;

	public SynchronizationContext() {
	}

	SynchronizationContext(final SynchronizationContext context) {
		currentContext.set(context);
	}

	public static SynchronizationContext current() {
		if (currentContext.get() == null) {
			currentContext.set(new SynchronizationContext());
		}
		return currentContext.get();
	}

	public SynchronizationContext createCopy() {
		return new SynchronizationContext(this);
	}

	public boolean isWaitNotificationRequired() {
		return notificationRequired;
	}

	public void operationCompleted() {

	}

	public void operationStarted() {

	}

	public void post(final SendOrPostCallback d, final Object state) {

	}

	public void send(final SendOrPostCallback d, final Object state) {

	}
}
