package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;

/**
 * Created by myungpyo.shim on 2017. 3. 27..
 *
 */

public class OperatorIgnoreElementTest extends BasePlayground {

	@Test
	public void testCollectOperator() {

		Observable.range(1, 10)
			.ignoreElements()
			.doOnError((error) -> System.out.println("Error"))
			.subscribe(() -> System.out.println("Complete"));

	}
}
