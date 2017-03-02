package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;

/**
 * Created by myungpyo.shim on 2017. 3. 2..
 *
 */

public class PushFunctionalityTest extends BasePlayground {

	@Test
	public void testPushFunctionality() {
		Observable<Integer> numberObservable = Observable.create(subscriber -> {
			for (int num = 1; num <= 10; num++) {
				if (subscriber.isDisposed()) {
					System.out.println("Emission has been disposed.");
					subscriber.onComplete();
					return;
				}
				System.out.println("Emit : " + num);
				subscriber.onNext(num);
			}
			System.out.println("onComplete");
			subscriber.onComplete();
		});

		numberObservable
			.skip(2)
			.take(3)
			.map(value -> value * 2)
			.subscribe(System.out::println);
	}
}
