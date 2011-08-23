package nextmethod.web.mvc;


import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Resources;
import com.google.inject.Injector;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 *
 */
@Singleton
class Mvc4jStaticFileFilter implements Filter {

	@Inject
	private Injector injector;

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest req = injector.getInstance(HttpServletRequest.class);
		final String path = req.getServletPath();
		final URL resource = Resources.getResource(path);
		if (resource != null) {
			OutputStream output = null;
			InputStream input = null;
			try {
				input = resource.openStream();
				output = response.getOutputStream();
				ByteStreams.copy(input, output);
			}
			catch (IOException ignored) {
			}
			finally {
				Closeables.closeQuietly(output);
				Closeables.closeQuietly(input);
			}
			return;
		}
//		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}
}
