package com.github.lucasvc.rsyslog_tests.log4j2_rollover;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MapMessage;

public class LoggingRollover {

	private static final Random random = new Random();

	public static void main(String[] args) throws Exception {
		Path output = Paths.get("build/logs");
		System.setProperty("outputDir", output.toString());
		String fileName = "json-rollover.log";
		System.setProperty("fileName", fileName);
		Logger logger = LogManager.getLogger("services");
		int i = 0;
		while (!hasRolled(output, fileName, i, 10)) {
			Map<String, String> event = new HashMap<>(3);
			event.put("name", "LoggingRollover");
			event.put("component", "rsyslog");
			event.put("trace_id", String.valueOf(i++));
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
			Thread.sleep(5);
		}
		List<String> lines = Files.readAllLines(Paths.get("rsyslog/output/recieved.log"), StandardCharsets.UTF_8);
		if (i != lines.size())
			throw new AssertionError("Expected '" + i + "' lines but got '" + lines.size() + "'");
	}

	private static String generateRand(int length) {
		return new BigInteger(length * 5, random).toString(32);
	}

	private static boolean hasRolled(Path output, final String fileName, int index, int times) throws Exception {
		if (index % 10000 != 0)
			return false;

		String[] listed = output.toFile().list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return (name.startsWith(fileName)) ? true : false;
			}
		});
		return listed.length >= times;
	}

}
