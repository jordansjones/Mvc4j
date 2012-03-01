package nextmethod.reflection;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 *
 */
public class AssemblyInfo {

	private final String name;
	private Set<ClassInfo<?>> entries;

	public AssemblyInfo(final String name) {
		this.name = name;
		this.entries = Sets.newHashSet();
	}

	public String getName() {
		return name;
	}

	public Set<ClassInfo<?>> getEntries() {
		return entries;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof AssemblyInfo)) return false;

		final AssemblyInfo that = (AssemblyInfo) o;

		if (!entries.equals(that.entries)) return false;

		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = name.hashCode();
		result = 31 * result + entries.hashCode();
		return result;
	}
}
