package com.smp.rx2playground;

import java.util.concurrent.CountDownLatch;

import android.support.annotation.Nullable;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public abstract class BasePlayground {

	@Nullable
	private CountDownLatch lock;

	protected void prepareLock() {
		prepareLock(1);
	}
	protected void prepareLock(int count) {
		lock = new CountDownLatch(count);
	}

	protected void releaseLock() {
		if (lock == null) {
			return;
		}

		lock.countDown();
	}

	protected boolean waitForLock() {
		if (lock == null) {
			return false;
		}
		try {
			lock.await();
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}

	protected String attachWithTid(String text) {
		return "[Tid : " + Thread.currentThread() + "] " + text;
	}
}
