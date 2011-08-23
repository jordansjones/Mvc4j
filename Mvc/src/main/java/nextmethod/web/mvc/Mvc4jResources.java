package nextmethod.web.mvc;

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

		MvcResources,
		Version;

		private final String bundle;

		Resources() {
			final String name = this.name();
			final String packageName = this.getClass().getPackage().getName();
			this.bundle = String.format("%s.%s", packageName, name);
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
