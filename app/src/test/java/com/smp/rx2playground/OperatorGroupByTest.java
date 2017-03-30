package com.smp.rx2playground;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;

/**
 * Created by myungpyo.shim on 2017. 3. 27..
 *
 */

public class OperatorGroupByTest extends BasePlayground {

	@Test
	public void testGroupByOperator() {

		Robot[] robots = {
			new LaborRobot(),
			new WarRobot(),
			new HarvestRobot(),
			new HarvestRobot(),
			new LaborRobot(),
			new WarRobot(),
			new LaborRobot()
		};

		Observable.fromArray(robots)
			.groupBy((Function<Robot, Class>)Robot::getClass)
			.doOnNext(group -> {System.out.println("Factory has been built : " + getFactoryName(group));})
			.flatMap(group -> group.map(o -> new PackagedRobot(getFactoryName(group) , o)))
			.map(PackagedRobot::move)
			.subscribe(System.out::println);
	}

	String getFactoryName(GroupedObservable<Class, Robot> group) {
		return group.getKey().getSimpleName() + " Factory";
	}

	interface Robot {
		String move();
		String work();
	}

	private class PackagedRobot implements Robot {
		private String factory;
		private Robot robot;

		public PackagedRobot(String factory, Robot robot) {
			this.factory = factory;
			this.robot = robot;
		}

		@Override
		public String move() {
			return "Made in " + factory + " : " + robot.move();
		}

		@Override
		public String work() {
			return "Made in " + factory + " : " + robot.work();
		}
	}

	private static class LaborRobot implements Robot {

		@Override
		public String move() {
			return "Move!! What a busy day!!";
		}

		@Override
		public String work() {
			return "Z~~~~i~~~ing";
		}
	}

	private static class WarRobot implements Robot {

		@Override
		public String move() {
			return "Move Move!!";
		}

		@Override
		public String work() {
			return "Boom ~~~~!!!";
		}
	}

	private static class HarvestRobot implements Robot {

		@Override
		public String move() {
			return "Move!! Let's get through!!";
		}

		@Override
		public String work() {
			return "Ti ki Ti ki ~~";
		}
	}
}
