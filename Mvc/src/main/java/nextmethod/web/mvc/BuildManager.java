package nextmethod.web.mvc;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import nextmethod.annotations.TODO;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletContext;

/**
 *
 */
@TODO("This class should walk the ServletContext Resource paths and load/analyze Classes and JARs")
final class BuildManager implements IBuildManager {

	@Inject
	private Provider<ServletContext> ctxProvider;

	private Object cache = null;

	private void ensureIntialized() {
		if (cache == null) {
			cache = new Object();
		}
	}

	@Override
	public Object createInstanceFromVirtualPath(final String virtualPath, final Class<?> requiredBaseType) {
		ensureIntialized();
		return null;
	}

	@Override
	public ImmutableCollection<Assembly> getReferencedAssemblies() {
		ensureIntialized();
		return ImmutableList.of();
	}

	@Override
	public void readCachedFile(final String fileName) {
		ensureIntialized();
	}

	@Override
	public void createCachedFile(final String fileName) {
		ensureIntialized();
	}
}
