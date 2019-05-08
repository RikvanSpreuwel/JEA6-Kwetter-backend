package nl.fontys.Utils.JsonSerializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import nl.fontys.models.Kwetter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KwetterListJsonSerializer extends StdSerializer<List<Kwetter>> {
    public KwetterListJsonSerializer(){
        this(null);
    }

    protected KwetterListJsonSerializer(Class<List<Kwetter>> t) {
        super(t);
    }

    @Override
    public void serialize(List<Kwetter> items, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        final List<Kwetter> kwetters = new ArrayList<>();
        items.forEach(item -> {
            item.setAuthor(null);
            kwetters.add(item);
        });
        jsonGenerator.writeObject(kwetters);
    }
}
