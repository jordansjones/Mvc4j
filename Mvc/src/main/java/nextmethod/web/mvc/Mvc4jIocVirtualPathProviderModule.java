package nextmethod.web.mvc;

import com.google.inject.AbstractModule;
import nextmethod.web.VirtualPathProvider;
import nextmethod.web.routing.RouteTable;

/**
 *
 */
final class Mvc4jIocVirtualPathProviderModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(VirtualPathProvider.class).toProvider(Mvc4jVirtualPathProvider.class);
		// This depends on VirtualPathProvider, so we Inject it here.
		requestStaticInjection(RouteTable.class);
	}
}
