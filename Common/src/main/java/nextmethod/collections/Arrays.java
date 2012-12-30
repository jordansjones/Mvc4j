package nextmethod.collections;

import com.google.common.base.Predicate;
import nextmethod.base.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Arrays {
	private Arrays() {}

	public static <T> boolean all(@Nullable final T[] arr, @Nonnull final Predicate<T> predicate) {
		if (arr == null || arr.length == 0) { return true; }
		checkNotNull(predicate);
		for (T t : arr) {
			if (!predicate.apply(t)) {
				return false;
			}
		}
		return true;
	}

	public static <T> boolean any(@Nullable final T[] arr, @Nonnull final Predicate<T> predicate) {
		if (arr == null || arr.length == 0) { return false; }
		checkNotNull(predicate);
		for (T t : arr) {
			if (predicate.apply(t)) {
				return true;
			}
		}
		return false;
	}

	public static Character[] asCharacterArray(@Nullable final String value) {
		if (Strings.isNullOrEmpty(value)) {
			return new Character[0];
		}

		final int numChars = value.length();
		final Character[] characters = new Character[numChars];
		for (int i = 0; i < numChars; i++) {
			characters[i] = value.charAt(i);
		}
		return characters;
	}
}
