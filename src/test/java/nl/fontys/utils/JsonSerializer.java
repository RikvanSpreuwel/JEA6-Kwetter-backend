package nl.fontys.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.util.Calendar;
import java.util.Date;

public class JsonSerializer {
    public static String toJson(Object objectToSerialize){
        Gson gson = createGsonSerializer();
        return gson.toJson(objectToSerialize);
    }

    public static <T> T fromJson(String textToDeserialize, Class<T> type){
        try {
            Gson gson = createGsonSerializer();
            return gson.fromJson(textToDeserialize, type);
        } catch (JsonParseException e){
            e.printStackTrace();
            return null;
        }
    }

    private static Gson createGsonSerializer(){
        return new GsonBuilder()
                .registerTypeHierarchyAdapter(Date.class, new DateSerializer())
                .registerTypeHierarchyAdapter(Date.class, new DateDeserializer())
                .create();
    }
}
