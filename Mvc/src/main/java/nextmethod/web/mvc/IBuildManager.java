package nextmethod.web.mvc;

import com.google.common.collect.ImmutableCollection;
import nextmethod.reflection.AssemblyInfo;

/**
 *
 */
interface IBuildManager {

	Object createInstanceFromVirtualPath(String virtualPath, Class<?> requiredBaseType);

	ImmutableCollection<AssemblyInfo> getReferencedAssemblies();

	void readCachedFile(String fileName);

	void createCachedFile(String fileName);

}
