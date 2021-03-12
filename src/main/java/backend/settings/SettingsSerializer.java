package backend.settings;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class SettingsSerializer extends StdSerializer<Settings> {
	
	public SettingsSerializer() {
		super(Settings.class);
	}
	
	@Override public void serialize(Settings setting, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject();
		gen.writeFieldName(setting.name());
		gen.writeObject(setting.getValue());
		gen.writeEndObject();
	}
	
}