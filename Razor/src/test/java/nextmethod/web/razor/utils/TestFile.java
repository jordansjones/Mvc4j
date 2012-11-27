package nextmethod.web.razor.utils;

import nextmethod.base.NotImplementedException;

import java.io.InputStream;

public class TestFile {

	public final String ResourceNameFormat = "testFiles.%s";

	private final String resourceName;

	public TestFile(final String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceName() {
		return resourceName;
	}

	public static TestFile create(final String localResourceName) {
		throw new NotImplementedException();
	}

	public InputStream openRead() {
		throw new NotImplementedException();
	}

	public byte[] readAllBytes() {
		throw new NotImplementedException();
	}

	public String readAllText() {
		throw new NotImplementedException();
	}

	public void save(final String filePath) {
		throw new NotImplementedException();
	}
}
