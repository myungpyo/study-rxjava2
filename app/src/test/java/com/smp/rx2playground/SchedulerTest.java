package com.smp.rx2playground;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.junit.Test;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by myungpyo.shim on 2017. 3. 2..
 *
 */

public class SchedulerTest extends BasePlayground {

	@Test
	public void testWithoutScheduler() {

		System.out.println(attachWithTid("Starting"));

		Observable<Integer> observable = Observable.create(emitter -> {
			emitter.onNext(1);
			emitter.onNext(2);
			emitter.onNext(3);
			emitter.onComplete();
		});

		System.out.println(attachWithTid("Created"));

		observable.subscribe(
			val -> System.out.println(attachWithTid("onNext " + val)),
			throwable -> {},
			() -> System.out.println(attachWithTid("onComplete")));

		System.out.println(attachWithTid("Exiting"));
	}

	@Test
	public void testWithScheduler() {

		prepareLock();

		System.out.println(attachWithTid("Starting"));

		Observable<Integer> observable = Observable.create(emitter -> {
			emitter.onNext(1);
			emitter.onNext(2);
			emitter.onNext(3);
			emitter.onComplete();
		});

		System.out.println(attachWithTid("Created"));

		observable = observable.subscribeOn(Schedulers.io());

		observable.subscribe(
			val -> System.out.println(attachWithTid("onNext " + val)),
			throwable -> releaseLock(),
			() -> {System.out.println(attachWithTid("onComplete")); releaseLock();});

		System.out.println(attachWithTid("Exiting"));

		waitForLock();
	}

	@Test
	public void testMultipleSubscribeSchedulers() {

		prepareLock();

		Scheduler schedulerA = Schedulers.from(createFixedExecutor("Scheduler-A", 5, 5));
		Scheduler schedulerB = Schedulers.from(createFixedExecutor("Scheduler-B", 5, 5));

		Observable.range(0, 10)
			.filter(val -> val > 3)
			.subscribeOn(schedulerA)
			.filter(val -> val % 2 == 0)
			.subscribeOn(schedulerB)
			.subscribe(
				val -> System.out.println(attachWithTid("onNext " + val)),
				throwable -> releaseLock(),
				() -> {System.out.println(attachWithTid("onComplete")); releaseLock();});

		waitForLock();
	}



}
