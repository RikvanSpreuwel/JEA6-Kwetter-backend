package nl.fontys.utils;


import com.google.gson.*;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateSerializer implements JsonSerializer<Date> {
    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        return date == null ? null : new JsonPrimitive(
                new SimpleDateFormat("yyyy-MM-dd").format(date.getTime()));
    }
}
