package com.smp.rx2playground;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by myungpyo.shim on 2017. 3. 2..
 *
 */

public class TimerTest extends BasePlayground {

	@Test
	public void testTimer() {
		prepareLock();

		Observable.timer(2, TimeUnit.SECONDS)
			.subscribe(
				System.out::println,
				error -> releaseLock(),
				this::releaseLock);

		waitForLock();
	}
}
