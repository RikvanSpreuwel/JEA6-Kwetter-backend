package nl.fontys.utils;

import nl.fontys.data.repositories.JPAUserRepository;

public class DatabaseEmptier {
    public static void removeData(final JPAUserRepository userRepository){
        userRepository.deleteAll();
    }
}
