package nextmethod.collections;

import com.google.common.collect.AbstractIterator;

import java.util.Iterator;

/**
 *
 */
public abstract class IterableIterator<T> extends AbstractIterator<T> implements Iterable<T> {

	@Override
	public Iterator<T> iterator() {
		return this;
	}
}
