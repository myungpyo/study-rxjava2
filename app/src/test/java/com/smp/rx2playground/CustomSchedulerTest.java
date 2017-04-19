package com.smp.rx2playground;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import android.support.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by myungpyo.shim on 2017. 3. 27..
 *
 */

public class CustomSchedulerTest extends BasePlayground {

	@Test
	public void testCustomScheduler() {

		ThreadFactory threadFactory = new ThreadFactory() {
			AtomicInteger threadSeq = new AtomicInteger(0);

			@Override
			public Thread newThread(@NonNull Runnable runnable) {
				Thread thread = new Thread(runnable);
				thread.setName("CustomThread#" + threadSeq.incrementAndGet());

				System.out.println("Thread has been created : " + thread.getName());
				return thread;
			}
		};

		Executor executor = new ThreadPoolExecutor(
			2,
			10,
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingDeque<>(3),
			threadFactory
		);

		Scheduler customScheduler = Schedulers.from(executor);

		prepareLock();

		Observable.range(1, 10)
			.flatMap((value) -> Observable.create((ObservableOnSubscribe<Integer>)observer -> {
				try {
					Thread.sleep(2000);
					observer.onNext(value);
					observer.onComplete();
				} catch (Throwable throwable) {
					observer.onError(throwable);
				}

			}).subscribeOn(customScheduler))
			.doOnNext(val -> System.out.println(attachWithTid("onNext(FlatMap) : " + val)))
			.subscribeOn(customScheduler)
			.observeOn(Schedulers.computation())
			.subscribe(System.out::println, (throwable -> releaseLock()), this::releaseLock);

		waitForLock();

	}
}