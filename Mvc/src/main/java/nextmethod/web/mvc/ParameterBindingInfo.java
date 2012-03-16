package nextmethod.web.mvc;

import com.google.common.collect.Lists;

import java.util.Collection;

public abstract class ParameterBindingInfo {

	public IModelBinder getBinder() {
		return null;
	}

	public Collection<String> getExclude() {
		return Lists.newArrayList();
	}

	public Collection<String> getInclude() {
		return Lists.newArrayList();
	}

	public String getPrefix() {
		return null;
	}

}
