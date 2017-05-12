package com.smp.rx2playground;

import java.util.Locale;

import org.junit.Test;

import io.reactivex.Single;

/**
 * Created by myungpyo.shim on 2017. 5. 12..
 *
 */

public class SingleTest extends BasePlayground {

	@Test
	public void testJust() {

		Single<String> single = Single.just("a value");

		single.subscribe(System.out::println);
	}

	@Test
	public void testError() {
		Single<String> single = Single.error(new RuntimeException("What the!!"));

		single.subscribe(System.out::println, System.out::println);
	}

	@Test
	public void testCustom() {

		temperature()
			.subscribe(System.out::println, System.out::println);
	}

	@Test
	public void testZip() {

		Single.zip(
			temperature(), humidity(), pm10(), pm25(),
			AirQuality::new);
	}

	private Single<Temperature> temperature() {
		return Single.create(emitter -> {
			try {
				emitter.onSuccess(new Temperature(30f));
			} catch (Throwable t) {
				emitter.onError(t);
			}
		});
	}

	private Single<Humidity> humidity() {
		return Single.create(emitter -> {
			try {
				emitter.onSuccess(new Humidity(40f));
			} catch (Throwable t) {
				emitter.onError(t);
			}
		});
	}

	private Single<PM10> pm10() {
		return Single.create(emitter -> {
			try {
				emitter.onSuccess(new PM10(34));
			} catch (Throwable t) {
				emitter.onError(t);
			}
		});
	}

	private Single<PM2_5> pm25() {
		return Single.create(emitter -> {
			try {
				emitter.onSuccess(new PM2_5(15));
			} catch (Throwable t) {
				emitter.onError(t);
			}
		});
	}

	private static class Temperature {
		private float value;

		public Temperature(float value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "Temperature : " + value;
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

	private class AirQuality {
		private Temperature temperature;
		private Humidity humidity;
		private PM10 pm10;
		private PM2_5 pm2_5;

		public AirQuality(Temperature temperature, Humidity humidity, PM10 pm10, PM2_5 pm2_5) {
			this.temperature = temperature;
			this.humidity = humidity;
			this.pm10 = pm10;
			this.pm2_5 = pm2_5;
		}

		public void setTemperature(Temperature temperature) {
			this.temperature = temperature;
		}

		public void setHumidity(Humidity humidity) {
			this.humidity = humidity;
		}

		public void setPm10(PM10 pm10) {
			this.pm10 = pm10;
		}

		public void setPm2_5(PM2_5 pm2_5) {
			this.pm2_5 = pm2_5;
		}

		@Override
		public String toString() {
			return String.format(Locale.ENGLISH,
				"Temperature : %.1f C, Humidity : %.1f %%, PM10 : %d, PM2.5 : %d",
				temperature.value, humidity.value, pm10.value, pm2_5.value);
		}
	}
}
