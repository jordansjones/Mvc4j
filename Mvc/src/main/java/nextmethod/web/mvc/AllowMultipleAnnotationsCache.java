package nextmethod.web.mvc;

import com.google.common.base.Supplier;
import nextmethod.reflection.AnnotationInfo;
import nextmethod.web.mvc.annotations.Filter;

final class AllowMultipleAnnotationsCache extends ReaderWriterCache<AnnotationInfo<? extends Filter>, Boolean> {

	public boolean isMultipleUseAnnotations(final AnnotationInfo<? extends Filter> annotationType) {
		return fetchOrCreateItem(annotationType, annotationUsageAllowsMultiple(annotationType));
	}
	
	private static Supplier<Boolean> annotationUsageAllowsMultiple(final AnnotationInfo<? extends Filter> type) {
		return new Supplier<Boolean>() {
			@Override
			public Boolean get() {
				return type.wrappedType().allowMultiple();
			}
		};
	}
}
