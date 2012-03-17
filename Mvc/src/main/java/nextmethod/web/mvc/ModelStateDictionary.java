package nextmethod.web.mvc;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import nextmethod.OutParam;
import nextmethod.annotations.TODO;
import nextmethod.base.IEqualityComparer;
import nextmethod.base.StringComparer;
import nextmethod.collect.Dictionary;
import nextmethod.collect.IDictionary;
import nextmethod.collect.KeyValuePair;

import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@TODO
public class ModelStateDictionary implements IDictionary<String, ModelState> {
	
	private final IDictionary<String, ModelState> delegate = new Dictionary<>(StringComparer.getOrdinalIgnoreCase());

	public ModelStateDictionary() {
	}

	public ModelStateDictionary(final ModelStateDictionary dictionary) {
		checkNotNull(dictionary, "dictionary");

		this.add(dictionary);
	}

	private ModelState getModelStateForKey(final String key) {
		checkNotNull(key, "key");

		final OutParam<ModelState> modelState = OutParam.of();
		if (!tryGetValue(key, modelState)) {
			modelState.set(new ModelState());
			this.add(key, modelState.get());
		}
		return modelState.get();
	}
	
	@TODO
	public void addModelError(final String key, final Exception e) {
//		getModelStateForKey(key).getErrors().add(e);
	}

	@TODO
	public void addModelError(final String key, final String errorMessage) {
//		getModelStateForKey(key).getErrors().add(errorMessage);
	}

	@TODO
	public boolean isValidField(final String key) {
		checkNotNull(key, "key");
		return false;
	}

	public void merge(final ModelStateDictionary dictionary) {
		if (dictionary == null) return;

		for (KeyValuePair<String, ModelState> pair : dictionary) {
			if (delegate.containsKey(pair.getKey()))
				delegate.set(pair.getKey(), pair.getValue());
			else
				delegate.add(pair);
		}
	}

	public void setModelValue(final String key, final ValueProviderResult value) {
		getModelStateForKey(key).setValue(value);
	}

	@Override
	public ModelStateDictionary add(final KeyValuePair<String, ModelState> item) {
		delegate.add(item);
		return this;
	}

	@Override
	public ModelStateDictionary add(final IDictionary<? extends String, ? extends ModelState> items) {
		delegate.add(items);
		return this;
	}

	@Override
	public ModelStateDictionary add(final Map<String, ModelState> items) {
		delegate.add(items);
		return this;
	}

	@Override
	public ModelStateDictionary add(final String s, final ModelState modelState) {
		delegate.add(s, modelState);
		return this;
	}

	@Override
	public ModelStateDictionary clear() {
		delegate.clear();
		return this;
	}

	@Override
	public boolean contains(final KeyValuePair<String, ModelState> item) {
		return delegate.contains(item);
	}

	@Override
	public boolean containsKey(final String s) {
		return delegate.containsKey(s);
	}

	@Override
	public boolean containsValue(final ModelState modelState) {
		return delegate.containsValue(modelState);
	}

	@Override
	public ModelStateDictionary copyTo(final KeyValuePair<String, ModelState>[] array, int arrayIndex) {
		delegate.copyTo(array, arrayIndex);
		return this;
	}

	@Override
	public ModelStateDictionary filterEntries(final Predicate<KeyValuePair<String, ModelState>> filter) {
		final ModelStateDictionary dictionary = new ModelStateDictionary();
		dictionary.delegate.add(delegate.filterEntries(filter));
		return dictionary;
	}

	@Override
	public ModelState get(final String s) {
		return delegate.get(s);
	}

	@Override
	public IEqualityComparer<String> getComparer() {
		return delegate.getComparer();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public boolean isReadOnly() {
		return delegate.isReadOnly();
	}

	@Override
	public ModelStateDictionary put(final String s, final ModelState modelState) {
		delegate.put(s, modelState);
		return this;
	}

	@Override
	public boolean remove(final KeyValuePair<String, ModelState> item) {
		return delegate.remove(item);
	}

	@Override
	public boolean remove(final String s) {
		return delegate.remove(s);
	}

	@Override
	public ModelStateDictionary set(final String s, final ModelState modelState) {
		delegate.set(s, modelState);
		return this;
	}

	@Override
	public int size() {
		return delegate.size();
	}

	@Override
	public Map<String, ModelState> toMap() {
		return delegate.toMap();
	}

	@Override
	public Optional<ModelState> tryGetValue(final String s) {
		return delegate.tryGetValue(s);
	}

	@Override
	public boolean tryGetValue(final String s, final OutParam<ModelState> value) {
		return delegate.tryGetValue(s, value);
	}

	@Override
	public Iterator<KeyValuePair<String, ModelState>> iterator() {
		return delegate.iterator();
	}
}
