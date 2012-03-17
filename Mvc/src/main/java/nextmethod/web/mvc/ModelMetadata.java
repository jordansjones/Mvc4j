package nextmethod.web.mvc;

import nextmethod.annotations.TODO;
import nextmethod.reflection.ClassInfo;

@TODO
public class ModelMetadata {

	private Object model;

	private Iterable<ModelMetadata> properties;
	private /*final*/ String propertyName;
	private ClassInfo<?> modelType;


	@TODO
	public Object getModel() {
		return model;
	}

	@TODO
	public void setModel(Object model) {
		this.model = model;
	}

	public ClassInfo<?> getModelType() {
		return modelType;
	}

	@TODO
	public Iterable<ModelMetadata> getProperties() {
		return properties;
	}

	public String getPropertyName() {
		return propertyName;
	}
}
