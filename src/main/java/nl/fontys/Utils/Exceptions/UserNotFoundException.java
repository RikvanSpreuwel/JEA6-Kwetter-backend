package nl.fontys.Utils.Exceptions;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID id) {
        super("Could not find a user with id: " + id);
    }
}
