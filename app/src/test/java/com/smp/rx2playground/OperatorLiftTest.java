package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by myungpyo.shim on 2017. 3. 27..
 *
 */

public class OperatorLiftTest extends BasePlayground {

	@Test
	public void testLiftOperator() {

		Observable
			.range(1, 10)
			.lift(masking())
			.subscribe(System.out::println);

	}

	private ObservableOperator<String, Integer> masking() {
		return new ObservableOperator<String, Integer>() {
			@Override
			public Observer<? super Integer> apply(Observer<? super String> observer) throws Exception {
				return new Observer<Integer>() {
					@Override
					public void onSubscribe(Disposable d) {
						observer.onSubscribe(d);
					}

					@Override
					public void onNext(Integer integer) {
						try {
							Observable.
								range(0, integer)
								.map(value -> "*")
								.reduce((val1, val2) -> val1 + val2)
								.subscribe(observer::onNext, this::onError);
						} catch (Throwable throwable) {
							onError(throwable);
						}
					}

					@Override
					public void onError(Throwable e) {
						observer.onError(e);
					}

					@Override
					public void onComplete() {
						observer.onComplete();
					}
				};
			}
		};
	}

}
