package common.Logging;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class CustomSerializer implements JsonSerializer<LogObject1> {

    @Override
    public JsonElement serialize(LogObject1 logObject1, Type type, JsonSerializationContext jsonSerializationContext) {
        return null;
    }
}
