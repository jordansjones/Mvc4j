package com.nextmethod.routing;

import com.nextmethod.web.HttpContext;
import com.nextmethod.web.HttpRequest;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

/**
 * User: Jordan
 * Date: 8/5/11
 * Time: 11:22 PM
 */
public class RequestContextTest extends BaseTest {

	@Mock
	private ServletContext servletContext;
	@Mock
	private HttpServletRequest servletRequest;
	@Mock
	private HttpServletResponse servletResponse;

	@Test
	public void testDefaultConstructor() {
		final RequestContext context = new RequestContext();

		assertNull(context.getHttpContext());
		assertNull(context.getRouteData());
	}

	@SuppressWarnings({"ConstantConditions"})
	@Test(expected = Exception.class)
	public void testConstructorWithNullContextThrowsNpe() {
		new RequestContext(null, new RouteData(null, null));
	}

	@SuppressWarnings({"ConstantConditions"})
	@Test(expected = Exception.class)
	public void testConstructorWithNullRouteDataThrowsNpe() {
		new RequestContext(mockHttpContext(), null);
	}

	@Test
	public void testHttpContext() {
		HttpContext mockContext = mockHttpContext();
		final RouteData mockRouteData = mock(RouteData.class);

		final RequestContext requestContext = new RequestContext(mockContext, mockRouteData);

		assertEquals(mockContext, requestContext.getHttpContext());

		mockContext = mockHttpContext();
		requestContext.setHttpContext(mockContext);
		assertEquals(mockContext, requestContext.getHttpContext());

		requestContext.setHttpContext(null);
		assertNull(requestContext.getHttpContext());
	}

	@Test
	public void testRouteData() {
		final HttpContext httpContext = mockHttpContext();
		RouteData mock = mock(RouteData.class);
		final RequestContext requestContext = new RequestContext(httpContext, mock);

		assertEquals(mock, requestContext.getRouteData());

		mock = mock(RouteData.class);
		requestContext.setRouteData(mock);
		assertEquals(mock, requestContext.getRouteData());

		requestContext.setRouteData(null);
		assertNull(requestContext.getRouteData());
	}

	private HttpContext mockHttpContext() {
		final HttpRequest httpRequest = new HttpRequest(servletRequest, servletContext);
		return new HttpContext(servletContext, httpRequest, servletResponse);
	}
}
