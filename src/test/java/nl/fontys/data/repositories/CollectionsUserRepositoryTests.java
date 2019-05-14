package nl.fontys.data.repositories;

import nl.fontys.data.InMemoryCollectionsDatabase;
import nl.fontys.models.entities.Role;
import nl.fontys.models.entities.User;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CollectionsUserRepositoryTests {
    @Autowired
    private CollectionsUserRepository userRepository;

    @Autowired
    private CollectionsKwetterRepository kwetterRepository;

    private InMemoryCollectionsDatabase datasource;

    @Before
    public void setup(){
        InMemoryCollectionsDatabase.getInstance().reset();
        datasource = InMemoryCollectionsDatabase.getInstance();
        userRepository = new CollectionsUserRepository();
        kwetterRepository = new CollectionsKwetterRepository();
    }

    @Test
    public void findAll(){
        Assert.assertEquals(10,
                Lists.newArrayList(userRepository.findAll()).size());
    }

    @Test
    public void findAllByUserName_AllResult(){
        Assert.assertEquals(10, Lists.newArrayList(userRepository.findAllByUserNameContaining("un")).size());
    }

    @Test
    public void findAllByUserName_OneResult(){
        Assert.assertEquals(1, Lists.newArrayList(userRepository.findAllByUserNameContaining("un3")).size());
    }

    @Test
    public void findById_DoesntExist(){
        Assert.assertFalse(userRepository.findById(UUID.randomUUID()).isPresent());
    }

    @Test
    public void findById(){
        final List<User> users = Lists.newArrayList(userRepository.findAll());
        final User user = userRepository.findById(users.get(0).getId()).get();
        Assert.assertEquals(users.get(0), user);
    }

    @Test
    public void findByUserName_FindsUser(){
        final List<User> users = Lists.newArrayList(userRepository.findAll());
        final User lookedUpUser = userRepository.findByUserName(users.get(0).getUserName()).get();
        Assert.assertEquals(users.get(0), lookedUpUser);
    }

    @Test
    public void save(){
        final User user = userRepository.save(createTestUser());
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());
        Assert.assertEquals(11, Lists.newArrayList(userRepository.findAll()).size());
        Assert.assertTrue(userRepository.findById(user.getId()).isPresent());
    }

    @Test
    public void delete(){
        final User user = getFirstExistingUser();
        userRepository.deleteById(user.getId());

        Assert.assertFalse(userRepository.findById(user.getId()).isPresent());
        Assert.assertEquals(9, Lists.newArrayList(userRepository.findAll()).size());
        Assert.assertEquals(9, Lists.newArrayList(kwetterRepository.findAll()).size());
    }

    @Test
    public void login_WrongPassword(){
        final User firstUser = getFirstExistingUser();
        final User loggedInUser = userRepository.findByEmailAndPassword(firstUser.getEmail(), "wrongPassword");
        Assert.assertNull(loggedInUser);
    }

    @Test
    public void login(){
        final User firstUser = getFirstExistingUser();
        final User loggedInUser = userRepository.findByEmailAndPassword(firstUser.getEmail(), firstUser.getPassword());
        Assert.assertEquals(firstUser, loggedInUser);
    }

    @Test
    public void follow(){
        List<User> users = Lists.newArrayList(userRepository.findAll());
        final User follower1 = users.get(0);
        final User follower2 = users.get(1);
        final User followed = users.get(2);

        follower1.follow(followed);
        userRepository.save(follower1);
        follower2.follow(followed);
        userRepository.save(follower2);

        Assert.assertEquals(1, userRepository.findById(follower1.getId()).get().getFollowing().size());
        Assert.assertEquals(1, userRepository.findById(follower2.getId()).get().getFollowing().size());
        Assert.assertEquals(2, userRepository.findById(followed.getId()).get().getFollowers().size());
    }

    @Test
    public void unFollow(){
        final List<User> users = Lists.newArrayList(userRepository.findAll());
        final User follower1 = users.get(0);
        final User follower2 = users.get(1);
        final User followed = users.get(2);

        follower1.follow(followed);
        userRepository.save(follower1);
        follower2.follow(followed);
        userRepository.save(follower2);

        follower1.unFollow(followed);
        userRepository.save(follower1);

        Assert.assertEquals(0, userRepository.findById(follower1.getId()).get().getFollowing().size());
        Assert.assertEquals(1, userRepository.findById(follower2.getId()).get().getFollowing().size());
        Assert.assertEquals(1, userRepository.findById(followed.getId()).get().getFollowers().size());
    }

    private User getFirstExistingUser(){
        return userRepository.findAll().iterator().next();
    }

    private User createTestUser() {
        return new User("newPassWord", "newemail@mail.com", "newFN", "newLn",
                "newUN", Calendar.getInstance().getTime(), "newBio", "newLoc", null, Role.ROLE_USER);
    }
}
