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
        JsonObject root = json.getAsJsonObject();
        JsonObject date = root.getAsJsonObject("date");
        JsonObject time = root.getAsJsonObject("time");

        int year = date.get("year").getAsInt();
        int month = date.get("month").getAsInt();
        int day = date.get("day").getAsInt();
        int hour = time.get("hour").getAsInt();

        return LocalDateTime.of(year, month, day, hour, 0);
    }

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject root = new JsonObject();

        JsonObject date = new JsonObject();
        date.addProperty("year", src.getYear());
        date.addProperty("month", src.getMonthValue());
        date.addProperty("day", src.getDayOfMonth());

        JsonObject time = new JsonObject();
        time.addProperty("hour", src.getHour());

        root.add("date", date);
        root.add("time", time);

        return root;
    }
}