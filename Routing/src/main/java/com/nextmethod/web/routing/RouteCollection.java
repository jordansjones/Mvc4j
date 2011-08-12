package com.nextmethod.web.routing;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.nextmethod.web.IHttpContext;
import com.nextmethod.web.VirtualPathProvider;
import com.sun.xml.internal.xsom.impl.scd.Iterators;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 7:38 PM
 */
public class RouteCollection implements Iterable<RouteBase> {

	private final VirtualPathProvider virtualPathProvider;
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final ReentrantReadWriteLock.ReadLock readLock = rwl.readLock();
	private final ReentrantReadWriteLock.WriteLock writeLock = rwl.writeLock();

	private boolean routeExistingFiles;

	public RouteCollection() {
		this(null);
	}

	public RouteCollection(@Nullable final VirtualPathProvider virtualPathProvider) {
		this.virtualPathProvider = virtualPathProvider;
	}

	private RouteBase findRouteWithName(final String routeName) {
		return null;
	}

	public boolean isRouteExistingFiles() {
		return routeExistingFiles;
	}

	public void setRouteExistingFiles(boolean routeExistingFiles) {
		this.routeExistingFiles = routeExistingFiles;
	}

	private final Map<String, RouteBase> routeItems = Maps.newHashMap();

	public void add(final String name, final RouteBase item) {
		checkNotNull(item);
		writeLock.lock();
		try {
			super.add(item);
			if (!Strings.isNullOrEmpty(name)) {
				routeItems.put(name, item);
			}
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void clear() {
		writeLock.lock();
		try {
			super.clear();
		} finally {
			writeLock.unlock();
		}
	}

	public Lock getReadLock() {
		return readLock;
	}

	public RouteData getRouteData(final IHttpContext httpContext) {
		checkNotNull(httpContext);
		checkNotNull(httpContext.getRequest());

		if (size() == 0)
			return null;

		if (!routeExistingFiles) {
//			final String path = httpContext.getRequest().getAppRelativeCurrentExecutionFilePath();
			// TODO: WTF?
		}


		return null;
	}

	public VirtualPathData getVirtualPath(final RequestContext requestContext, final RouteValueDictionary routeValueDictionary) {
		return getVirtualPath(requestContext, null, routeValueDictionary);
	}

	public VirtualPathData getVirtualPath(final RequestContext requestContext, @Nullable final String name, final RouteValueDictionary values) {
		checkNotNull(requestContext);
		if (size() == 0)
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

	public Lock getWriteLock() {
		return writeLock;
	}


	@Override
	public Iterator<RouteBase> iterator() {
		return Iterators.
	}
}
