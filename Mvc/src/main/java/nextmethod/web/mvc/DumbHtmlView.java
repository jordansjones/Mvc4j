package nextmethod.web.mvc;

import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;

/**
 *
 */
public class DumbHtmlView implements IView {

	private final URL htmlPage;

	public DumbHtmlView(final URL htmlPage) {
		this.htmlPage = htmlPage;
	}

	@Override
	public void render(final ViewContext viewContext, final Writer writer) {
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(htmlPage.openStream());
			CharStreams.copy(inputStreamReader, writer);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			Closeables.closeQuietly(inputStreamReader);
		}
	}
}
