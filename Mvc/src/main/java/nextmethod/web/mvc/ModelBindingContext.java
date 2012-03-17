package nextmethod.web.mvc;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import nextmethod.annotations.TODO;
import nextmethod.base.StringComparer;
import nextmethod.collect.Dictionary;
import nextmethod.reflection.ClassInfo;

import javax.annotation.Nullable;

@TODO
public class ModelBindingContext {

	private static final Predicate<String> defaultPropertyFilter = Predicates.alwaysTrue();

	private String modelName;
	private ModelStateDictionary modelState;
	private Predicate<String> propertyFilter;
	private Dictionary<String, ModelMetadata> propertyMetadata;
	private IValueProvider valueProvider;
	private boolean fallBackToEmptyPrefix;
	private ModelMetadata modelMetadata;

	public ModelBindingContext() {
		this(null);
	}

	@TODO
	public ModelBindingContext(final ModelBindingContext bindingContext) {
		if (bindingContext != null) {
			modelState = bindingContext.modelState;
			valueProvider = bindingContext.valueProvider;
		}

		if (modelState == null)
			modelState = new ModelStateDictionary();
	}

	public boolean isFallBackToEmptyPrefix() {
		return fallBackToEmptyPrefix;
	}

	public void setFallBackToEmptyPrefix(boolean fallBackToEmptyPrefix) {
		this.fallBackToEmptyPrefix = fallBackToEmptyPrefix;
	}

	public Object getModel() {
		return getModelMetadata().getModel();
	}

	public ModelMetadata getModelMetadata() {
		return modelMetadata;
	}

	public void setModelMetadata(ModelMetadata modelMetadata) {
		this.modelMetadata = modelMetadata;
	}

	public String getModelName() {
		return Strings.nullToEmpty(modelName);
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public ModelStateDictionary getModelState() {
		return modelState;
	}

	public void setModelState(ModelStateDictionary modelState) {
		this.modelState = modelState;
	}
	
	public ClassInfo<?> getModelType() {
		return this.getModelMetadata().getModelType();
	}

	public Predicate<String> getPropertyFilter() {
		return propertyFilter == null ? defaultPropertyFilter : propertyFilter;
	}

	public void setPropertyFilter(Predicate<String> propertyFilter) {
		this.propertyFilter = propertyFilter;
	}

	public Dictionary<String, ModelMetadata> getPropertyMetadata() {
		if (propertyMetadata == null) {
			propertyMetadata = Dictionary.asDictionary(getModelMetadata().getProperties(), new Function<ModelMetadata, String>() {
				@Override
				public String apply(@Nullable ModelMetadata input) {
					return input != null ? input.getPropertyName() : null;
				}
			}, StringComparer.getOrdinalIgnoreCase());
		}
		return propertyMetadata;
	}

	public void setPropertyMetadata(Dictionary<String, ModelMetadata> propertyMetadata) {
		this.propertyMetadata = propertyMetadata;
	}

	public IValueProvider getValueProvider() {
		return valueProvider;
	}

	public void setValueProvider(IValueProvider valueProvider) {
		this.valueProvider = valueProvider;
	}
}
