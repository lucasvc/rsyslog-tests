package com.github.lucasvc.rsyslog_tests.log4j2_rollover;

import java.io.IOException;

import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.appender.rolling.AbstractRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.RolloverDescription;
import org.apache.logging.log4j.core.appender.rolling.RolloverDescriptionImpl;
import org.apache.logging.log4j.core.appender.rolling.action.AbstractAction;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.appender.rolling.action.FileRenameAction;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "SleepableRolloverStrategy", category = Core.CATEGORY_NAME, printObject = true)
public class SleepableRolloverStrategy extends AbstractRolloverStrategy {

	@PluginFactory
	public static SleepableRolloverStrategy createStrategy(
			@PluginAttribute("max") final String max,
			@PluginAttribute("min") final String min,
			@PluginAttribute("fileIndex") final String fileIndex,
			@PluginAttribute("compressionLevel") final String compressionLevelStr,
			@PluginAttribute(value = "sleep", defaultLong = 0L) final long sleepMillis,
			@PluginElement("Actions") final Action[] customActions,
			@PluginAttribute(value = "stopCustomActionsOnError", defaultBoolean = true) final boolean stopCustomActionsOnError,
			@PluginConfiguration final Configuration config) {

		if (sleepMillis > 0) {
			LOGGER.debug("SleepableRolloverStrategy after file rename will sleep the thread for {} milliseconds", sleepMillis);
		}
		return new SleepableRolloverStrategy(DefaultRolloverStrategy.createStrategy(max, min, fileIndex,
				compressionLevelStr, customActions, stopCustomActionsOnError, config), sleepMillis);
	}

	private DefaultRolloverStrategy delegate;
	private long sleepMillis;

	public SleepableRolloverStrategy(DefaultRolloverStrategy delegate, long sleepMillis) {
		super(delegate.getStrSubstitutor());
		this.delegate = delegate;
		this.sleepMillis = sleepMillis;
	}

	@Override
	public RolloverDescription rollover(RollingFileManager manager) throws SecurityException {
		RolloverDescription description = delegate.rollover(manager);
		final FileRenameAction fileRename = (FileRenameAction) description.getSynchronous();
		Action newSynchronous = new AbstractAction() {

			@Override
			public boolean execute() throws IOException {
				fileRename.execute();
				try {
					Thread.sleep(sleepMillis);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				return true;
			}

		};
		return new RolloverDescriptionImpl(description.getActiveFileName(), description.getAppend(), newSynchronous,
				description.getAsynchronous());
	}

}
