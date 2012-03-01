package nextmethod.web.mvc;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

/**
 *
 */
class IgnoredPackagesPredicate implements Predicate<String> {

	private static final String[] ignoredPackages = {
		"java",
		"javax",
		"com.google.common",
		"com.google.inject",
		"net.sf.cglib",
		"org.aopalliance",
		"org.apache.commons",
		"org.hamcrest",
		"org.objectweb.asm",
		"sun",
		"sunw"
	};

	@Override
	public boolean apply(@Nullable final String input) {
		return isIgnoredPackage(input);
	}

	private static final Joiner PackagePartJoiner = Joiner.on('.').skipNulls();

	private static boolean isIgnoredPackage(String path) {
		if (Strings.isNullOrEmpty(path))
			return true;

		String[] parts = path.split("/");
		if (parts == null)
			return true;

		if (parts.length < 1)
			return false;

		final List<String> packageParts = Lists.newArrayList();
		for (String part : parts) {
			if (Strings.isNullOrEmpty(part))
				continue;
			if ("WEB-INF".equalsIgnoreCase(part))
				continue;
			if ("classes".equalsIgnoreCase(part))
				continue;

			packageParts.add(part);
		}

		path = PackagePartJoiner.join(packageParts);

		if (Strings.isNullOrEmpty(path))
			return false;

		final String lcPath = path.toLowerCase();
		for (String ignoredPackage : ignoredPackages) {
			if (lcPath.startsWith(ignoredPackage.toLowerCase())) {
				return true;
			}
		}

		return false;
	}
}
