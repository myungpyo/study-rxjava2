package com.smp.rx2playground;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;

/**
 * Created by myungpyo.shim on 2017. 3. 20..
 *
 */

public class BufferTest extends BasePlayground {

	@Test
	public void testBuffer() {

		prepareLock();
		prepareElapsedTime();

		Observable.range(0, 20)
			.concatMap(
				(Function<Integer, ObservableSource<?>>)integer -> Observable.just(integer)
					.delay(400, TimeUnit.MILLISECONDS))
			.buffer(1L, TimeUnit.SECONDS)
			.subscribe(
				list -> System.out.println(attachWithElapsedTime(list.toString())),
				throwable -> releaseLock(),
				this::releaseLock);

		waitForLock();
	}

	@Test
	public void testBufferWithClosingIndicator() {

		prepareLock();
		prepareElapsedTime();

		Observable.range(0, 20)
			.concatMap(
				(Function<Integer, ObservableSource<?>>)integer -> Observable.just(integer)
					.delay(200, TimeUnit.MILLISECONDS))
			.doOnNext(val -> System.out.println("onNext : " + val))
			.buffer(
				Observable.interval(1, TimeUnit.SECONDS)
					.doOnNext(val -> System.out.println("opening")),
				(Function<Long, ObservableSource<?>>)aLong -> Observable.interval(500, TimeUnit.MILLISECONDS)
					.doOnNext(val -> System.out.println("closing")))
			.subscribe(
				list -> System.out.println(attachWithElapsedTime(list.toString())),
				throwable -> releaseLock(),
				this::releaseLock);

		waitForLock();
	}

	@Test
	public void testWindow() {

		prepareLock();
		prepareElapsedTime();

		Observable.range(0, 20)
			.concatMap(
				(Function<Integer, ObservableSource<?>>)integer -> Observable.just(integer)
					.delay(200, TimeUnit.MILLISECONDS))
			.window(1L, 3L, TimeUnit.SECONDS)
			.flatMap(value -> value)
			.subscribe(
				value -> System.out.println(attachWithElapsedTime(value.toString())),
				throwable -> releaseLock(),
				this::releaseLock);

		waitForLock();
	}

	@Test
	public void testDebounce() {

		prepareLock();

		Observable.range(0, 10)
			.concatMap(
				(Function<Integer, ObservableSource<?>>)value -> Observable.just(value)
					.delay(value < 5 ? 200 : 600, TimeUnit.MILLISECONDS))
			.debounce(400, TimeUnit.MILLISECONDS)
			.subscribe(
				value -> System.out.println(value.toString()),
				throwable -> releaseLock(),
				this::releaseLock);

		waitForLock();
	}

	@Test
	public void testDebounceSelector() {

		prepareLock();

		Observable.range(0, 10)
			.concatMap(
				(Function<Integer, ObservableSource<?>>)value -> Observable.just(value)
					.delay(200, TimeUnit.MILLISECONDS))
			.debounce(x -> Observable.empty().delay((Integer)x < 5 ? 400 : 100, TimeUnit.MILLISECONDS))
			.subscribe(
				value -> System.out.println(value.toString()),
				throwable -> releaseLock(),
				this::releaseLock);

		waitForLock();
	}

	@Test
	public void testTimedDebounce() {
		prepareLock();

		ConnectableObservable<Long> upstream = Observable.interval(200, TimeUnit.MILLISECONDS)
			.publish();

		upstream
			.compose(stream -> timedDebounce(stream, 400, 3000, TimeUnit.MILLISECONDS))
			.take(5)
			.subscribe(
				System.out::println,
				throwable -> releaseLock(),
				this::releaseLock);

		upstream.connect();

		waitForLock();
	}

	private <T> Observable<T> timedDebounce(Observable<T> upstream, long timeout, long silentTimeout,
		TimeUnit timeUnit) {
		Observable<T> onTimeout = upstream.take(1)
			.concatWith(Observable.defer(() -> timedDebounce(upstream, timeout, silentTimeout, timeUnit)));

		return upstream
			.debounce(timeout, timeUnit)
			.timeout(silentTimeout, timeUnit, onTimeout);
	}
}
