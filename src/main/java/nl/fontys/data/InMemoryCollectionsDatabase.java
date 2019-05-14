package nl.fontys.data;

import nl.fontys.models.entities.Kwetter;
import nl.fontys.models.entities.Role;
import nl.fontys.models.entities.User;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryCollectionsDatabase {
    private static InMemoryCollectionsDatabase instance;
    private HashMap<UUID, User> users;
    private HashMap<UUID, Kwetter> kwetters;

    private InMemoryCollectionsDatabase(){
        users = new HashMap<>();
        kwetters = new HashMap<>();
        initialize();
    }

    public static InMemoryCollectionsDatabase getInstance(){
        if (instance == null){
            instance = new InMemoryCollectionsDatabase();
        }

        return instance;
    }

    /**
     * This method initializes the in-memory database using collections
     *
     * This method creates 10 users, which all have a kwetter
     */
    private void initialize(){
        for (int i = 0; i < 10; i++) {
            final User user = new User("password" + i, "emailOf" + i + "@mail.com", "fn" + i, "ln" + i, "un" + i, Calendar.getInstance().getTime(), "bioOf" + i, "locOf" + i, null, Role.ROLE_USER);
            user.setId(UUID.randomUUID());

            final Kwetter kwetter = user.postKwetter("User " + i + "post");
            kwetter.setId(UUID.randomUUID());

            users.put(user.getId(), user);
            kwetters.put(kwetter.getId(), kwetter);
        }
    }

    public void reset(){
        instance = null;
    }

    public <S extends Kwetter> S save(S kwetter) {
        kwetter.setId(UUID.randomUUID());
        kwetters.put(kwetter.getId(), kwetter);
        return kwetter;
    }

    public <S extends User> S save(S user){
        user.setId(UUID.randomUUID());
        users.put(user.getId(), user);
        return user;
    }

    public boolean deleteUser(final UUID id){
        Set<UUID> kwetterIdsToDeleteAlongsideUser = new HashSet<>();
        findUserById(id).getKwetters().forEach(kwetter -> kwetterIdsToDeleteAlongsideUser.add(kwetter.getId()));
        kwetterIdsToDeleteAlongsideUser.forEach(this::deleteKwetter);

        return users.remove(id, findUserById(id));
    }

    public boolean deleteKwetter(final UUID id) {
        final Kwetter kwetter = kwetters.get(id);
        final User poster = users.get(kwetter.getAuthor().getId());
        boolean kwetterRemovedFromPoster = poster.removeKwetter(kwetter);

        boolean kwetterRemovedFromData = kwetters.remove(id, kwetter);

        return kwetterRemovedFromPoster && kwetterRemovedFromData;
    }

    public List<User> findAllUsers(){
        return Collections.unmodifiableList(new ArrayList<>(users.values()));
    }

    public List<Kwetter> findAllKwetters(){
        return Collections.unmodifiableList(new ArrayList<>(kwetters.values()));
    }

    public User findUserById(final UUID id){
        return users.get(id);
    }

    public Kwetter findKwetterById(final UUID id){
        return kwetters.get(id);
    }

    public List<User> findAllUsersByUserName(final String userName) {
        return users.values().stream()
                .filter(user ->
                        user.getUserName().toLowerCase()
                                .contains(userName.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Optional<User> findByUserName(final String userName){
        return users.values().stream().filter(
                user -> user.getUserName().equals(userName)
        ).findFirst();
    }

    public List<Kwetter> findAllKwettersByMessage(final String message){
        return kwetters.values().stream()
                .filter(kwetter ->
                        kwetter.getMessage().toLowerCase()
                        .contains(message.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Kwetter> findAllKwettersByAuthorId(final UUID authorId){
        return users.get(authorId).getKwetters();
    }

    public User login(final String email, final String password) {
        for (User user: users.values()) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) return user;
        }

        return null;
    }

    public boolean isEmailInUse(final String email){
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) return true;
        }

        return false;
    }
}
