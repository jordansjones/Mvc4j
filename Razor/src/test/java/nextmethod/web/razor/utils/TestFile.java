package nextmethod.web.razor.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.Resources;
import nextmethod.base.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

public class TestFile {

	public static final String ResourceNameFormat = "/testFiles/%s";

	private final String resourceName;

	public TestFile(final String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceName() {
		return resourceName;
	}

	public static TestFile create(final String localResourceName) {
		return new TestFile(String.format(ResourceNameFormat, localResourceName));
	}

	public InputSupplier<InputStream> openRead() {
		final URL resource = Resources.getResource(getClass(), this.resourceName);
		return Resources.newInputStreamSupplier(resource);
	}

	public byte[] readAllBytes() {
		throw new NotImplementedException();
	}

	public String readAllText() {
		try(InputStreamReader stream = new InputStreamReader(openRead().getInput())) {
			final List<String> strings = CharStreams.readLines(stream);
			return Joiner.on("\r\n").join(strings);
		}
		catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	public void save(final String filePath) {
		throw new NotImplementedException();
	}
}
