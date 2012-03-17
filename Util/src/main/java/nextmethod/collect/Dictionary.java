package nextmethod.collect;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.math.IntMath;
import com.google.inject.TypeLiteral;
import nextmethod.OutParam;
import nextmethod.annotations.TODO;
import nextmethod.base.IEqualityComparer;

import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

@SuppressWarnings("ForLoopReplaceableByForEach")
public class Dictionary<TKey, TValue> implements IDictionary<TKey, TValue> {

	// The implementation of this class uses a hash table and linked lists
	// (see: http://msdn2.microsoft.com/en-us/library/ms379571(VS.80).aspx).
	//
	// We use a kind of "mini-heap" instead of reference-based linked lists:
	// "keySlots" and "valueSlots" is the heap itself, it stores the data
	// "linkSlots" contains information about how the slots in the heap
	//             are connected into linked lists
	//             In addition, the HashCode field can be used to check if the
	//             corresponding key and value are present (HashCode has the
	//             HASH_FLAG bit set in this case), so, to iterate over all the
	//             items in the dictionary, simply iterate the linkSlots array
	//             and check for the HASH_FLAG bit in the HashCode field.
	//             For this reason, each time a hashcode is calculated, it needs
	//             to be ORed with HASH_FLAG before comparing it with the save hashcode.
	// "touchedSlots" and "emptySlot" manage the free space in the heap

	protected static final int INITIAL_SIZE = 10;
	protected static final float DEFAULT_LOAD_FACTOR = (90f / 100);
	protected static final int NO_SLOT = -1;
	protected static final int HASH_FLAG = -2147483648;

	// The hash table contains indices into the linkSlots array
	private int [] table;

	// All (key,value) pairs are chained into linked lists. The connection
	// information is stored in "linkSlots" along with the key's hash code
	// (for performance reasons).
	// TODO: get rid of the hash code in Link (this depends on a few
	// JIT-compiler optimizations)
	// Every link in "linkSlots" corresponds to the (key,value) pair
	// in "keySlots"/"valueSlots" with the same index.
	private HashLink [] linkSlots;
	private TKey [] keySlots;
	private TValue [] valueSlots;

	//Leave those 2 fields here to improve heap layout.
	private IEqualityComparer<TKey> hcp;
//	SerializationInfo serialization_info;

	// The number of slots in "linkSlots" and "keySlots"/"valueSlots" that
	// are in use (i.e. filled with data) or have been used and marked as
	// "empty" later on.
	private int touchedSlots;

	// The index of the first slot in the "empty slots chain".
	// "Remove()" prepends the cleared slots to the empty chain.
	// "Add()" fills the first slot in the empty slots chain with the
	// added item (or increases "touchedSlots" if the chain itself is empty).
	private int emptySlot;

	// The number of (key,value) pairs in this dictionary.
	private int count;

	// The number of (key,value) pairs the dictionary can hold without
	// resizing the hash table and the slots arrays.
	private int threshold;

	// The number of changes made to this dictionary. Used by enumerators
	// to detect changes and invalidate themselves.
	private int generation;
	
	private Class<? super TKey> keyClass = new TypeLiteral<TKey>() {}.getRawType();
	private Class<? super TValue> valueClass = new TypeLiteral<TValue>() {}.getRawType();


	public Dictionary() {
		init(INITIAL_SIZE, null);
	}

	public Dictionary(final IEqualityComparer<TKey> comparer) {
		init(INITIAL_SIZE, comparer);
	}
	
	public Dictionary(final IDictionary<TKey, TValue> dictionary) {
		this(dictionary, null);
	}
	
	public Dictionary(final Map<TKey, TValue> map) {
		this(map, null);
	}
	
	public Dictionary(final int capacity) {
		init(capacity, null);
	}
	
	public Dictionary(final Map<TKey, TValue> map, final IEqualityComparer<TKey> comparer) {
		checkNotNull(map);
		init(map.size(), comparer);
		for (Map.Entry<TKey, TValue> entry : map.entrySet()) {
			this.add(entry.getKey(), entry.getValue());
		}
	}

	public Dictionary(final IDictionary<TKey, TValue> dictionary, final IEqualityComparer<TKey> comparer) {
		checkNotNull(dictionary);
		init(dictionary.size(), comparer);
		this.add(dictionary);
	}

