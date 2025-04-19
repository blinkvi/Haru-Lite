package cc.unknown.util.client.system;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LocalDateTimeStructuredAdapter implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        JsonObject date = obj.get("date").getAsJsonObject();
        JsonObject time = obj.get("time").getAsJsonObject();

        return LocalDateTime.of(
            date.get("year").getAsInt(),
            date.get("month").getAsInt(),
            date.get("day").getAsInt(),
            time.get("hour").getAsInt(),
            time.get("minute").getAsInt()
        );
    }

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        JsonObject date = new JsonObject();
        JsonObject time = new JsonObject();

        date.addProperty("year", src.getYear());
        date.addProperty("month", src.getMonthValue());
        date.addProperty("day", src.getDayOfMonth());

        time.addProperty("hour", src.getHour());
        time.addProperty("minute", src.getMinute());

        result.add("date", date);
        result.add("time", time);
        return result;
    }
}