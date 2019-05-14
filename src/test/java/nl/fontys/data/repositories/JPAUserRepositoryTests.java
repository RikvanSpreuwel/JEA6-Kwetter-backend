package nl.fontys.data.repositories;

import nl.fontys.models.entities.Role;
import nl.fontys.models.entities.User;
import nl.fontys.utils.DatabaseInserter;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
public class JPAUserRepositoryTests {
    @Autowired
    private JPAUserRepository userRepository;

    @Autowired
    private JPAKwetterRepository kwetterRepository;

    @Before
    public void setup() {
        DatabaseInserter.insertMockData(kwetterRepository, userRepository);
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
    public void update(){
        final User userToUpdate = getFirstExistingUser();
        userToUpdate.setId(userToUpdate.getId());
        userToUpdate.setBio("A new bio set as a change to test.");

        final User returnValue = userRepository.save(userToUpdate);
        Assert.assertEquals(userToUpdate, returnValue);

        final User datasourceValue = userRepository.findById(userToUpdate.getId()).get();
        Assert.assertEquals(returnValue, datasourceValue);
        Assert.assertEquals("A new bio set as a change to test.", datasourceValue.getBio());
    }

    @Test
    public void delete(){
        final User user = getFirstExistingUser();

        Iterable<User> userssss = userRepository.findAll();

        userRepository.deleteById(user.getId());

        Iterable<User> users = userRepository.findAll();
        Assert.assertFalse(userRepository.findById(user.getId()).isPresent());
        Assert.assertEquals(9, Lists.newArrayList(userRepository.findAll()).size());
        Assert.assertEquals(9, Lists.newArrayList(kwetterRepository.findAll()).size());
    }

    @Test
    public void delete_UserFollowingSomeone_RemovesFollowerFromFollowedUser(){
        final List<User> users = Lists.newArrayList(userRepository.findAll());
        final User follower1 = users.get(0);
        final User followed = users.get(1);
        final User follower2 = users.get(2);

        follower1.follow(followed);
        userRepository.save(follower1);
        follower2.follow(follower1);
        userRepository.save(follower2);

        removeFollowReferences(follower1);

        userRepository.deleteById(follower1.getId());
        Assert.assertFalse(userRepository.findById(follower1.getId()).isPresent());
        Assert.assertEquals(0, userRepository.findById(followed.getId()).get().getFollowers().size());
        Assert.assertEquals(0, userRepository.findById(follower2.getId()).get().getFollowing().size());
    }

    private void removeFollowReferences(final User userToBeDeleted){
        unFollowUsers(userToBeDeleted.getFollowing(), userToBeDeleted);
        removeFollowers(userToBeDeleted.getFollowers(), userToBeDeleted);
    }

    private void unFollowUsers(final List<User> following, final User follower) {
        final User followerNewReference = userRepository.findById(follower.getId()).get();
        following.forEach(followedUser -> {
            final User copyFollowedUser = userRepository.findById(followedUser.getId()).get();
            followerNewReference.unFollow(copyFollowedUser);
        });

        userRepository.save(followerNewReference);
    }

    private void removeFollowers(final List<User> followers, final User following){
        final User followingNewReference = userRepository.findById(following.getId()).get();
        followers.forEach(follower -> {
            final User copyFollower = userRepository.findById(follower.getId()).get();
            copyFollower.unFollow(followingNewReference);
            userRepository.save(copyFollower);
        });
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

        final User follower1NewReference = userRepository.findById(follower1.getId()).get();
        final User followedNewReference = userRepository.findById(followed.getId()).get();
        follower1NewReference.unFollow(followedNewReference);
        userRepository.save(follower1NewReference);

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
