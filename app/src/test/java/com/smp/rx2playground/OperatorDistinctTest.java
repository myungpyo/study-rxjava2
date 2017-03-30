package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;

/**
 * Created by myungpyo.shim on 2017. 3. 27..
 *
 */

public class OperatorDistinctTest extends BasePlayground {

	@Test
	public void testDistinctOperator() {

		String[] students = {"Robert", "Mark", "Tim", "Jenifer", "Lucas", "Mark", "Jenifer"};

		Observable.fromArray(students)
			.distinct()
			.subscribe(System.out::println, System.out::println);

	}
}
