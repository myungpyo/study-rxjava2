package com.smp.rx2playground;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by myungpyo.shim on 2017. 3. 23..
 *
 */

public class OperatorDelayTest extends BasePlayground {

	@Test
	public void testTimer() {
		prepareLock();

		Observable
			.timer(2, TimeUnit.SECONDS)
			.flatMap(i -> Observable.just("a", "bc", "def"))
			.subscribe(new DisposableObserver<String>() {
				@Override
				public void onNext(String value) {
					System.out.println(attachWithTid(value));
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

	@Test
	public void testDelay() {
		prepareLock();

		Observable
			.just("a", "bc", "def")
			.delay(word -> Observable.timer(word.length(), TimeUnit.SECONDS))
			//			.flatMap(word -> Observable.timer(word.length(), TimeUnit.SECONDS).map(x -> word))
			.subscribe(new DisposableObserver<String>() {
				@Override
				public void onNext(String value) {
					System.out.println(attachWithTid(value));
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
