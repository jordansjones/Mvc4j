package nextmethod.web.routing;

import com.google.common.collect.ImmutableList;
import nextmethod.web.IHttpContext;
import nextmethod.web.IHttpRequest;
import nextmethod.web.InvalidOperationException;
import org.junit.Test;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: jordanjones
 * Date: 8/8/11
 * Time: 10:32 AM
 */
public class RouteTest extends BaseTest {

	@Test
	public void testConstructorNullArgs() {
		final Route route = new Route(null, null);
		assertEquals("", route.getUrl());
		assertNull(route.getRouteHandler());
	}

	@Test
	public void testSetNullUrl() {
		final Route route = new Route(null, null);
		route.setUrl("urn:foo");
		route.setUrl(null);

		assertEquals("", route.getUrl());
	}

	@Test
	public void testInvalidUrls() {
		final List<TestUrl> __invalidUrls = generateInvalidUrls();
		for (TestUrl url : __invalidUrls) {
			try {
				new Route(url.getUrl(), null);
				fail(url.getLabel());
			}
			catch (Exception e) {
				if (!url.getExpectedException().isAssignableFrom(e.getClass())) {
					fail(String.format("[%s] Unexpected Exception - %s expected %s", url.getLabel(), e.getClass().getCanonicalName(), url.getExpectedException().getCanonicalName()));
				}
			}
		}
	}

	@Test
	public void testValidUrls() {
		Route r;
		for (TestUrl url : generateValidUrls()) {
			r = new Route(url.getUrl(), null);
			assertEquals(url.getLabel(), url.getExpected(), r.getUrl());
			assertNull(url.getLabel() + "-2", r.getDataTokens());
			assertNull(url.getLabel() + "-3", r.getDefaults());
			assertNull(url.getLabel() + "-4", r.getConstraints());
		}
	}

	@Test
	public void testRoutingHandler() {
		final Route route = new Route(null, new StopRoutingHandler());
		assertEquals(StopRoutingHandler.class, route.getRouteHandler().getClass());
	}

	@Test
	public void testGetRouteDataNoTemplate() {
		final IHttpContext httpContext = mockRequest("~/foo/bar", "");
		final Route r = new Route("foo/bar", null);
		final RouteData routeData = r.getRouteData(httpContext);

		assertNotNull("#1", routeData);
		assertEquals("#2", r, routeData.getRoute());
		assertEquals("#3", 0, routeData.getDataTokens().size());
		assertEquals("#4", 0, routeData.getValues().size());
	}

	@Test(expected = InvalidOperationException.class)
	public void testInvalidConstraint() {
		final Route route = new Route("{foo}/{bar}", new StopRoutingHandler());
		final RouteValueDictionary constraints = new RouteValueDictionary();
		constraints.put("foo", UUID.randomUUID());
		route.setConstraints(constraints);

		final IHttpContext mockContext = mockRequest("~/x/y", "");

		route.getRouteData(mockContext);
	}

	@Test
	public void testGetRouteData() {
		final Route r = new Route("{foo}/{bar}", null);
		final IHttpContext hc = mockRequest("~/x/y", "");
		final RouteData rd = r.getRouteData(hc);

		assertNotNull("#1", rd);

		final RouteValueDictionary values = rd.getValues();
		assertEquals("#2", r, rd.getRoute());
		assertEquals("#3", 0, rd.getDataTokens().size());
		assertEquals("#4", 2, values.size());
		assertEquals("#4-1", "x", values.get("foo"));
		assertEquals("#4-2", "y", values.get("bar"));
	}

	@Test
	public void testGetRouteData2() {
		// {} matches and substitutes even at partial state..
		final Route r = new Route("{foo}/bar{baz}", null);
		final IHttpContext hc = mockRequest("~/x/bart", "");
		final RouteData rd = r.getRouteData(hc);

		assertNotNull("#1", rd);

		final RouteValueDictionary values = rd.getValues();
		assertEquals("#2", r, rd.getRoute());
		assertEquals("#3", 0, rd.getDataTokens().size());
		assertEquals("#4", 2, values.size());
		assertEquals("#4-1", "x", values.get("foo"));
		assertEquals("#4-2", "t", values.get("baz"));
	}

	@Test
	public void testGetRouteData3() {
		final Route r = new Route("{foo}/{bar}", null);
		final IHttpContext hc = mockRequest("~/x/y/z", "");
		final RouteData rd = r.getRouteData(hc);
		assertNull(rd); // mismatch
	}

	@Test
	public void testGetRouteData4() {
		final Route r = new Route("{foo}/{bar}", null);
		final IHttpContext hc = mockRequest("~/x", "");
		final RouteData rd = r.getRouteData(hc);
		assertNull(rd); // mismatch
	}

	@Test
	public void testGetRouteData5() {
		final Route r = new Route("{foo}/{bar}", new StopRoutingHandler());

		RouteData rd = r.getRouteData(mockRequest("x/y", ""));
		assertNull("#1", rd);

		rd = r.getRouteData(mockRequest("~/x/y", ""));
		assertNotNull("#2", rd);

		rd = r.getRouteData(mockRequest("~/x/y/z", ""));
		assertNull("#3", rd);

		rd = r.getRouteData(mockRequest("~x/y", ""));
		assertNull("#4", rd);

		rd = r.getRouteData(mockRequest("/x/y", ""));
		assertNull("#5", rd);

		rd = r.getRouteData(mockRequest("{foo}/{bar}/baz", ""));
		assertNull("#6", rd);

		rd = r.getRouteData(mockRequest("{foo}/{bar}", ""));
		assertNotNull("#7", rd);
		assertEquals("#7-1", 0, rd.getDataTokens().size());
		assertEquals("#7-2", 2, rd.getValues().size());
	}

