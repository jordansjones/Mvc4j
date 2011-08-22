package nextmethod.web.mvc;

import com.google.common.base.Suppliers;
import nextmethod.reflection.MethodInfo;

/**
 *
 */
final class ActionMethodDispatcherCache extends ReaderWriterCache<MethodInfo, ActionMethodDispatcher> {

	ActionMethodDispatcherCache() {
	}

	public ActionMethodDispatcher getDispatcher(final MethodInfo actionMethod) {
		return fetchOrCreateItem(actionMethod, Suppliers.ofInstance(new ActionMethodDispatcher(actionMethod)));
	}
}
