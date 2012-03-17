package nextmethod.collect;

public class KeyValuePair<TKey, TValue> {
	
	private final TKey key;
	private final TValue value;

	public KeyValuePair(TKey key, TValue value) {
		this.key = key;
		this.value = value;
	}

	public TKey getKey() {
		return key;
	}

	public TValue getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		KeyValuePair that = (KeyValuePair) o;

		if (key != null ? !key.equals(that.key) : that.key != null) return false;
		if (value != null ? !value.equals(that.value) : that.value != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = key != null ? key.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb
			.append("[")
			.append(key != null ? key.toString() : "")
			.append(", ").append(value != null ? value.toString() : "")
			.append(']');
		return sb.toString();
	}
}
