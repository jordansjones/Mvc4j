package nextmethod.web.mvc;

import com.google.common.collect.ImmutableCollection;

/**
 *
 */
interface IBuildManager {

	Object createInstanceFromVirtualPath(String virtualPath, Class<?> requiredBaseType);

	ImmutableCollection<Assembly> getReferencedAssemblies();

	void readCachedFile(String fileName);

	void createCachedFile(String fileName);

}