	public Dictionary(final int capacity, final IEqualityComparer<TKey> comparer) {
		init(capacity, comparer);
	}

	
	private void init(int capacity, final IEqualityComparer<TKey> hcp) {
		checkArgument(capacity >= 0, "capacity");
		this.hcp = (hcp != null) ? hcp : EqualityComparer.<TKey>getDefault();

		if (capacity == 0)
			capacity = INITIAL_SIZE;

		/* Modify capacity so 'capacity' elements can be added without resizing */
		capacity = (int) (capacity / DEFAULT_LOAD_FACTOR) + 1;
		initArrays(capacity);
		generation = 0;
	}

	@SuppressWarnings("unchecked")
	private TKey[] newKeyArray(final int size) {
		return (TKey[]) Array.newInstance(keyClass, size);
	}

	@SuppressWarnings("unchecked")
	private TValue[] newValueArray(final int size) {
		return (TValue[]) Array.newInstance(valueClass, size);
	}

	@SuppressWarnings("unchecked")
	private void initArrays(int size) {
		table = new int[size];
		
		linkSlots = new HashLink[size];
		emptySlot = NO_SLOT;

		keySlots = newKeyArray(size);
		valueSlots = newValueArray(size);
		touchedSlots = 0;

		threshold = (int)(table.length * DEFAULT_LOAD_FACTOR);
		if (threshold == 0 && table.length > 0)
			threshold = 1;
	}

	private void copyToCheck(final KeyValuePair<TKey, TValue>[] array, int index) {
		checkNotNull(array, "array");
		if (index < 0)
			throw new IndexOutOfBoundsException("index");

		checkPositionIndex(index, array.length, "index larger than largest valid index of array");
		if (array.length - index < size())
			throw new IllegalArgumentException("Destination array cannot hold the requested elements!");
	}

	private void copyKeys(final TKey[] array, int index) {
		for (int i = 0; i < touchedSlots; i++) {
			if ((linkSlots[i].hashCode & HASH_FLAG) != 0)
				array[index++] = keySlots[i];
		}
	}

	private void copyValues(final TValue[] array, int index) {
		for (int i = 0; i < touchedSlots; i++) {
			if ((linkSlots[i].hashCode & HASH_FLAG) != 0)
				array[index++] = valueSlots[i];
		}
	}

	protected static <TKey, TValue> KeyValuePair<TKey, TValue> makePair(final TKey key, final TValue value) {
		return new KeyValuePair<TKey, TValue>(key, value);
	}

	@Override
	public IDictionary<TKey, TValue> add(final KeyValuePair<TKey, TValue> item) {
		checkNotNull(item);
		add(item.getKey(), item.getValue());
		return this;
	}

	@Override
	public IDictionary<TKey, TValue> add(final TKey key, final TValue value) {
		checkNotNull(key, "key");
		
		// get first item of linked list corresponding to given key
		int hashCode = hcp.getHashCode(key) | HASH_FLAG;
		int index = (hashCode & Integer.MAX_VALUE) % table.length;
		int cur = table[index] - 1;

		// walk linked list until end is reached (throw an exception if a
		// existing slot is found having an equivalent key)
		while (cur != NO_SLOT) {
			// The ordering is important for compatibility with MS and strange
			// Object.Equals () implementations
			if (linkSlots[cur].hashCode == hashCode && hcp.equals(keySlots[cur], key))
				throw new IllegalArgumentException("An element with the same key already exists in the dictionary.");

			cur = linkSlots[cur].next;
		}

		if (++count > threshold) {
			resize();
			index = (hashCode & Integer.MAX_VALUE) % table.length;
		}

		// find an empty slot
		cur = emptySlot;
		if (cur == NO_SLOT)
			cur = touchedSlots++;
		else
			emptySlot = linkSlots[cur].next;

		// store the hash code of the added item,
		// prepend the added item to its linked list,
		// update the hash table
		linkSlots[cur] = new HashLink();
		linkSlots[cur].hashCode = hashCode;
		linkSlots[cur].next = table[index] - 1;
		table[index] = cur + 1;

		// Store item's data
		keySlots[cur] = key;
		valueSlots[cur] = value;

		generation++;
		
		return this;
	}

	@Override
	public IDictionary<TKey, TValue> add(final IDictionary<? extends TKey, ? extends TValue> items) {
		checkNotNull(items);
		for (KeyValuePair<? extends TKey, ? extends TValue> item : items) {
			if (this.containsKey(item.getKey()))
				this.set(item.getKey(), item.getValue());
			else
				this.add(item.getKey(), item.getValue());
		}
		return this;
	}

	@Override
	public IDictionary<TKey, TValue> add(final Map<TKey, TValue> items) {
		checkNotNull(items);
		for (Map.Entry<TKey, TValue> item : items.entrySet()) {
			if (this.containsKey(item.getKey()))
				this.set(item.getKey(), item.getValue());
			else
				this.add(item.getKey(), item.getValue());
		}
		return this;
	}

