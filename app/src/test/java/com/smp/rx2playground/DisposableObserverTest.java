package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by myungpyo.shim on 2017. 3. 2..
 *
 */

public class DisposableObserverTest extends BasePlayground {

	@Test
	public void testPushFunctionality() {
		Observable<Integer> numberObservable = Observable.create(subscriber -> {
			for (int num = 1; num <= 10; num++) {
				if (subscriber.isDisposed()) {
					System.out.println("Emission has been disposed.");
					subscriber.onComplete();
					return;
				}
				subscriber.onNext(num);
			}
			subscriber.onComplete();
		});

		DisposableObserver<Integer> observer = new DisposableObserver<Integer>() {
			@Override
			public void onNext(Integer integer) {
				if (integer > 5) {
					dispose();
					return;
				}
				System.out.println("Emitted : " + integer);
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("onError : " + t.getMessage());
			}

			@Override
			public void onComplete() {
				System.out.println("onComplete");
			}
		};

		numberObservable
			.subscribe(observer);
	}
}
