package nl.fontys.utils.exceptions;

import java.util.UUID;

public class KwetterNotFoundException extends RuntimeException {
    public KwetterNotFoundException(UUID id) {
        super("Could not find a kwetter with id: " + id);
    }
}
