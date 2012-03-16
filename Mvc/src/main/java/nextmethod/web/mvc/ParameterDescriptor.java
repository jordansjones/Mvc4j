package nextmethod.web.mvc;

import nextmethod.reflection.ParameterInfo;

public abstract class ParameterDescriptor {

	private static final EmptyParameterBindingInfo emptyBindingInfo = new EmptyParameterBindingInfo();

	public abstract ActionDescriptor getActionDescriptor();

	public ParameterBindingInfo getBindingInfo() {
		return emptyBindingInfo;
	}

	public Object getDefaultValue() {
		return null;
	}

	public abstract String getParameterName();
	public abstract ParameterInfo<?> getParameterType ();


	private static final class EmptyParameterBindingInfo extends ParameterBindingInfo {}
}
