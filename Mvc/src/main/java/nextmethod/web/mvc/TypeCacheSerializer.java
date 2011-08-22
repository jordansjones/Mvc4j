package nextmethod.web.mvc;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

// Processes files with the following format:
//
// <typeCache lastModified="..." mvcVersionId="...">
//  <assembly name="...">
//      <module versionId="...">
//          <type>...</type>
//      </module>
//  </assembly>
// </typeCache>
//
// This is used to store caches of files between "AppDomain" resets, leading to improved cold boot time
// and more efficient use of memory.
final class TypeCacheSerializer {

	public List<Class<?>> deserializeTypes(final Reader input) {
		return Lists.newArrayList();
	}

	public void serializeTypes(final Iterable<Class<?>> types, final Writer output) {

	}

	private DateTime currentDate() {
		return DateTime.now();
	}
}
