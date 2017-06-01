package com.github.lucasvc.rsyslog_tests.log4j2_rollover;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.time.Instant;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.util.Strings;

import com.fasterxml.jackson.databind.ObjectMapper;

@Plugin(name = "SingleLineJsonLayout", category = Node.CATEGORY, elementType = Layout.ELEMENT_TYPE, printObject = true)
public class SingleLineJsonLayout extends AbstractStringLayout {

	private static final ObjectMapper mapper = new ObjectMapper();

	public static class Builder<B extends Builder<B>> extends AbstractStringLayout.Builder<B>
			implements org.apache.logging.log4j.core.util.Builder<SingleLineJsonLayout> {

		@Override
		public SingleLineJsonLayout build() {
			return new SingleLineJsonLayout(getCharset());
		}

	}

	@PluginBuilderFactory
	public static <B extends Builder<B>> B newBuilder() {
		return new Builder<B>().asBuilder();
	}

	public SingleLineJsonLayout(Charset charset) {
		super(charset);
	}

	@Override
	public String toSerializable(LogEvent event) {
		StringWriter writer = new StringWriter();
		try {
			MapMessage message = (MapMessage) event.getMessage();
			message.put("level", event.getLevel().name());
			message.put("@timestamp", Instant.now().toString());
			mapper.writeValue(writer, message.getData());
			writer.append("\n");
			return writer.toString();
		} catch (Exception e) {
			return Strings.EMPTY;
		}
	}

}
