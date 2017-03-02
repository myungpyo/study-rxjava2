package com.smp.rx2playground;

import java.util.concurrent.CountDownLatch;

import android.support.annotation.Nullable;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public abstract class BasePlayground {

	@Nullable
	private CountDownLatch lock;

	protected void waitForObservable() {
		waitForObservable(1);
	}

	protected void waitForObservable(int count) {
		lock = new CountDownLatch(count);
		try {
			lock.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void stopWaitingForObservable() {
		if (lock == null) {
			return;
		}
		lock.countDown();
	}

	protected String attachWithTid(String text) {
		return "[Tid : " + Thread.currentThread() + "] " + text;
	}
}
