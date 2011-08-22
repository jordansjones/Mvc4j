package nextmethod.web.mvc;


import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
final class SingleEntryGate {

	private final AtomicBoolean status = new AtomicBoolean(false);

	// returns true if this is the first call to tryEnter(), false otherwise
	public boolean tryEnter() {
		return !status.getAndSet(true);
	}

}
