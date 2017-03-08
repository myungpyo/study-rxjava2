package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by myungpyo.shim on 2017. 3. 2..
 *
 */

public class InfiniteObservableTest extends BasePlayground {

	@Test
	public void testInfiniteObservable() {
		prepareLock(1);

		Observable<Integer> infiniteObservable = Observable.create(subscriber -> {
			System.out.println(attachWithTid("Subscriber has been created."));
			int num = 0;
			while (!subscriber.isDisposed()) {
				subscriber.onNext(num++);
			}

			subscriber.onComplete();
			System.out.println(attachWithTid("Subscriber has been completed."));
		});

		infiniteObservable = infiniteObservable.observeOn(Schedulers.computation());
		infiniteObservable = infiniteObservable.subscribeOn(Schedulers.io());
		infiniteObservable.subscribe(new DisposableObserver<Integer>() {
			@Override
			public void onNext(Integer val) {
				System.out.println(attachWithTid("onNext : " + val));

				if (val > 4) {
					dispose();
				}
			}

			@Override
			public void onError(Throwable e) {
				System.out.println(attachWithTid("onError : " + e.getMessage()));
				releaseLock();
			}

			@Override
			public void onComplete() {
				System.out.println(attachWithTid("onComplete"));
				releaseLock();
			}
		});

		waitForLock();
	}
}
