package nextmethod.web.mvc;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import nextmethod.annotations.TODO;
import nextmethod.reflection.AssemblyInfo;

/**
 *
 */
@TODO("This class should walk the ServletContext Resource paths and load/analyze Classes and JARs")
final class BuildManager implements IBuildManager {

//	private BuildManager() {
//	}

	@Override
	public Object createInstanceFromVirtualPath(final String virtualPath, final Class<?> requiredBaseType) {
		return null;
	}

	@Override
	public ImmutableCollection<AssemblyInfo> getReferencedAssemblies() {
//		return ImmutableList.copyOf(assemblies);
		return ImmutableList.of();
	}

	@Override
	public void readCachedFile(final String fileName) {
	}

	@Override
	public void createCachedFile(final String fileName) {
	}

}
