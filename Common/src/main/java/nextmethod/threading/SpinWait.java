package nextmethod.threading;

import com.google.common.base.Stopwatch;
import nextmethod.base.Delegates;

import java.util.concurrent.TimeUnit;

public final class SpinWait {

	// The number of steps until SpinOnce yields on multicore machines
	private static final int step = 10;
	private static final int maxTime = 200;
	private static final boolean isSingleCpu = (Runtime.getRuntime().availableProcessors() == 1);

	private int ntime;

	public void spinOnce() {
		ntime += 1;
		if (nextSpinWillYield()) {
			// Spinning does no good on single cpus
			Thread.yield();
		}
		else {
			// Multi-CPU system
			spin(Math.min(ntime, maxTime) << 1);
		}
	}

	public void reset() {
		ntime = 0;
	}

	public boolean nextSpinWillYield() {
		return isSingleCpu ? true : ntime % step == 0;
	}

	public int count() {
		return ntime;
	}
	
	private void spin(int iterations) {
		if (iterations < 0) {
			return;
		}
		while (iterations-- > 0) {
			// Nop
			assert Boolean.TRUE;
		}
	}

	public static void spinUntil(final Delegates.IFunc<Boolean> condition) {
		final SpinWait sw = new SpinWait();
		while (!condition.invoke()) {
			sw.spinOnce();
		}
	}
	
	public static boolean spinUntil(final Delegates.IFunc<Boolean> condition, final long amount, final TimeUnit timeUnit) {
		final SpinWait spinWait = new SpinWait();
		final Stopwatch stopwatch = Stopwatch.createUnstarted().start();

		while(!condition.invoke()) {
			if (stopwatch.elapsed(timeUnit) > amount) {
				return false;
			}
			spinWait.spinOnce();
		}
		return true;
	}
}
