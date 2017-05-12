package com.smp.rx2playground;

import static java.util.function.Function.*;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import io.reactivex.Observable;

/**
 * Created by myungpyo.shim on 2017. 5. 10..
 *
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class CompletableFutureTest extends BasePlayground {

	@Test
	public void testCompletableFuture() throws ExecutionException, InterruptedException {

		AirQuality resultAirQuality = temperatureAsync()
			.thenCombine(humidityAsync(), (temperature, humidity) -> {
				AirQuality airQuality = new AirQuality();
				airQuality.setTemperature(temperature);
				airQuality.setHumidity(humidity);
				return airQuality;
			})
			.thenCombine(pm10Async(), (airQuality, pm10) -> {
				airQuality.setPm10(pm10);
				return airQuality;
			})
			.thenCombine(pm25Async(), (airQuality, pm25) -> {
				airQuality.setPm2_5(pm25);
				return airQuality;
			})
			.get();

		System.out.println("Result Air Quality : " + resultAirQuality.toString());

	}

	@Test
	public void testAnyOf() throws ExecutionException, InterruptedException {

		AirQuality resultAirQuality = temperatureAsync()
			.thenCombine(humidityAsync(), (temperature, humidity) -> {
				AirQuality airQuality = new AirQuality();
				airQuality.setTemperature(temperature);
				airQuality.setHumidity(humidity);
				return airQuality;
			})
			.thenCombine(pm10Async(), (airQuality, pm10) -> {
				airQuality.setPm10(pm10);
				return airQuality;
			})
			.thenCombine(pm25FromFastestSourceWithAnyOfAsync(), (airQuality, pm25) -> {
				airQuality.setPm2_5(pm25);
				return airQuality;
			})
			.get();

		System.out.println("Result Air Quality : " + resultAirQuality.toString());

	}

	@Test
	public void testApplyToEither() throws ExecutionException, InterruptedException {

		AirQuality resultAirQuality = temperatureAsync()
			.thenCombine(humidityAsync(), (temperature, humidity) -> {
				AirQuality airQuality = new AirQuality();
				airQuality.setTemperature(temperature);
				airQuality.setHumidity(humidity);
				return airQuality;
			})
			.thenCombine(pm10Async(), (airQuality, pm10) -> {
				airQuality.setPm10(pm10);
				return airQuality;
			})
			.thenCombine(pm25FromFastestSourceWithApplyToEitherAsync(), (airQuality, pm25) -> {
				airQuality.setPm2_5(pm25);
				return airQuality;
			})
			.get();

		System.out.println("Result Air Quality : " + resultAirQuality.toString());

	}

	@Test
	public void testConvertCompletableFutureToObservable() throws ExecutionException, InterruptedException {
		CompletableFuture<AirQuality> airQualityCompletableFuture = temperatureAsync()
			.thenCombine(humidityAsync(), (temperature, humidity) -> {
				AirQuality airQuality = new AirQuality();
				airQuality.setTemperature(temperature);
				airQuality.setHumidity(humidity);
				return airQuality;
			})
			.thenCombine(pm10Async(), (airQuality, pm10) -> {
				airQuality.setPm10(pm10);
				return airQuality;
			})
			.thenCombine(pm25Async(), (airQuality, pm25) -> {
				airQuality.setPm2_5(pm25);
				return airQuality;
			});

		Observable<AirQuality> airQualityObservable =
			Observable.create(emitter -> airQualityCompletableFuture.whenComplete((value, exception) -> {

				if (exception != null) {
					emitter.onError(exception);
				} else {
					emitter.onNext(value);
					emitter.onComplete();
				}
			}));

		prepareLock();

		airQualityObservable.subscribe(System.out::println, throwable -> releaseLock(), super::releaseLock);

		waitForLock();
	}

	private CompletableFuture<Temperature> temperatureAsync() {
		return CompletableFuture.supplyAsync(this::temperatureSync);
	}

	private CompletableFuture<Humidity> humidityAsync() {
		return CompletableFuture.supplyAsync(this::humiditySync);
	}

	private CompletableFuture<PM10> pm10Async() {
		return CompletableFuture.supplyAsync(this::pm10Sync);
	}

	private CompletableFuture<PM2_5> pm25Async() {
		return CompletableFuture.supplyAsync(this::pm25Sync);
	}

	private CompletableFuture<PM2_5> pm25Source1Async() {
		return CompletableFuture.supplyAsync(this::pm25Source1Sync);
	}

	private CompletableFuture<PM2_5> pm25Source2Async() {
		return CompletableFuture.supplyAsync(this::pm25Source2Sync);
	}

	private CompletableFuture<PM2_5> pm25Source3Async() {
		return CompletableFuture.supplyAsync(this::pm25Source3Sync);
	}

	private CompletableFuture<PM2_5> pm25FromFastestSourceWithApplyToEitherAsync() {
		return pm25Source1Async()
			.applyToEither(pm25Source2Async(), identity())
			.applyToEither(pm25Source3Async(), identity());
	}

	@SuppressWarnings("unchecked")
	@Nullable
	private CompletableFuture<PM2_5> pm25FromFastestSourceWithAnyOfAsync() {
		try {
			return CompletableFuture.class
				.cast(CompletableFuture.anyOf(pm25Source1Async(), pm25Source2Async(), pm25Source3Async()));
		} catch (Throwable t) {
			System.out.println("error : " + t.getMessage());
		}
		return CompletableFuture.supplyAsync(() -> new PM2_5(0));
	}

	private Temperature temperatureSync() {
		try {
			Thread.sleep(2000);
			return new Temperature(22f);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new Temperature(0f);
	}

	private Humidity humiditySync() {
		try {
			Thread.sleep(3000);
			return new Humidity(45f);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new Humidity(0f);
	}

	private PM10 pm10Sync() {
		try {
			Thread.sleep(4000);
			return new PM10(33);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new PM10(0);
	}

	private PM2_5 pm25Sync() {
		try {
			Thread.sleep(5000);
			return new PM2_5(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new PM2_5(0);
	}

	private PM2_5 pm25Source1Sync() {
		try {
			Thread.sleep(6000);
			return new PM2_5(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new PM2_5(0);
	}

	private PM2_5 pm25Source2Sync() {
		try {
			Thread.sleep(2000);
			return new PM2_5(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new PM2_5(0);
	}

	private PM2_5 pm25Source3Sync() {
		try {
			Thread.sleep(4000);
			return new PM2_5(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new PM2_5(0);
	}

	private class AirQuality {
		private Temperature temperature;
		private Humidity humidity;
		private PM10 pm10;
		private PM2_5 pm2_5;

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
