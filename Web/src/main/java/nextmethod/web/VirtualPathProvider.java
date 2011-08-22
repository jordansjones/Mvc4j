package nextmethod.web;

/**
 *
 */
public abstract class VirtualPathProvider {

	public abstract String combineVirtualPaths(final String basePath, final String relativePath);

	public abstract boolean directoryExists(final String virtualDir);

	public abstract boolean fileExists(final String virtualPath);

	// TODO: GetCacheDependency
	// TODO: GetCacheKey
	// TODO: GetDirectory
	// TODO: GetFile
	public abstract String getFileHash(final String virtualPath, final Iterable<Object> virtualPathDependencies);

	protected abstract void initialize();
	// TODO: OpenFile

}
