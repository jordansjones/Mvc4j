package nextmethod.reflection;


import com.google.common.base.Predicate;

import javax.annotation.Nullable;

/**
 *
 */
final class Predicates {

	private Predicates() {
	}

	public static final Predicate<MethodInfo> PublicMethods = new Predicate<MethodInfo>() {
		@Override
		public boolean apply(@Nullable final MethodInfo input) {
			return false;
		}
	};

}