	@Override
	public IDictionary<TKey, TValue> clear() {
		count = 0;
		// clear the hash table
		table = new int[table.length];
		// clear arrays
		keySlots = newKeyArray(keySlots.length);
		valueSlots = newValueArray(valueSlots.length);
		Arrays.fill(linkSlots, null);

		// empty the "empty slots chain"
		emptySlot = NO_SLOT;

		touchedSlots = 0;
		generation++;

		return this;
	}

	@Override
	public boolean contains(final KeyValuePair<TKey, TValue> item) {
		checkNotNull(item, "item");

		final Optional<TValue> optional = tryGetValue(item.getKey());
		return optional.isPresent() && EqualityComparer.<TValue>getDefault().equals(item.getValue(), optional.get());
	}

	@Override
	public boolean containsKey(final TKey key) {
		checkNotNull(key, "key");
		
		// get first item of linked list corresponding to given key
		int hashCode = hcp.getHashCode(key) | HASH_FLAG;
		int cur = table[(hashCode & Integer.MAX_VALUE) % table.length] - 1;

		// walk linked list until right slot is found or end is reached
		while(cur != NO_SLOT) {
			// The ordering is important for compatibility with MS and strange
			// Object.Equals () implementations
			if (linkSlots[cur].hashCode == hashCode && hcp.equals(keySlots[cur], key))
				return true;

			cur = linkSlots[cur].next;
		}
		
		return false;
	}

	@Override
	public boolean containsValue(final TValue value) {
		final EqualityComparer<TValue> cmp = EqualityComparer.<TValue>getDefault();
		for (int i = 0; i < table.length; i++) {
			int cur = table[i] - 1;
			while (cur != NO_SLOT) {
				if (cmp.equals(valueSlots[cur], value))
					return true;
				cur = linkSlots[cur].next;
			}
		}
		return false;
	}

	@Override
	public IDictionary<TKey, TValue> copyTo(final KeyValuePair<TKey, TValue>[] array, int arrayIndex) {
		copyToCheck(array, arrayIndex);
		for (int i = 0; i < touchedSlots; i++) {
			if ((linkSlots[i].hashCode & HASH_FLAG) != 0)
				array[arrayIndex++] = makePair(keySlots[i], valueSlots[i]);
		}
		return this;
	}

	@Override
	public IDictionary<TKey, TValue> filterEntries(final Predicate<KeyValuePair<TKey, TValue>> filter) {
		final Dictionary<TKey, TValue> dictionary = new Dictionary<>(size(), getComparer());
		for (KeyValuePair<TKey, TValue> pair : this) {
			if (filter.apply(pair))
				dictionary.add(pair);
		}
		return dictionary;
	}

	@Override
	public TValue get(final TKey key) {
		checkNotNull(key);

		// get first item of linked list corresponding to given key
		int hashCode = hcp.getHashCode(key) | HASH_FLAG;
		int cur = table[(hashCode & Integer.MAX_VALUE) % table.length] - 1;

		// walk linked list until right slot is found or end is reached
		while (cur != NO_SLOT) {
			// The ordering is important for compatibility with MS and strange
			// Object.Equals () implementations
			if (linkSlots[cur].hashCode == hashCode && hcp.equals(keySlots[cur], key))
				return valueSlots[cur];

			cur = linkSlots[cur].next;
		}

		throw new KeyNotFoundException();
	}

	@Override
	public IEqualityComparer<TKey> getComparer() {
		return hcp;
	}

