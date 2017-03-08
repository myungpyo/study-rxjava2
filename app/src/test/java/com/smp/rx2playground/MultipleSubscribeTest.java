package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by myungpyo.shim on 2017. 3. 2..
 *
 */

public class MultipleSubscribeTest extends BasePlayground {

	@Test
	public void testScheduledMultipleSubscribe() {
		prepareLock(2);

		Observable<Integer> numberObservable = Observable.create(subscriber -> {
			System.out.println(attachWithTid("Subscriber has been created."));
			for (int num = 1; num <= 5; num++) {
				if (subscriber.isDisposed()) {
					System.out.println(attachWithTid("Emission has been disposed."));
					subscriber.onComplete();
					return;
				}
				subscriber.onNext(num);
			}
			subscriber.onComplete();
		});

		// Scheduler for Observable
		numberObservable = numberObservable.observeOn(Schedulers.io());
		// Scheduler for Subscriber
		numberObservable = numberObservable.subscribeOn(Schedulers.computation());

		// First subscribe
		numberObservable.subscribe(
			val -> System.out.println(attachWithTid("S1 : subscribed : " + val)),
			error -> {
				attachWithTid("S1 : error occurred : " + error.getMessage());
				releaseLock();
			},
			() -> {
				attachWithTid("S1 : complete");
				releaseLock();
			});

		// Second subscribe
		numberObservable.subscribe(
			val -> System.out.println(attachWithTid("S2 : subscribed : " + val)),
			error -> {
				attachWithTid("S2 : error occurred : " + error.getMessage());
				releaseLock();
			},
			() -> {
				attachWithTid("S2 : complete");
				releaseLock();
			});

		waitForLock();
	}

	@Test
	public void testScheduledMultipleSubscribeWithCache() {
		prepareLock(2);

		Observable<Integer> numberObservable = Observable.create(subscriber -> {
			System.out.println(attachWithTid("Subscriber has been created."));
			for (int num = 1; num <= 5; num++) {
				if (subscriber.isDisposed()) {
					System.out.println("Emission has been disposed.");
					subscriber.onComplete();
					return;
				}
				subscriber.onNext(num);
			}
			subscriber.onComplete();
		});

		// Scheduler for Observable
		numberObservable = numberObservable.observeOn(Schedulers.io());
		// Scheduler for Subscriber
		numberObservable = numberObservable.subscribeOn(Schedulers.computation());

		/* CACHE will create just one subscription */
		numberObservable = numberObservable.cache();

		// First subscribe
		numberObservable.subscribe(
			val -> System.out.println(attachWithTid("S1 : subscribed : " + val)),
			error -> {
				attachWithTid("S1 : error occurred : " + error.getMessage());
				releaseLock();
			},
			() -> {
				attachWithTid("S1 : complete");
				releaseLock();
			});

		// Second subscribe
		numberObservable.subscribe(
			val -> System.out.println(attachWithTid("S2 : subscribed : " + val)),
			error -> {
				attachWithTid("S2 : error occurred : " + error.getMessage());
				releaseLock();
			},
			() -> {
				attachWithTid("S2 : complete");
				releaseLock();
			});

		waitForLock();
	}
}
