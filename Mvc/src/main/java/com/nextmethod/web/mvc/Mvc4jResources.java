package com.nextmethod.web.mvc;

import com.google.common.collect.ImmutableMap;

import java.util.ResourceBundle;

/**
 *
 */
final class Mvc4jResources {

	private Mvc4jResources() {
	}

	private static final ImmutableMap<Resources, ResourceBundle> bundles = createBundleMap();

	static enum Resources {

		MvcResources("com.nextmethod.web.mvc.MvcResources"),
		Version("com.nextmethod.web.mvc.Version");

		private final String bundle;

		Resources(final String bundleName) {
			this.bundle = bundleName;
		}

		@Override
		public String toString() {
			return bundle;
		}
	}

	public static ResourceBundle MvcResources() {
		return bundles.get(Resources.MvcResources);
	}

	public static ResourceBundle Version() {
		return bundles.get(Resources.Version);
	}

	private static ImmutableMap<Resources, ResourceBundle> createBundleMap() {
		final ImmutableMap.Builder<Resources, ResourceBundle> builder = ImmutableMap.builder();
		for (Resources r : Resources.values()) {
			builder.put(r, ResourceBundle.getBundle(r.toString()));
		}
		return builder.build();
	}
}
