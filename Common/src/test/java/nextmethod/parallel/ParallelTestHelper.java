package nextmethod.parallel;

import com.google.common.util.concurrent.Uninterruptibles;
import nextmethod.base.Delegates;

public final class ParallelTestHelper {

	private ParallelTestHelper() {}

	public static final int NumRun = 500;

	public static void repeat(final Delegates.IAction action) {
		repeat(action, NumRun);
	}

	public static void repeat(final Delegates.IAction action, final int numberTimes) {
		for (int i = 0; i < numberTimes; i++) {
			action.invoke();
		}
	}

	public static <T> void parallelStressTest(final T obj, final Delegates.IAction1<T> action) {
		parallelStressTest(obj, action, Runtime.getRuntime().availableProcessors() + 2);
	}

	public static <T> void parallelStressTest(final T obj, final Delegates.IAction1<T> action, int numberOfThreads) {
		final Thread[] threads = new Thread[numberOfThreads];
		for (int i = 0; i < numberOfThreads; i++) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					action.invoke(obj);
				}
			});
			threads[i].start();
		}

		for (int i = 0; i < numberOfThreads; i++) {
			threads[i].interrupt();
			Uninterruptibles.joinUninterruptibly(threads[i]);
		}
	}

}
