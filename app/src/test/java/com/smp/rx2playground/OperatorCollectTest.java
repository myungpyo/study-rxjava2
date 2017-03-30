package com.smp.rx2playground;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.reactivex.Observable;

/**
 * Created by myungpyo.shim on 2017. 3. 27..
 *
 */

public class OperatorCollectTest extends BasePlayground {

	@Test
	public void testCollectOperator() {

		Observable.range(1, 10)
			.collect(ArrayList::new, List::add)
			.map(Utils::collectionToString)
			.subscribe(System.out::println);

	}
}
