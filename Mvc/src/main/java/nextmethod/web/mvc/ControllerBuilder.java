package nextmethod.web.mvc;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Set;

/**
 *
 */
class ControllerBuilder {

	@Inject
	private IControllerFactory controllerFactory;

	private final Set<String> packages;

	@Inject
	ControllerBuilder(@Named(MagicStrings.DefaultPackagesParamKey) final String defaultPackages) {
		this.packages = Sets.newHashSet();

		if (!Strings.isNullOrEmpty(defaultPackages)) {
			final String[] packages = defaultPackages.split(MagicStrings.DefaultPackagesSeparator);
			for (String s : packages) {
				if (!Strings.isNullOrEmpty(s)) {
					this.packages.add(s);
				}
			}
		}
	}

	public IControllerFactory getControllerFactory() {
		return controllerFactory;
	}

	public Set<String> getDefaultPackages() {
		return this.packages;
	}
}
