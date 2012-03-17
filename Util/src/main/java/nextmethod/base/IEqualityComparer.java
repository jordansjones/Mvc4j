package nextmethod.base;

public interface IEqualityComparer<T> {

	boolean equals(final T x, final T y);
	int getHashCode(final T obj);

}
