package nextmethod.web.mvc;

import com.google.common.base.Strings;
import com.google.common.io.Resources;
import nextmethod.OutParam;
import nextmethod.web.VirtualPathProvider;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
class Mvc4jVirtualPathProvider implements Provider<VirtualPathProvider> {

	private final ServletContext servletContext;

	@Inject
	Mvc4jVirtualPathProvider(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	public VirtualPathProvider get() {
		return new ServletResourceVirtualPathProvider(this.servletContext);
	}

	private static class ServletResourceVirtualPathProvider extends VirtualPathProvider {

		private static final String WebInfFolder = "/WEB-INF";

		private final ServletContext context;

		private String getServletContainer() {
			return context.getServerInfo();
		}

		private ServletResourceVirtualPathProvider(final ServletContext context) {
			this.context = context;
		}

		@Override
		public String combineVirtualPaths(final String basePath, final String relativePath) {
			return null;
		}

		@Override
		public boolean directoryExists(final String virtualDir) {
			return false;
		}

		@Override
		public boolean fileExists(final String virtualPath) {
			if (Strings.isNullOrEmpty(virtualPath))
				return false;

			final String path = virtualPath.charAt(0) == '~' ? virtualPath.substring(1) : virtualPath;
			final OutParam<URL> url = OutParam.of(URL.class);

			if (!tryGetContextResource(path, url))
				tryGetClassLoaderResource(path, url);

			return url.get() != null;
		}

		private boolean tryGetClassLoaderResource(final String resource, final OutParam<URL> url) {
			URL r = null;
			try {
				r = Resources.getResource(resource);
				if (r == null)
					r = Resources.getResource(String.format("%s%s", WebInfFolder, resource));
			}
			catch (Exception ignored) {
			}

			if (r != null)
				url.set(r);

			return r != null;
		}

		private boolean tryGetContextResource(final String resource, final OutParam<URL> url) {
			URL r = null;
			try {
				r = this.context.getResource(resource);
				if (r == null)
					r = this.context.getResource(String.format("%s%s", WebInfFolder, resource));
			}
			catch (MalformedURLException ignored) {
			}

			if (r != null)
				url.set(r);

			return r != null;
		}

		@Override
		public String getFileHash(final String virtualPath, final Iterable<Object> virtualPathDependencies) {
			return null;
		}

		@Override
		protected void initialize() {
		}
	}
}
