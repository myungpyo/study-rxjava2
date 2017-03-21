package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;

/**
 * Created by myungpyo.shim on 2017. 3. 20..
 *
 */

public class OperatorMapTest extends BasePlayground {

	@Test
	public void testMap() {

		String text = "Hello Developers!";

		Observable.fromIterable(Utils.asList(text.toCharArray()))
			.map(value -> value.toString().toUpperCase())
			.reduce((value1, value2) -> value1 + value2)
			.subscribe(System.out::println);

	}

}
