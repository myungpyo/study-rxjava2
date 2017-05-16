package com.smp.rx2playground;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import io.reactivex.Observable;

/**
 * Created by myungpyo.shim on 2017. 3. 20..
 *
 */

public class SampleTest extends BasePlayground {

	@Test
	public void testSample() {

		PM10 defaultPM10 = new PM10(0);
		PM2_5 defaultPM25 = new PM2_5(0);

		prepareLock();
		prepareElapsedTime();

		Observable.zip(
			startTemperatureSensor(),
			startHumiditySensor(),
			((temperature, humidity) -> new AirQuality(temperature, humidity, defaultPM10, defaultPM25)))
			.sample(1, TimeUnit.SECONDS)
			.take(10)
			.subscribe(
				(val) -> System.out.println(attachWithElapsedTime(val.toString())),
				throwable -> releaseLock(),
				this::releaseLock);

		waitForLock();
	}

	@Test
	public void testCustomSample() {

		PM10 defaultPM10 = new PM10(0);
		PM2_5 defaultPM25 = new PM2_5(0);

		prepareLock();
		prepareElapsedTime();

		Observable.zip(
			startTemperatureSensor(),
			startHumiditySensor(),
			((temperature, humidity) -> new AirQuality(temperature, humidity, defaultPM10, defaultPM25)))
			.sample(cpuIdleSampler())
			.take(10)
			.subscribe(
				(val) -> System.out.println(attachWithElapsedTime(val.toString())),
				throwable -> releaseLock(),
				this::releaseLock);

		waitForLock();

	}

	private Observable<Long> cpuIdleSampler() {
		return Observable.interval(0, 1, TimeUnit.SECONDS)
			.filter(value -> value % 3 == 0);
	}

	private Observable<Temperature> startTemperatureSensor() {
		Random random = new Random(System.currentTimeMillis());

		return Observable
			.interval(0, 200, TimeUnit.MILLISECONDS)
			.map(v -> new Temperature(random.nextFloat() * 100 % 40));
	}

	private Observable<Humidity> startHumiditySensor() {
		Random random = new Random(System.currentTimeMillis());

		return Observable
			.interval(0, 500, TimeUnit.MILLISECONDS)
			.map(v -> new Humidity(random.nextFloat() * 100 % 100));
	}

	private class AirQuality {
		private Temperature temperature;
		private Humidity humidity;
		private PM10 pm10;
		private PM2_5 pm2_5;

		public AirQuality(Temperature temperature, Humidity humidity, PM10 pm10,
			PM2_5 pm2_5) {
			this.temperature = temperature;
			this.humidity = humidity;
			this.pm10 = pm10;
			this.pm2_5 = pm2_5;
			System.out.println(attachWithTid("Zipping as AirQuality"));
		}

		@Override
		public String toString() {
			return String.format(Locale.ENGLISH,
				"Temperature : %.1f C, Humidity : %.1f %%, PM10 : %d, PM2.5 : %d",
				temperature.value, humidity.value, pm10.value, pm2_5.value);
		}
	}

	private static class Temperature {
		private float value;

		public Temperature(float value) {
			this.value = value;
		}
	}

	private static class Humidity {
		private float value;

		public Humidity(float value) {
			this.value = value;
		}
	}

	private static class PM10 {
		private int value;

		public PM10(int value) {
			this.value = value;
		}
	}

	private static class PM2_5 {
		private int value;

		public PM2_5(int value) {
			this.value = value;
		}
	}

}
