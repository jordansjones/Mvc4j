package nextmethod.web.razor.text;

import javax.annotation.Nonnull;
import java.util.Objects;

public class LocationTagged<T> {

	private final SourceLocation location;
	private final T value;

	private LocationTagged() {
		this.location = SourceLocation.Undefined;
		this.value = null;
	}

	public LocationTagged(@Nonnull final T value, final int offset, final int line, final int col) {
		this(value, new SourceLocation(offset, line, col));
	}

	public LocationTagged(@Nonnull final T value, @Nonnull final SourceLocation location) {
		Objects.requireNonNull(value);

		this.location = location;
		this.value = value;
	}

	public SourceLocation getLocation() {
		return location;
	}

	public T getValue() {
		return value;
	}

	@Override
	public String toString() {
		return Objects.toString(value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LocationTagged that = (LocationTagged) o;

		if (location != null ? !location.equals(that.location) : that.location != null) return false;
		if (value != null ? !value.equals(that.value) : that.value != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = location != null ? location.hashCode() : 0;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}
}
