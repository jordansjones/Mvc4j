package nextmethod.web.razor.utils;

import nextmethod.base.IAction;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

public class DisposableActionTest {

	@Test(expected = NullPointerException.class)
	public void constructorRequiresNonNullAction() {
		new DisposableAction(null);
	}

	@Test
	public void actionIsExecutedOnDispose() {
		// Arrange
		final AtomicBoolean called = new AtomicBoolean(false);
		DisposableAction action = new DisposableAction(new IAction<Object>() {
			@Override
			public Object invoke() {
				called.set(true);
				return null;
			}
		});

		// Act
		action.close();

		// Assert
		assertTrue("The action was not run when the DisposableAction was closed", called.get());
	}
}
