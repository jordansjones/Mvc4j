package jordan;

import com.google.inject.Module;
import com.nextmethod.web.HttpServletContextListener;
import com.nextmethod.web.IHttpApplication;

import java.util.List;

public class MvcServletContextListener extends HttpServletContextListener {

	@Override
	protected Class<? extends IHttpApplication> getHttpApplication() {
		return MvcApplication.class;
	}

	@Override
	protected void loadModules(List<Module> modules) {
	}
}
