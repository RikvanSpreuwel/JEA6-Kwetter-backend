package nl.fontys.utils;

import com.google.gson.*;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateDeserializer implements JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            if (jsonElement == null) return null;

            final Calendar returnValue = Calendar.getInstance();

            returnValue.setTime(new SimpleDateFormat("yyyy-MM-dd")
                    .parse(jsonElement.getAsString()));

            return returnValue.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}