package com.smp.rx2playground;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import io.reactivex.Observable;

/**
 * Created by myungpyo.shim on 2017. 3. 24..
 *
 */

public class OperatorCombineLatestTest extends BasePlayground {

	@Test
	public void testCombineLatest() {

		prepareLock();

		Observable.combineLatest(
			Observable.interval(1, 1, TimeUnit.SECONDS).map(x -> "A" + x).take(6),
			Observable.interval(1, 2, TimeUnit.SECONDS).map(x -> "B" + x).take(6),
			(a, b) -> a + ":" + b
		).subscribe(System.out::println, e -> releaseLock(), this::releaseLock);

		waitForLock();
	}

	@Test
	public void testCombineLatestFrom() {

		prepareLock();

		Observable<String> fast = Observable.interval(1, 1, TimeUnit.SECONDS).map(x -> "Fast" + x).take(50);
		Observable<String> slow = Observable.interval(1, 4, TimeUnit.SECONDS).map(x -> "Slow" + x).take(6);

		slow.withLatestFrom(
			fast, (s, f) -> s + ":" + f
		).subscribe(System.out::println, e -> releaseLock(), this::releaseLock);

		waitForLock();
	}

}
