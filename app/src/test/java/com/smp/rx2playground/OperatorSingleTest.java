package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;

/**
 * Created by myungpyo.shim on 2017. 3. 27..
 *
 */

public class OperatorSingleTest extends BasePlayground {

	@Test
	public void testSingleWithMultipleValuesOperator() {

		Observable.range(1, 10)
			.single(0)
			.subscribe(System.out::println, System.out::println);

	}
}
