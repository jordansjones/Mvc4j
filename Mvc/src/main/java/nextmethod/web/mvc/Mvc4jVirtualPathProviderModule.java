package nextmethod.web.mvc;

import com.google.inject.AbstractModule;
import nextmethod.web.VirtualPathProvider;
import nextmethod.web.routing.RouteTable;

import javax.inject.Singleton;

/**
 *
 */
final class Mvc4jVirtualPathProviderModule extends AbstractModule {

	@Override
	protected void configure() {
		// Virtual Path Utility
		bind(VirtualPathUtility.class).in(Singleton.class);
		bind(VirtualPathProvider.class).toProvider(Mvc4jVirtualPathProvider.class);
		// This depends on VirtualPathProvider, so we Inject it here.
		requestStaticInjection(RouteTable.class);
	}
}
