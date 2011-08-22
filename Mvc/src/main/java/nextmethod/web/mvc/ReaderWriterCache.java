package nextmethod.web.mvc;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import nextmethod.OutParam;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 */
abstract class ReaderWriterCache<TKey, TValue> {

	private final Map<TKey, TValue> cache;
	private final ReadWriteLock readWriteLock;
	private final Comparator<TKey> comparator;

	protected ReaderWriterCache() {
		this(null);
	}

	protected ReaderWriterCache(@Nullable final Comparator<TKey> comparator) {
		this.cache = Maps.newHashMap();
		this.readWriteLock = new ReentrantReadWriteLock();
		this.comparator = comparator;
	}

	protected Map<TKey, TValue> getCache() {
		return this.cache;
	}

	protected TValue fetchOrCreateItem(final TKey key, final Supplier<TValue> creator) {
		final OutParam<TValue> existingEntry = OutParam.of();
		if (tryGetValue(key, existingEntry)) {
			return existingEntry.get();
		}

		// Insert the new item into the cache
		final TValue newEntry = creator.get();
		readWriteLock.writeLock().lock();
		try {
			final OutParam<TValue> entry = OutParam.of();
			if (tryGetValueNoLock(key, entry)) {
				return entry.get();
			}
			cache.put(key, newEntry);
			return newEntry;
		}
		finally {
			readWriteLock.writeLock().unlock();
		}
	}

	protected boolean tryGetValue(final TKey key, final OutParam<TValue> outParam) {
		readWriteLock.readLock().lock();
		try {
			return tryGetValueNoLock(key, outParam);
		}
		finally {
			readWriteLock.readLock().unlock();
		}
	}

	private boolean tryGetValueNoLock(final TKey key, final OutParam<TValue> outParam) {
		if (cache.containsKey(key)) {
			outParam.set(cache.get(key));
			return true;
		}
		return false;
	}
}
