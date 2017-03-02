package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by myungpyo.shim on 2017. 3. 2..
 *
 */

public class ThreadingTest extends BasePlayground {

	@Test
	public void testScheduler() {

		Observable<Integer> observable = Observable.create(subscriber -> {
			subscriber.onNext(1);
			subscriber.onNext(2);
			subscriber.onNext(3);
			subscriber.onNext(4);
			subscriber.onComplete();
		});

		observable
			.doOnNext(i -> {
				System.out.println(attachWithTid("doOnNext : " + i));
			})
			.filter(i -> i % 2 == 0)
			.map(i -> i * 2)
			.subscribeOn(Schedulers.computation())
			.observeOn(Schedulers.io())
			.subscribe(nextValue -> System.out.println(attachWithTid("subscribed : " + nextValue)),
				error -> {
					attachWithTid("error occurred : " + error.getMessage());
					stopWaitingForObservable();
				},
				() -> {
					attachWithTid("complete");
					stopWaitingForObservable();
				});

		waitForObservable();

	}
}
