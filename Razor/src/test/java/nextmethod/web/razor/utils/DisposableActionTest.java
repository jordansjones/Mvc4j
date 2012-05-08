package nextmethod.web.razor.utils;

import nextmethod.base.IAction;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DisposableActionTest {

	@Test(expected = NullPointerException.class)
	public void constructorRequiresNonNullAction() {
		new DisposableAction(null);
	}

	@Test
	public void actionIsExecutedOnExplicitDispose() {
		final AtomicBoolean called = new AtomicBoolean(false);
		DisposableAction action = new DisposableAction(createAction(called));

		assertFalse(called.get());

		action.close();

		assertTrue("The action was not run when the DisposableAction was closed", called.get());
	}

	@Test
	public void actionIsExectuedOnImplicitDispose() {
		final AtomicBoolean called = new AtomicBoolean(false);

		try (DisposableAction action = new DisposableAction(createAction(called))) {
			assertFalse(called.get());
		}

		assertTrue("The action was not run when the DisposableAction was closed", called.get());
	}

	private static IAction<Boolean> createAction(final AtomicBoolean called) {
		return new IAction<Boolean>() {
			@Override
			public Boolean invoke() {
				return called.getAndSet(true);
			}
		};
	}
}