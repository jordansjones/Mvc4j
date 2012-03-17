package nextmethod.web.mvc;

import java.io.Serializable;

public class ModelState implements Serializable {

	private static final long serialVersionUID = -183206270727702658L;

	private ModelErrorCollection errors = new ModelErrorCollection();

	private ValueProviderResult value;

	public ValueProviderResult getValue() {
		return value;
	}

	public void setValue(ValueProviderResult value) {
		this.value = value;
	}

	public ModelErrorCollection getErrors() {
		return errors;
	}

}
