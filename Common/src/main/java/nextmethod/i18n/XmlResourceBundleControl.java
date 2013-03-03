package nextmethod.i18n;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 */
public class XmlResourceBundleControl extends ResourceBundle.Control {

	private static String XML = "xml";

	public List<String> getFormats(String baseName) {
		return Collections.singletonList(XML);
	}

	public ResourceBundle newBundle(String baseName, Locale locale, String format,
	                                ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException,
		IOException {

		if ((baseName == null) || (locale == null) || (format == null) || (loader == null)) {
			throw new NullPointerException();
		}
		ResourceBundle bundle = null;
		if (!format.equals(XML)) {
			return null;
		}

		String bundleName = toBundleName(baseName, locale);
		String resourceName = toResourceName(bundleName, format);
		URL url = loader.getResource(resourceName);
		if (url == null) {
			return null;
		}
		URLConnection connection = url.openConnection();
		if (connection == null) {
			return null;
		}
		if (reload) {
			connection.setUseCaches(false);
		}
		InputStream stream = connection.getInputStream();
		if (stream == null) {
			return null;
		}
		BufferedInputStream bis = new BufferedInputStream(stream);
		bundle = new XmlResourceBundle(bis);
		bis.close();

		return bundle;
	}
}
