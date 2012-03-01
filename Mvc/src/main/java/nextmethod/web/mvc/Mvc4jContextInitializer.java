package nextmethod.web.mvc;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

/**
 *
 */
public class Mvc4jContextInitializer implements ServletContainerInitializer {

	@Override
	public void onStartup(final Set<Class<?>> c, final ServletContext ctx) throws ServletException {
		ServletContextAnalyzer.analyze(ctx);
		int x = 1;
		ctx.addListener(nextmethod.web.mvc.Mvc4jHttpRuntime.class);
	}
}
