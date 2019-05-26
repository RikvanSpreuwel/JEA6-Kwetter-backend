package nl.fontys.utils;

import nl.fontys.data.repositories.JPAKwetterRepository;
import nl.fontys.data.repositories.JPAUserRepository;
import nl.fontys.models.entities.Kwetter;
import nl.fontys.models.entities.Role;
import nl.fontys.models.entities.User;

import java.util.Calendar;

public class DatabaseInserter {
    public static void insertMockData(final JPAKwetterRepository kwetterRepository, final JPAUserRepository userRepository){
        for (int i = 0; i < 10; i++) {
            final User user = new User("password" + i, "emailof" + i + "@mail.com", "fn" + i, "ln" + i, "un" + i, Calendar.getInstance().getTime(), "bioOf" + i, "locOf" + i, null, Role.ROLE_USER);

            userRepository.save(user);

            final Kwetter kwetter = user.postKwetter("User " + i + "post");

            kwetterRepository.save(kwetter);
        }
    }
}
