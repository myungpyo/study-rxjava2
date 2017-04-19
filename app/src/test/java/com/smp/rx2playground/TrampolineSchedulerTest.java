package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by myungpyo.shim on 2017. 3. 27..
 *
 */

public class TrampolineSchedulerTest extends BasePlayground {

	@Test
	public void testTrampolineScheduler() {

		prepareLock();

		Observable.range(1, 10)
			.flatMap(value -> Observable.create((ObservableOnSubscribe<Integer>)observer -> {
				try {
					Thread.sleep((10 - value) * 100);
					observer.onNext(value);
					observer.onComplete();
				} catch (Throwable throwable) {
					observer.onError(throwable);
				}
			}).doOnNext(val -> System.out.println(attachWithTid("doOnNext : " + val)))
				.subscribeOn(Schedulers.trampoline()))
			.observeOn(Schedulers.computation())
			.subscribe(val -> {
				},
				(throwable -> releaseLock()),
				this::releaseLock);

		waitForLock();

	}
}
