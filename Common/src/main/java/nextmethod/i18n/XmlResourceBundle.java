package nextmethod.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

/**
 *
 */
public class XmlResourceBundle extends ResourceBundle {

	private final Properties props;

	XmlResourceBundle(InputStream stream) throws IOException {
		props = new Properties();
		props.loadFromXML(stream);
	}

	@Override
	protected Object handleGetObject(final String key) {
		return props.getProperty(key);
	}

	@Override
	public Enumeration<String> getKeys() {
		Set<String> handleKeys = props.stringPropertyNames();
		return Collections.enumeration(handleKeys);
	}
}
