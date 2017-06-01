package com.github.lucasvc.rsyslog_tests.log4j2_rollover;

import java.io.File;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MapMessage;

public class LoggingRollover {

	public static void main(String[] args) throws Exception {
		Path output = Paths.get("build/logs");
		Files.walk(output, FileVisitOption.FOLLOW_LINKS)
			.sorted(Comparator.reverseOrder())
			.map(Path::toFile)
			.forEach(File::delete);
		System.setProperty("outputDir", output.toString());
		String fileName = "json.log";
		System.setProperty("fileName", fileName);
		Logger logger = LogManager.getLogger("services");
		int i = 0;
		while (!hasRolled(output, fileName, 3)) {
			Map<String, String> event = new HashMap<>(3);
			event.put("name", "LoggingRollover");
			event.put("component", "rsyslog");
			event.put("index", String.valueOf(i++));
			logger.info(new MapMessage(event));
		}
	}

	private static boolean hasRolled(Path output, String fileName, int times) throws Exception {
		AtomicInteger counter = new AtomicInteger(0);
		Files.list(output).forEach(current -> {
			if (current.toFile().getName().startsWith(fileName)) {
				counter.incrementAndGet();
			}
		});
		return counter.get() >= times;
	}

}
