package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;

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

	public Observable<String> split(String text) {
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

	public Observable<String> splitError(Throwable throwable) {
		System.out.println("------");
		return Observable.just("Error");
	}

	public Observable<String> splitComplete() {
		return Observable.just("Complete");
	}

}