	@Override
	public boolean isEmpty() {
		return this.count == 0;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public IDictionary<TKey, TValue> put(final TKey tKey, final TValue tValue) {
		if (this.containsKey(tKey))
			this.set(tKey, tValue);
		else
			this.add(tKey, tValue);
		return this;
	}

	@Override @TODO("When setting, ensure that new additions to linkSlots gets a new HashLink object")
	public IDictionary<TKey, TValue> set(final TKey key, final TValue value) {
		checkNotNull(key);

		// get first item of linked list corresponding to given key
		int hashCode = hcp.getHashCode(key) | HASH_FLAG;
		int index = (hashCode & Integer.MAX_VALUE) % table.length;
		int cur = table[index] - 1;

		// walk linked list until right slot (and its predecessor) is
		// found or end is reached
		int prev = NO_SLOT;
		if (cur != NO_SLOT) {
			do {
				// The ordering is important for compatibility with MS and strange
				// Object.Equals () implementations
				if (linkSlots[cur].hashCode == hashCode && hcp.equals(keySlots[cur], key))
					break;

				prev = cur;
				cur = linkSlots[cur].next;
			}
			while(cur != NO_SLOT);
		}

		// is there no slot for the given key yet?
		if (cur == NO_SLOT) {
			// there is no existing slot for the given key,
			// allocate one and prepend it to its corresponding linked
			// list

			if (++count > threshold) {
				resize();
				index = (hashCode & Integer.MAX_VALUE) % table.length;
			}

			// find an empty slot
			cur = emptySlot;
			if (cur == NO_SLOT)
				cur = touchedSlots++;
			else
				emptySlot = linkSlots[cur].next;


			// prepend the added item to its linked list,
			// update the hash table
			linkSlots[cur].next = table[index] - 1;
			table[index] = cur + 1;
			
			// store the new item and its hash code
			linkSlots[cur].hashCode = hashCode;
			keySlots[cur] = key;
		}
		else {
			// we already have a slot for the given key,
			// update the existing slot

			// if the slot is not at the front of its linked list,
			// we move it there
			if (prev != NO_SLOT) {
				linkSlots[prev].next = linkSlots[cur].next;
				linkSlots[cur].next = table[index] - 1;
				table[index] = cur + 1;
			}
		}

		// store the item's data itself
		valueSlots[cur] = value;
		generation++;

		return this;
	}

	private void resize() {
		// From the SDK docs:
		//	 Hashtable is automatically increased
		//	 to the smallest prime number that is larger
		//	 than twice the current number of Hashtable buckets
		int newSize = toPrime((table.length << 1) | 1);
		
		// allocate new hash table and link slots array
		int[] newTable = new int[newSize];
		HashLink[] newLinkSlots = new HashLink[newSize];
		
		for (int i = 0; i < table.length; i++) {
			int cur = table[i] - 1;
			while (cur != NO_SLOT) {
				int hashCode = newLinkSlots[cur].hashCode = hcp.getHashCode(keySlots[cur]) | HASH_FLAG;
				int index = (hashCode & Integer.MAX_VALUE) % newSize;
				newLinkSlots[cur].next = newTable[index] - 1;
				newTable[index] = cur + 1;
				cur = linkSlots[cur].next;
			}
		}
		table = newTable;
		linkSlots = newLinkSlots;

		// Allocate new data slots, copy data
		TKey[] newKeySlots = newKeyArray(newSize);
		TValue[] newValueSlots = newValueArray(newSize);
		System.arraycopy(keySlots, 0, newKeySlots, 0, keySlots.length);
		System.arraycopy(valueSlots, 0, newValueSlots, 0, valueSlots.length);
		keySlots = newKeySlots;
		valueSlots = newValueSlots;

		threshold = (int) (newSize * DEFAULT_LOAD_FACTOR);
	}

	@Override
	public boolean remove(final KeyValuePair<TKey, TValue> item) {
		return contains(item) && remove(item.getKey());
	}

	@Override
	public boolean remove(final TKey key) {
		checkNotNull(key, "key");

		// get first item of linked list corresponding to given key
		int hashCode = hcp.getHashCode(key) | HASH_FLAG;
		int index = (hashCode & Integer.MAX_VALUE) % table.length;
		int cur = table[index] - 1;

		// if there is no linked list, return false
		if (cur == NO_SLOT)
			return false;

		// walk linked list until right slot (and its predecessor) is
		// found or end is reached
		int prev = NO_SLOT;
		do {
			// The ordering is important for compatibility with MS and strange
			// Object.Equals () implementations
			if (linkSlots[cur].hashCode == hashCode && hcp.equals(keySlots[cur], key))
				break;
			prev = cur;
			cur = linkSlots[cur].next;
		} while (cur != NO_SLOT);

		// if we reached the end of the chain, return false
		if (cur == NO_SLOT)
			return false;

		count--;
		// remove slot from linked list
		// is slot at beginning of linked list?
		if (prev == NO_SLOT)
			table[index] = linkSlots[cur].next + 1;
		else
			linkSlots[prev].next = linkSlots[cur].next;

		// mark slot as empty and prepend it to "empty slots chain"
		linkSlots[cur].next = emptySlot;
		emptySlot = cur;

		linkSlots[cur].hashCode = 0;
		// clear empty key and value slots
		keySlots[cur] = null;
		valueSlots[cur] = null;

		generation++;
		return true;
	}

	@Override
	public int size() {
		return count;
	}

	@Override
	public Map<TKey, TValue> toMap() {
		final Map<TKey, TValue> map = Maps.newHashMapWithExpectedSize(size());
		for (KeyValuePair<TKey, TValue> pair : this) {
			map.put(pair.getKey(), pair.getValue());
		}
		return map;
	}

	@Override
	public Optional<TValue> tryGetValue(final TKey key) {
		checkNotNull(key, "key");

		TValue value = null;

		// get first item of linked list corresponding to given key
		int hashCode = hcp.getHashCode(key) | HASH_FLAG;
		int cur = table [(hashCode & Integer.MAX_VALUE) % table.length] - 1;

		// walk linked list until right slot is found or end is reached
		while (cur != NO_SLOT) {
			// The ordering is important for compatibility with MS and strange
			// Object.Equals () implementations
			if (linkSlots[cur].hashCode == hashCode && hcp.equals(keySlots[cur], key)) {
				value = valueSlots [cur];
				break;
			}
			cur = linkSlots[cur].next;
		}

		return Optional.fromNullable(value);
	}

	@Override
	public boolean tryGetValue(final TKey key, OutParam<TValue> value) {
		checkNotNull(value, "value");
		final Optional<TValue> optional = tryGetValue(key);
		value.set(optional.get());
		return optional.isPresent();
	}

	@Override
	public Iterator<KeyValuePair<TKey, TValue>> iterator() {
		return new DictionaryIterator<TKey, TValue>(this);
	}

	private static class DictionaryIterator<TKey, TValue> implements Iterator<KeyValuePair<TKey, TValue>> {
		
		private final Dictionary<TKey, TValue> dictionary;
		int next;
		int stamp;
		
		private KeyValuePair<TKey, TValue> current;

		private DictionaryIterator(Dictionary<TKey, TValue> dictionary) {
			this.dictionary = dictionary;
			stamp = dictionary.generation;
		}

		@Override
		public boolean hasNext() {
			verifyState();
			if (next < 0)
				return false;
			
			while(next < dictionary.touchedSlots) {
				int cur = next++;
				if ((dictionary.linkSlots[cur].hashCode & HASH_FLAG) != 0) {
					current = makePair(dictionary.keySlots[cur], dictionary.valueSlots[cur]);
					return true;
				}
			}
			next = -1;
			return false;
		}

		@Override
		public KeyValuePair<TKey, TValue> next() {
			return current;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private void verifyState() {
			if (dictionary == null)
				throw new IllegalStateException("dictionary may be disposed.");
			if (dictionary.generation != stamp)
				throw new IllegalStateException("out of sync");
		}

	}


	static int toPrime(int x) {
		for (int i = 0; i < primeTbl.length; i++) {
			if (x <= primeTbl[i])
				return primeTbl[i];
		}
		return calcPrime(x);
	}
	
	private static int calcPrime(int x) {
		for (int i = (x & (~1)) - 1; i < Integer.MAX_VALUE; i++) {
			if (testPrime(i)) return i;
		}
		return x;
	}

	private static boolean testPrime(int x) {
		if ((x & 1) != 0){
			int top = IntMath.sqrt(x, RoundingMode.FLOOR);
			
			for (int n = 3; n < top; n += 2) {
				if ((x % n) == 0)
					return false;
			}
			return true;
		}
		// There is only one even prime - 2. And only 1 Optimus Prime!
		return (x == 2);
	}


	private static final int [] primeTbl = {
		11,
		19,
		37,
		73,
		109,
		163,
		251,
		367,
		557,
		823,
		1237,
		1861,
		2777,
		4177,
		6247,
		9371,
		14057,
		21089,
		31627,
		47431,
		71143,
		106721,
		160073,
		240101,
		360163,
		540217,
		810343,
		1215497,
		1823231,
		2734867,
		4102283,
		6153409,
		9230113,
		13845163
	};

	public static <TKey, TValue> Dictionary<TKey, TValue> asDictionary(final Iterable<TValue> iterable, final Function<TValue, TKey> keyFunction) {
		return asDictionary(iterable, keyFunction, null);
	}
	
	public static <TKey, TValue> Dictionary<TKey, TValue> asDictionary(final Iterable<TValue> iterable, final Function<TValue, TKey> keyFunction, final IEqualityComparer<TKey> equalityComparer) {
		checkNotNull(iterable, "iterable");
		checkNotNull(keyFunction, "keyFunction");

		final Dictionary<TKey, TValue> dictionary = new Dictionary<>(Iterables.size(iterable), equalityComparer);
		for (TValue tValue : iterable) {
			dictionary.add(keyFunction.apply(tValue), tValue);
		}
		return dictionary;
	}
}
