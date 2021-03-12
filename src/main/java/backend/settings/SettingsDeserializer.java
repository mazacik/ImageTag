package backend.settings;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class SettingsDeserializer extends StdDeserializer<Settings> {
	
	public SettingsDeserializer() {
		super(Settings.class);
	}
	
	@Override
	public Settings deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
		
		String nodeName = node.fieldNames().next();
		JsonNode valueNode = node.get(nodeName);
		
		Object value;
		if (valueNode.isBoolean()) {
			value = valueNode.booleanValue();
		} else if (valueNode.isInt()) {
			value = valueNode.intValue();
		} else if (valueNode.isTextual()) {
			value = valueNode.textValue();
		} else {
			throw new RuntimeException();
		}
		
		Settings setting = Settings.valueOf(nodeName);
		setting.setValue(value);
		
		return setting;
	}
	
}
