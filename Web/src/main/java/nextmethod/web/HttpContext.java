package nextmethod.web;

import com.google.inject.servlet.RequestScoped;

import javax.inject.Inject;
import javax.servlet.ServletContext;

@RequestScoped
public final class HttpContext implements IHttpContext {

	private final ServletContext servletContext;
	private final IHttpRequest request;
	private final IHttpResponse response;

	@Inject
	public HttpContext(final ServletContext servletContext, final IHttpRequest request, final IHttpResponse response) {
		this.servletContext = servletContext;
		this.request = request;
		this.response = response;
	}

	ServletContext getServletContext() {
		return servletContext;
	}

	public void dump() {
//		final String path = request.getPath();
//		final String applicationPath = request.getApplicationPath();
//		final String servletPath = request.getHttpServletRequest().getServletPath();
//
//		final String realPath = servletContext.getRealPath(path);
//		final String realApplicationPath = servletContext.getRealPath(applicationPath);
//		final String realServletPath = servletContext.getRealPath(servletPath);
//		final String realSiteCssPath = servletContext.getRealPath("/Content/Site.css");
//		URL resource = null;
//		try {
//			resource = servletContext.getResource("/WEB-INF/web.xml");
//		}
//		catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//		if (resource != null) {
//			try {
//				final String providerUrl = InitialContext.PROVIDER_URL;
//				final InitialDirContext dirContext = new InitialDirContext();
//				final DirContext resCtx = (DirContext) dirContext.lookup("java:comp/Resources");
//				if (resCtx.getClass().getName().equalsIgnoreCase("org.apache.naming.resources.ProxyDirContext")) {
//					try {
//						final Method getDocBase = resCtx.getClass().getMethod("getDocBase", null);
//						int y = 1;
//					}
//					catch (NoSuchMethodException e) {
//						e.printStackTrace();
//					}
//				}
//				final InitialContext initialContext = new InitialContext();
//				final String nameInNamespace = initialContext.getNameInNamespace();
//				final NamingEnumeration<Binding> bindingNamingEnumeration = initialContext.listBindings(nameInNamespace);
//				final Context envCtx = (Context) initialContext.lookup("java:comp/env");
//				final Object lookup = initialContext.lookup(resource.toString());
//				int x = 1;
//			}
//			catch (NamingException e) {
//				e.printStackTrace();
//			}
//		}
//
//
//		boolean doBreak = true;
	}

	@Override
	public IHttpRequest getRequest() {
		return request;
	}

	@Override
	public IHttpResponse getResponse() {
		return response;
	}

	@Override
	public String applyApplicationPathModifier(final String virtualPath) {
		// TODO: Implement this
		// NOTE: Take SessionID into account.
		return virtualPath;
	}

	@Override
	public String getApplicationPath() {
		return request.getApplicationPath();
	}

}
