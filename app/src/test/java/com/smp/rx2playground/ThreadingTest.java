package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by myungpyo.shim on 2017. 3. 2..
 *
 */

public class ThreadingTest extends BasePlayground {

	@Test
	public void testScheduler() {

		prepareLock();

		Observable<Integer> observable = Observable.create(subscriber -> {
			subscriber.onNext(1);
			subscriber.onNext(2);
			subscriber.onNext(3);
			subscriber.onNext(4);
			subscriber.onComplete();
		});

		observable
			.doOnNext(i -> {
				System.out.println(attachWithTid("doOnNext : " + i));
			})
			.filter(i -> i % 2 == 0)
			.map(i -> i * 2)
			.subscribeOn(Schedulers.computation())
			.observeOn(Schedulers.io())
			.subscribe(nextValue -> System.out.println(attachWithTid("subscribed : " + nextValue)),
				error -> {
					attachWithTid("error occurred : " + error.getMessage());
					releaseLock();
				},
				() -> {
					attachWithTid("complete");
					releaseLock();
				});

		waitForLock();

	}

	@Test
	public void testSchedulerInTheInnerObservable() {

		prepareLock();

		final int mapWidth = 10000;
		final int mapHeight = 10000;
		final int treasureHunterCount = 10;
		final int sectionLengthForTreasureHunter = mapHeight / treasureHunterCount;

		char treasureMap[][] = new char[mapWidth][mapHeight];
		setTreasures(treasureMap);

		Scheduler treasureHunter = createFixedScheduler("TreasureHunterPool", treasureHunterCount, treasureHunterCount);

		Observable.range(0, treasureHunterCount)
			.map(val -> new Range(
				val * sectionLengthForTreasureHunter,
				(sectionLengthForTreasureHunter + val * sectionLengthForTreasureHunter) - 1))
			.flatMap(
				range -> Observable.range(range.a, range.b - range.a + 1)
					.flatMap(x -> Observable.range(0, mapWidth)
									.map(y -> new Point(x, y))
									.subscribeOn(treasureHunter)
							)
					.filter(point -> treasureMap[point.x][point.y] != 0))
			.subscribe(
				val -> System.out.println(attachWithTid("Found a treasure at " + val)),
				throwable -> releaseLock(),
				() -> {System.out.println(attachWithTid("onComplete")); releaseLock();});


		waitForLock();

	}

	@Test
	public void testSchedulerInTheInnerObservableWithGroupBy() {

		prepareLock();

		final int mapWidth = 10000;
		final int mapHeight = 10000;
		final int treasureHunterCount = 10;
		final int sectionLengthForTreasureHunter = mapHeight / treasureHunterCount;

		char treasureMap[][] = new char[mapWidth][mapHeight];
		setTreasures(treasureMap);

		Scheduler treasureHunter = createFixedScheduler("TreasureHunterPool", treasureHunterCount, treasureHunterCount);

		Observable.range(0, treasureHunterCount)
			.map(val -> new Range(
				val * sectionLengthForTreasureHunter,
				(sectionLengthForTreasureHunter + val * sectionLengthForTreasureHunter) - 1))
			.flatMap(
				range -> Observable.range(range.a, range.b - range.a + 1)
					.flatMap(x -> Observable.range(0, mapWidth)
									.map(y -> new Point(x, y))
									.subscribeOn(treasureHunter)
							)
					.filter(point -> treasureMap[point.x][point.y] != 0))
			.groupBy(point -> treasureMap[point.x][point.y])
			.compose(groups -> toObservable(groups,
				keyCountPair -> new TreasureInfo(keyCountPair.first, keyCountPair.second)))
			.subscribe(
				val -> System.out.println(attachWithTid("Found a treasure at " + val)),
				throwable -> releaseLock(),
				() -> {System.out.println(attachWithTid("onComplete")); releaseLock();});


		waitForLock();

	}

	private <Key, Obj, Result> ObservableSource<Result> toObservable(
		Observable<GroupedObservable<Key, Obj>> upstream,
		Function<Pair<Key, Long>, Result> mapper) {

		return upstream.flatMap(grouped -> Observable.create(emitter -> grouped
			.count()
			.map(quantity -> mapper.apply(new Pair<>(grouped.getKey(), quantity)))
			.subscribe(treasureInfo -> {
					emitter.onNext(treasureInfo);
					emitter.onComplete();
				},
				emitter::onError)));
	}

	private void setTreasures(char[][] treasureMap) {
		treasureMap[10][8] = '*';
		treasureMap[100][10] = '+';
		treasureMap[1200][50] = '+';
		treasureMap[3200][1110] = '*';
		treasureMap[5610][300] = '*';
		treasureMap[7231][3300] = '*';
		treasureMap[7710][2600] = '-';
		treasureMap[8123][5000] = '*';
		treasureMap[8888][7777] = '*';
		treasureMap[9103][9864] = '*';
	}

	private static class Range {
		int a;
		int b;

		Range(int a, int b) {
			this.a = a;
			this.b = b;
		}

		@Override
		public String toString() {
			return "A : " + a + " B : " + b;
		}
	}

	private static class Point {
		int x;
		int y;

		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "X : " + x + " Y : " + y;
		}
	}

	private static class TreasureInfo {
		char name;
		long quantity;

		TreasureInfo(char name, long quantity) {
			this.name = name;
			this.quantity = quantity;
		}

		@Override
		public String toString() {
			return "name : " + name + " quantity : " + quantity;
		}
	}

	private static class Pair<F, S> {
		F first;
		S second;

		Pair(F first, S second) {
			this.first = first;
			this.second = second;
		}
	}

}
