package com.smp.rx2playground;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function4;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by myungpyo.shim on 2017. 3. 20..
 *
 */

public class PracticalSampleTest extends BasePlayground {

	@Test
	public void testPracticalSample() {

		AirConditioner airConditioner = new AirConditioner();
		ReactiveSampleApi sampleApi = new ReactiveSampleApi();

		prepareLock();

		Observable.zip(
			sampleApi.loadTemperature(),
			sampleApi.loadHumidity(),
			sampleApi.loadPM10(),
			sampleApi.loadPM2_5(),
			AirQuality::new)
			.repeat(10)
			.take(10)
			.subscribeOn(Schedulers.io())
			.observeOn(Schedulers.computation())
			.subscribe(airConditioner::updateAirQuality, throwable -> releaseLock(), this::releaseLock);

		waitForLock();

	}

	private static class ReactiveSampleApi {

		private LegacySampleAPI legacySampleAPI = new LegacySampleAPI();

		public Observable<Temperature> loadTemperature() {
			return Observable.defer(() -> Observable.just(legacySampleAPI.loadTemperature()));
		}

		public Observable<Humidity> loadHumidity() {
			return Observable.defer(() -> Observable.just(legacySampleAPI.loadHumidity()));
		}

		public Observable<PM10> loadPM10() {
			return Observable.defer(() -> Observable.just(legacySampleAPI.loadPM10()));
		}

		public Observable<PM2_5> loadPM2_5() {
			return Observable.defer(() -> Observable.just(legacySampleAPI.loadPM2_5()));
		}
	}

	private static class LegacySampleAPI {

		private Random random = new Random(System.currentTimeMillis());

		public Temperature loadTemperature() {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				throw new RuntimeException("Failed to load temperature");
			}
			return new Temperature(random.nextFloat() * 100 % 40);
		}

		public Humidity loadHumidity() {
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				throw new RuntimeException("Failed to load humidity");
			}
			return new Humidity(Math.abs(random.nextFloat() * 100 % 80));
		}

		public PM10 loadPM10() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				throw new RuntimeException("Failed to load pm 10");
			}
			return new PM10(Math.abs(random.nextInt() % 200));
		}

		public PM2_5 loadPM2_5() {
			try {
				Thread.sleep(900);
			} catch (InterruptedException e) {
				throw new RuntimeException("Failed to load pm 2.5");
			}
			return new PM2_5(Math.abs(random.nextInt() % 100));
		}
	}

	private static class AirConditioner {

		private AirConditioningMode mode = AirConditioningMode.MODE_NONE;

		public void updateAirQuality(AirQuality airQuality) {
			System.out.println("Air condition is updated : " + airQuality.toString());

			if (airQuality.pm10.value > 80 || airQuality.pm2_5.value > 80) {
				changeMode(AirConditioningMode.MODE_AIR_CLEANING);
				return;
			}

			if (airQuality.humidity.value > 50) {
				changeMode(AirConditioningMode.MODE_DEHUMIDIFYING);
				return;
			}

			if (airQuality.temperature.value < 24) {
				changeMode(AirConditioningMode.MODE_WARMING);
			} else {
				changeMode(AirConditioningMode.MODE_COOLING);
			}
		}

		private void changeMode(AirConditioningMode mode) {
			this.mode = mode;
			System.out.println("Air conditioning mode has been changed to " + this.mode);
		}

		enum AirConditioningMode {
			MODE_NONE,
			MODE_WARMING,
			MODE_COOLING,
			MODE_DEHUMIDIFYING,
			MODE_AIR_CLEANING
		}
	}

	private static class AirQuality {
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
