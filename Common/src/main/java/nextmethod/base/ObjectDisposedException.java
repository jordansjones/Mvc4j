package nextmethod.base;

public class ObjectDisposedException extends IllegalStateException {

	public ObjectDisposedException(final String objectName) {
		super(objectName);
	}
}
