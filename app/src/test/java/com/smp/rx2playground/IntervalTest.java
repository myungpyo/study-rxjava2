package com.smp.rx2playground;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by myungpyo.shim on 2017. 3. 2..
 *
 */

public class IntervalTest extends BasePlayground {

	@Test
	public void testInterval() {
		prepareLock();

		Observable.interval(2, 1, TimeUnit.SECONDS)
			.subscribe(new DisposableObserver<Long>() {
				@Override
				public void onNext(Long val) {
					System.out.println(val);
					if (val > 4) {
						dispose();
						releaseLock();
					}
				}

				@Override
				public void onError(Throwable e) {
					releaseLock();
				}

				@Override
				public void onComplete() {
					releaseLock();
				}
			});

		waitForLock();
	}
}
