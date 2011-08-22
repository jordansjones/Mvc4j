package nextmethod.web.mvc;

import com.google.inject.servlet.ServletModule;
import nextmethod.web.HttpContext;
import nextmethod.web.HttpRequest;
import nextmethod.web.HttpResponse;
import nextmethod.web.IHttpApplication;
import nextmethod.web.IHttpContext;
import nextmethod.web.IHttpRequest;
import nextmethod.web.IHttpResponse;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

final class Mvc4jIoCServletModule extends ServletModule {

	private final Class<? extends IHttpApplication> httpApplication;

	Mvc4jIoCServletModule(Class<? extends IHttpApplication> httpApplication) {
		this.httpApplication = httpApplication;
	}

	@Override
	protected void configureServlets() {
		filter("/*").through(Mvc4jFilter.class);
		serve("/*").with(Mvc4jServlet.class);

		bind(IHttpApplication.class).to(httpApplication);

		bindConstructor(IHttpRequest.class, HttpRequest.class,
			HttpServletRequest.class, ServletContext.class
		);
		bindConstructor(IHttpResponse.class, HttpResponse.class,
			HttpServletResponse.class, ServletContext.class
		);
		bindConstructor(IHttpContext.class, HttpContext.class,
			ServletContext.class, IHttpRequest.class, IHttpResponse.class
		);
	}

	private <I, T extends I> void bindConstructor(final Class<I> iface, final Class<T> cls, final Class<?>... classes) {
		try {
			bind(iface).toConstructor(cls.getConstructor(classes));
		}
		catch (NoSuchMethodException e) {
			addError(e);
		}
	}
}