	@Test
	public void testGetRouteData6() {
		final Route r = new Route("{table}/{action}.aspx", null);
		final RouteData rd = r.getRouteData(mockRequest("~/FooTable/List.aspx", ""));
		assertNotNull("#1", rd);
		assertEquals("#2", "FooTable", rd.getValues().get("table"));
		assertEquals("#3", "List", rd.getValues().get("action"));
	}

	@Test
	public void testGetRouteData7() {
		final Route r = new Route("{table}/{action}.aspx", null);
		final RouteData rd = r.getRouteData(mockRequest("~/FooTable/", ""));
		assertNull(rd);
	}

	@Test
	public void testGetRouteData8() {
		final Route r = new Route("{first}/{*rest}", null);
		final IHttpContext hc = mockRequest("~/a/b/c/d", "");
		final RouteData rd = r.getRouteData(hc);
		assertNotNull("#1", rd);
		assertEquals("#2", r, rd.getRoute());
		assertEquals("#3", 0, rd.getDataTokens().size());
		final RouteValueDictionary values = rd.getValues();
		assertEquals("#4", 2, values.size());
		assertEquals("#5", "a", values.get("first"));
		assertEquals("#6", "b/c/d", values.get("rest"));
	}


	private static IHttpContext mockRequest(final String requestedUrl, final String contextPath) {
		final IHttpRequest mockRequest = when(mock(IHttpRequest.class).getAppRelativeCurrentExecutionFilePath()).thenReturn(requestedUrl).getMock();
		final ServletContext context = when(mock(ServletContext.class).getContextPath()).thenReturn(contextPath).getMock();
		return null;//new HttpContext(context, mockRequest, mock(HttpServletResponse.class));
	}

	private static ImmutableList<TestUrl> generateValidUrls() {
		return ImmutableList.<TestUrl>builder()
			.add(newTestUrl(1, "{foo}/{bar}", "{foo}/{bar}"))
			.add(newTestUrl(2, "a~c", "a~c"))
			.add(newTestUrl(3, "foo/", "foo/"))
			.add(newTestUrl(4, "summary/{action}-{type}/{page}", "summary/{action}-{type}/{page}"))
			.add(newTestUrl(5, "{first}/{*rest}", "{first}/{*rest}"))
			.add(newTestUrl(6, "{language}-{country}/{controller}/{action}", "{language}-{country}/{controller}/{action}"))
			.add(newTestUrl(7, "{controller}.{action}.{id}", "{controller}.{action}.{id}"))
			.add(newTestUrl(8, "{reporttype}/{year}/{month}/{date}", "{reporttype}/{year}/{month}/{date}"))
			.add(newTestUrl(9, "Book{Title}and{foo}", "Book{Title}and{foo}"))
			.add(newTestUrl(10, "foo/{ }", "foo/{ }"))
			.add(newTestUrl(11, "foo/{ \t}", "foo/{ \t}"))
			.add(newTestUrl(12, "foo/{ \n}", "foo/{ \n}"))
			.add(newTestUrl(13, "foo/{ \t\n}", "foo/{ \t\n}"))
			.add(newTestUrl(14, "-{foo}", "-{foo}"))
			.build();
	}

	private static final Class<IllegalArgumentException> ILLEGAL_ARGUMENT_EXCEPTION_CLASS = IllegalArgumentException.class;

	private static ImmutableList<TestUrl> generateInvalidUrls() {
		return ImmutableList.<TestUrl>builder()
			.add(newTestUrl(1, "~", ILLEGAL_ARGUMENT_EXCEPTION_CLASS))
			.add(newTestUrl(2, "/", ILLEGAL_ARGUMENT_EXCEPTION_CLASS))
			.add(newTestUrl(3, "foo?bar", ILLEGAL_ARGUMENT_EXCEPTION_CLASS))
			.add(newTestUrl(4, "foo/{bar", ILLEGAL_ARGUMENT_EXCEPTION_CLASS))
			.add(newTestUrl(5, "foo/bar}", ILLEGAL_ARGUMENT_EXCEPTION_CLASS))
			.add(newTestUrl(6, "foo/{}", ILLEGAL_ARGUMENT_EXCEPTION_CLASS))
			.add(newTestUrl(7, "foo/{x/y/z}", ILLEGAL_ARGUMENT_EXCEPTION_CLASS))
			.add(newTestUrl(8, "foo/{a{{b}}c}", ILLEGAL_ARGUMENT_EXCEPTION_CLASS))
			.add(newTestUrl(9, "foo/{a}{b}", ILLEGAL_ARGUMENT_EXCEPTION_CLASS))
			.add(newTestUrl(10, "foo//bar", ILLEGAL_ARGUMENT_EXCEPTION_CLASS))
			.add(newTestUrl(11, "{first}/{*rest}/{foo}", ILLEGAL_ARGUMENT_EXCEPTION_CLASS))
			.add(newTestUrl(12, "{first}/{*rest}-{foo}", ILLEGAL_ARGUMENT_EXCEPTION_CLASS))
			.add(newTestUrl(13, "{first}/{foo}-{*rest}", ILLEGAL_ARGUMENT_EXCEPTION_CLASS))
			.add(newTestUrl(14, "-{*rest}", ILLEGAL_ARGUMENT_EXCEPTION_CLASS))
			.build();
	}

	private static TestUrl newTestUrl(final Integer idx, final String url, final Class<? extends Exception> expectedException) {
		return new TestUrl(url, newUrlLabel(idx), expectedException);
	}

	private static TestUrl newTestUrl(final Integer idx, final String url, final String expected) {
		return new TestUrl(url, expected, newUrlLabel(idx));
	}

	private static String newUrlLabel(final Integer idx) {
		return String.format("#%d", idx);
	}

}
