package nextmethod.base;

public interface IDisposable extends AutoCloseable {

	@Override
	void close();
}
