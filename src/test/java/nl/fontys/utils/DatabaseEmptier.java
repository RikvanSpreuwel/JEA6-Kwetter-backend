package nl.fontys.utils;

import nl.fontys.data.repositories.JPAKwetterRepository;
import nl.fontys.data.repositories.JPAUserRepository;
import nl.fontys.models.Kwetter;
import nl.fontys.models.Role;
import nl.fontys.models.User;

import java.util.Calendar;

public class DatabaseEmptier {
    public static void removeData(final JPAUserRepository userRepository){
        userRepository.deleteAll();
    }
}
