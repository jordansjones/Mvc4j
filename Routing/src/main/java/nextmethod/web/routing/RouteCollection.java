package nextmethod.web.routing;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nextmethod.web.IHttpContext;
import nextmethod.web.VirtualPathProvider;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 */
public class RouteCollection implements Iterable<RouteBase> {

	private final Provider<VirtualPathProvider> virtualPathProvider;
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final ReentrantReadWriteLock.ReadLock readLock = rwl.readLock();
	private final ReentrantReadWriteLock.WriteLock writeLock = rwl.writeLock();

	private boolean routeExistingFiles;

	@Inject
	public RouteCollection(final Provider<VirtualPathProvider> virtualPathProvider) {
		this.virtualPathProvider = virtualPathProvider;
	}

	private RouteBase findRouteWithName(final String routeName) {
		checkNotNull(routeName);
		readLock.lock();
		try {
			if (routeItems.isEmpty())
				return null;

			if (!routeItems.containsKey(routeName))
				return null;

			return routeItems.get(routeName);
		}
		finally {
			readLock.unlock();
		}
	}

	public boolean isRouteExistingFiles() {
		return routeExistingFiles;
	}

	public void setRouteExistingFiles(boolean routeExistingFiles) {
		this.routeExistingFiles = routeExistingFiles;
	}

	private final List<RouteBase> routes = Lists.newArrayList();
	private final Map<String, RouteBase> routeItems = Maps.newHashMap();

	public void add(final RouteBase item) {
		checkNotNull(item);
		final int hashCode = Objects.hashCode(item);
		writeLock.lock();
		try {
			routeItems.put(String.valueOf(hashCode), item);
			routes.add(item);
		}
		finally {
			writeLock.unlock();
		}
	}

	public void add(final String name, final RouteBase item) {
		checkNotNull(name);
		checkNotNull(item);
		writeLock.lock();
		try {
			routeItems.put(name, item);
			routes.add(item);
		}
		finally {
			writeLock.unlock();
		}
	}

	public void clear() {
		writeLock.lock();
		try {
			routeItems.clear();
			routes.clear();
		}
		finally {
			writeLock.unlock();
		}
	}

//	public Lock getReadLock() {
//		return readLock;
//	}

	public RouteData getRouteData(final IHttpContext httpContext) {
		checkNotNull(httpContext);
		checkNotNull(httpContext.getRequest());

		final ImmutableCollection<RouteBase> routes;
		readLock.lock();
		try {
			routes = ImmutableList.copyOf(this.routes);
		}
		finally {
			readLock.unlock();
		}

		if (routes.isEmpty())
			return null;

		if (!routeExistingFiles) {
			final String path = httpContext.getRequest().getAppRelativeCurrentExecutionFilePath();
			final VirtualPathProvider vpp = virtualPathProvider.get();
			if (!path.equalsIgnoreCase("~/") && vpp != null && (vpp.fileExists(path) || vpp.directoryExists(path))) {
				return null;
			}
		}

		for (RouteBase routeBase : routes) {
			final RouteData rd = routeBase.getRouteData(httpContext);
			if (rd != null)
				return rd;
		}

		return null;
	}

	public VirtualPathData getVirtualPath(final RequestContext requestContext, final RouteValueDictionary routeValueDictionary) {
		return getVirtualPath(requestContext, null, routeValueDictionary);
	}

	public VirtualPathData getVirtualPath(final RequestContext requestContext, @Nullable final String name, final RouteValueDictionary values) {
		checkNotNull(requestContext);
		readLock.lock();
		try {
			if (routeItems.isEmpty())
				return null;

			VirtualPathData pathData = null;
			if (!Strings.isNullOrEmpty(name)) {
				final RouteBase routeBase = findRouteWithName(name);
				if (routeBase != null) {
					pathData = routeBase.getVirtualPath(requestContext, values);
				} else {
					throw new IllegalArgumentException(String.format("Invalid route name: %s", name));
				}
			} else {
				for (RouteBase routeBase : this) {
					pathData = routeBase.getVirtualPath(requestContext, values);
					if (pathData != null)
						break;
				}
			}

			if (pathData != null) {
				String appPath = requestContext.getHttpContext().getApplicationPath();
				if (appPath != null && (appPath.length() == 0 || !appPath.endsWith("/")))
					appPath += "/";

				final String pathWithApp = String.format("%s%s", appPath, pathData.getVirtualPath());
				pathData.setVirtualPath(requestContext.getHttpContext().applyApplicationPathModifier(pathWithApp));
				return pathData;
			}

			return null;
		}
		finally {
			readLock.unlock();
		}
	}

//	public Lock getWriteLock() {
//		return writeLock;
//	}


	@Override
	public Iterator<RouteBase> iterator() {
		readLock.lock();
		try {
			return Iterators.unmodifiableIterator(this.routes.iterator());
		}
		finally {
			readLock.unlock();
		}
	}
}
