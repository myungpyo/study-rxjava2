package com.smp.rx2playground;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by myungpyo.shim on 2017. 3. 20..
 *
 */

public class OperatorFlatMapTest extends BasePlayground {

	@Test
	public void testFlatMap() {

		String[] sentences = new String[] {
			"This is my first experience",
			"Thank you for your kindness",
			"Android is one of the most famous platform for mobile development."
		};

		Observable.fromArray(sentences)
			.flatMap(this::split, Observable::error, this::splitComplete) //What is the purpose of this overloaded function?
			.subscribe(System.out::println, System.out::println, System.out::println);

	}

	@Test
	public void testOrderingOfFlatMap() {

		prepareLock();

		String[] sentences = new String[] {
			"This is my first experience",
			"Thank you for your kindness",
			"Android is one of the most famous platform for mobile development."
		};

		Observable.fromArray(sentences)
			.doOnNext(System.out::println)
			.flatMap(sentence -> splitWithDelay(sentence, 1000)) // Can use concatMap() for the order of upstream.
			.subscribe(new Observer<String>() {
				@Override
				public void onSubscribe(Disposable d) {

				}

				@Override
				public void onNext(String s) {
					System.out.println(s);
				}

				@Override
				public void onError(Throwable e) {
					System.out.println("onError : " + e.getMessage());
					releaseLock();
				}

				@Override
				public void onComplete() {
					System.out.println("onComplete");
					releaseLock();
				}
			});

		waitForLock();

	}

	@Test
	public void testStreamRestrictionOfFlatMap() {

		prepareLock();

		String[] articleUrls = new String[] {
			"http://www.it-news.com/article/100",
			"http://www.it-news.com/article/101",
			"http://www.it-news.com/article/102",
			"http://www.it-news.com/article/103",
			"http://www.it-news.com/article/104",
			"http://www.it-news.com/article/105",
			"http://www.it-news.com/article/106",
			"http://www.it-news.com/article/107",
			"http://www.it-news.com/article/108",
			"http://www.it-news.com/article/109",
		};

		Observable.fromArray(articleUrls)
			.flatMap(this::mockHttpCall, 3)
			.subscribe(System.out::println, e -> releaseLock(), this::releaseLock);

		waitForLock();

	}

	private Observable<String> split(String text) {
		return Observable.create(e -> {
			try {
				String[] tokens = text.split(" ");

				int index = 0;
				for (String token : tokens) {
					if (index >= 5) {
						throw new IllegalArgumentException("Text is too long.");
					}
					e.onNext(token);
					index++;
				}
			} catch (Throwable t) {
				e.onError(t);
				return;
			}
			e.onComplete();
		});
	}

	private Observable<String> splitWithDelay(String text, long delayInMills) {
		return Observable.create(e -> {
			// This is bad way but for testing.
			new Thread() {
				@Override
				public void run() {
					try {
						String[] tokens = text.split(" ");

						int index = 0;
						for (String token : tokens) {

							Thread.sleep(delayInMills);

							e.onNext(token);
							index++;
						}
					} catch (Throwable t) {
						e.onError(t);
						return;
					}
					e.onComplete();
				}
			}.start();
		});
	}

	private Observable<String> mockHttpCall(String url) {
		return Observable.create(e -> {
			// This is bad way but for testing.
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(3000);
						e.onNext(url.toUpperCase());
					} catch (Throwable t) {
						e.onError(t);
						return;
					}
					e.onComplete();
				}
			}.start();
		});
	}

	public Observable<String> splitError(Throwable throwable) {
		System.out.println("------");
		return Observable.just("Error");
	}

	private Observable<String> splitComplete() {
		return Observable.just("Complete");
	}

}
