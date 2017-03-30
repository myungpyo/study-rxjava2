package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;

/**
 * Created by myungpyo.shim on 2017. 3. 27..
 *
 */

public class OperatorComposeTest extends BasePlayground {

	@Test
	public void testConcatOperator() {

		String[] numberOrChar = {"1", "2", "A", "B", "C", "D", "3", "4", "E", "5"};

		Observable.fromArray(numberOrChar)
			.compose(this::integerOnly)
			.subscribe(System.out::println);

	}

	private <T> ObservableSource<Integer> integerOnly(Observable<T> upstream) {

		return upstream.filter(t -> {
			if (t instanceof Integer) {
				return true;
			}

			if (t instanceof String) {
				try {
					Integer.parseInt((String)t);
					return true;
				} catch (Throwable throwable) {
					return false;
				}
			}

			return false;
		}).map(o -> Integer.parseInt((String)o));
	}
}
