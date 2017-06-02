package com.github.lucasvc.rsyslog_tests.log4j2_rollover;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MapMessage;

public class LoggingRollover {

	private static final Random random = new Random();

	public static void main(String[] args) throws Exception {
		final Path output = Paths.get("build/logs");
		System.setProperty("outputDir", output.toString());
		final String fileName = "json-rollover.log";
		System.setProperty("fileName", fileName);
		final Logger logger = LogManager.getLogger("services");
		final AtomicInteger trace = new AtomicInteger(0);
		final AtomicBoolean stop = new AtomicBoolean(false);
		final int rolls = 5;
		int threads = 10;
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					while (!hasRolled(output, fileName, rolls)) {
						Thread.sleep(200);
					}
				}
				catch (Exception e) {
					throw new RuntimeException();
				}
				finally {
					stop.set(true);
				}
			}
		});
		for (int i = 0; i < threads; i++) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					while (!stop.get()) {
						Map<String, String> event = new HashMap<>(3);
						event.put("name", "LoggingRollover");
						event.put("component", "rsyslog");
						event.put("trace_id", String.valueOf(trace.getAndIncrement()));
						event.put(generateRand(32), generateRand(32));
						event.put(generateRand(32), generateRand(32));
						event.put(generateRand(32), generateRand(32));
						event.put(generateRand(32), generateRand(32));
						event.put(generateRand(32), generateRand(32));
						event.put(generateRand(32), generateRand(32));
						event.put(generateRand(32), generateRand(32));
						event.put(generateRand(32), generateRand(32));
						event.put(generateRand(32), generateRand(32));
						event.put(generateRand(32), generateRand(32));
						event.put(generateRand(32), generateRand(32));
						logger.info(new MapMessage(event));
					}
				}
			});
		}
		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.MINUTES);

		int expected = trace.get();
		int actual = -1;
		do {
			Thread.sleep(1000);
			actual = Files.readAllLines(Paths.get("rsyslog/output/recieved.log"), StandardCharsets.UTF_8).size();
		} while (actual < expected);
		if (expected < actual)
			throw new AssertionError("Expected '" + expected + "' lines but got '" + actual + "'");
	}

	private static String generateRand(int length) {
		return new BigInteger(length * 5, random).toString(32);
	}

	private static boolean hasRolled(Path output, final String fileName, int times) throws Exception {
		String[] listed = output.toFile().list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.startsWith(fileName)) ? true : false;
			}
		});
		return listed.length >= times;
	}

}
