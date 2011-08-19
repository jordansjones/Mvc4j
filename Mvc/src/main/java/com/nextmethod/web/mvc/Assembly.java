package com.nextmethod.web.mvc;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 *
 */
class Assembly {

	public static final String Ungrouped = "MVC4j-Ungrouped-Classes";

	private final String name;
	private final Set<AssemblyType<?>> entries;

	Assembly(final String name) {
		this.name = name;
		this.entries = Sets.newHashSet();
	}

	public String getName() {
		return name;
	}

	public Set<AssemblyType<?>> getEntries() {
		return entries;
	}

	@SuppressWarnings({"RedundantIfStatement"})
	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof Assembly)) return false;

		final Assembly assembly = (Assembly) o;

		if (entries != null ? !entries.equals(assembly.entries) : assembly.entries != null) return false;
		if (name != null ? !name.equals(assembly.name) : assembly.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (entries != null ? entries.hashCode() : 0);
		return result;
	}
}
