package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;

/**
 * Created by myungpyo.shim on 2017. 3. 27..
 *
 */

public class OperatorConcatTest extends BasePlayground {

	@Test
	public void testConcatOperator() {

		Observable<Integer> numbers = Observable.range(1, 10);

		Observable.concat(
			numbers.take(3),
			numbers.takeLast(3)
		 ).subscribe(System.out::println);

	}
}
