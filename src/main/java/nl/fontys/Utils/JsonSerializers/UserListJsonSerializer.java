package nl.fontys.Utils.JsonSerializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import nl.fontys.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserListJsonSerializer extends StdSerializer<List<User>> {
    public UserListJsonSerializer(){
        this(null);
    }

    protected UserListJsonSerializer(Class<List<User>> t) {
        super(t);
    }

    @Override
    public void serialize(List<User> items, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        final List<User> users = new ArrayList<>();
        items.forEach(user -> {
            user.setKwetters(null);
            user.setFollowing(null);
            user.setFollowers(null);
            users.add(user);
        });

        jsonGenerator.writeObject(users);
    }
}
