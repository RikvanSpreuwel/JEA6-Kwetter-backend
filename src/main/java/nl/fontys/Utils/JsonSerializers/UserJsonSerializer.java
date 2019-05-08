package nl.fontys.Utils.JsonSerializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import nl.fontys.models.User;

import java.io.IOException;

public class UserJsonSerializer extends StdSerializer<User> {
    public UserJsonSerializer(){
        this(null);
    }

    protected UserJsonSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        user.setFollowers(null);
        user.setFollowing(null);
        user.setKwetters(null);
        jsonGenerator.writeObject(user);
    }
}
