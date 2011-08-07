package com.nextmethod.routing;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.nextmethod.web.HttpContext;
import com.nextmethod.web.VirtualPathProvider;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractCollection;
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
public class RouteCollection extends AbstractCollection<RouteBase> {

	private final VirtualPathProvider virtualPathProvider;
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final ReentrantReadWriteLock.ReadLock readLock = rwl.readLock();
	private final ReentrantReadWriteLock.WriteLock writeLock = rwl.writeLock();

	public RouteCollection() {
		this(null);
	}

	public RouteCollection(@Nullable final VirtualPathProvider virtualPathProvider) {
		this.virtualPathProvider = virtualPathProvider;
	}

	private RouteBase findRouteWithName(final String routeName) {
		return null;
	}

	@Override
	public Iterator<RouteBase> iterator() {
		return null;
	}

	@Override
	public int size() {
		return 0;
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

	public RouteData getRouteData(final HttpContext httpContext) {
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

	public void ignore(final String url) {
		ignore(url, null);
	}

	public void ignore(final String url, @Nullable final Object constraints) {
		checkNotNull(url);
		add(new Route(url, null, new RouteValueDictionary(constraints), new StopRoutingHandler()));
	}

	public Route mapPageRoute(final String routeName, final String routeUrl, final String physicalFile) {
		return mapPageRoute(routeName, routeUrl, physicalFile, true);
	}

	public Route mapPageRoute(final String routeName, final String routeUrl, final String physicalFile, final boolean checkPhysicalUrlAccess) {
		return mapPageRoute(routeName, routeUrl, physicalFile, checkPhysicalUrlAccess, null);
	}

	public Route mapPageRoute(final String routeName, final String routeUrl, final String physicalFile, final boolean checkPhysicalUrlAccess, @Nullable final RouteValueDictionary defaults) {
		return mapPageRoute(routeName, routeUrl, physicalFile, checkPhysicalUrlAccess, defaults, null);
	}

	public Route mapPageRoute(final String routeName, final String routeUrl, final String physicalFile, final boolean checkPhysicalUrlAccess, final RouteValueDictionary defaults, @Nullable final RouteValueDictionary constraints) {
		return mapPageRoute(routeName, routeUrl, physicalFile, checkPhysicalUrlAccess, defaults, constraints, null);
	}

	public Route mapPageRoute(final String routeName, final String routeUrl, final String physicalFile, final boolean checkPhysicalUrlAccess, final RouteValueDictionary defaults, final RouteValueDictionary constraints, @Nullable final RouteValueDictionary dataTokens) {
		checkNotNull(routeUrl);

		final Route route = new Route(routeUrl, defaults, constraints, dataTokens, new PageRouteHandler(physicalFile, checkPhysicalUrlAccess));
		add(routeName, route);
		return route;
	}
}
