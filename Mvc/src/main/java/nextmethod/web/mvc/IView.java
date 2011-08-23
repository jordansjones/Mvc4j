package nextmethod.web.mvc;

import java.io.Writer;

/**
 *
 */
public interface IView {

	void render(ViewContext viewContext, Writer writer);
}
