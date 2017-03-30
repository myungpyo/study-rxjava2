package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;

/**
 * Created by myungpyo.shim on 2017. 3. 27..
 *
 */

public class OperatorScanTest extends BasePlayground {

	@Test
	public void testScanOperator() {

		Observable.range(1, 10)
			.scan((val1, val2) -> val1 + val2)
			.subscribe(System.out::println);

	}
}
