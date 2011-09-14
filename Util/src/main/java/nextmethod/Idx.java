package nextmethod;

import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import java.text.Normalizer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides a consistent way of getting a "Key" for hash lookups, etc.
 */
public final class Idx {

	private final Supplier<Integer> supplier;

	private Idx(final Supplier<Integer> supplier) {
		this.supplier = checkNotNull(supplier);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof Idx)) return false;

		final Idx idx = (Idx) o;
		return this.supplier.get().equals(idx.supplier.get());
	}

	@Override
	public int hashCode() {
		return this.supplier.get().hashCode();
	}

	public static Idx of(String val) {
		val = Strings.nullToEmpty(val);
		final String normalized = Normalizer.normalize(val, Normalizer.Form.NFC);
		return new Idx (Suppliers.ofInstance(normalized.toUpperCase().hashCode()));
	}

	public static Idx of(int val) {
		return new Idx(Suppliers.ofInstance(val));
	}

	public static Idx of(long val) {
		return new Idx(Suppliers.ofInstance(Long.valueOf(val).hashCode()));
	}

}
