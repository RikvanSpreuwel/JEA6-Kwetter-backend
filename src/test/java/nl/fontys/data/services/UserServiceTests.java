package nl.fontys.data.services;

import nl.fontys.Utils.Exceptions.UserNotFoundException;
import nl.fontys.data.repositories.JPAKwetterRepository;
import nl.fontys.data.repositories.JPAUserRepository;
import nl.fontys.data.services.interfaces.IUserService;
import nl.fontys.models.Role;
import nl.fontys.models.User;
import nl.fontys.utils.DatabaseInserter;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
public class UserServiceTests {
    @Autowired
    private JPAKwetterRepository kwetterRepository;

    @Autowired
    private JPAUserRepository userRepository;

    @Autowired
    private IUserService userService;

    @Before
    public void setup(){
        DatabaseInserter.insertMockData(kwetterRepository, userRepository);
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void findAll(){
        final List<User> users = userService.findAll();
        Assert.assertEquals(userRepository.findAll(), users);
    }

    @Test
    public void findAllByUserName_Null(){
        expectIllegalArgumentException("The given username cannot be null.");
        userService.findAllByUserName(null);
    }

    @Test
    public void findAllByUserName_EmptyString(){
        expectIllegalArgumentException("The given username cannot be an empty string.");
        userService.findAllByUserName("");
    }

    @Test
    public void findAllByUserName_AllResult(){
        final List<User> users = userService.findAllByUserName("un");
        Assert.assertEquals(10, users.size());
        Assert.assertEquals(users, userRepository.findAllByUserNameContaining("un"));
    }

    @Test
    public void findAllByUserName_OneResult(){
        final List<User> users = userService.findAllByUserName("un3");
        Assert.assertEquals(1, users.size());
        Assert.assertEquals(users, userRepository.findAllByUserNameContaining("un3"));
    }

    @Test
    public void findById_IdNull(){
        expectIllegalArgumentException("The given id cannot be null.");
        userService.findById(null);
    }

    @Test
    public void findById_IdDoesntExist(){
        exceptionRule.expect(UserNotFoundException.class);
        userService.findById(UUID.randomUUID());
    }

    @Test
    public void findById(){
        final User user = userService.findById(getFirstExistingUser().getId());
        Assert.assertEquals(userRepository.findById(user.getId()).get(), user);
    }

    @Test
    public void save_UserNull(){
        expectIllegalArgumentException("The given user cannot be null.");
        userService.save(null);
    }

    @Test
    public void save_UserAlreadyHasId(){
        expectIllegalArgumentException("A new user cannot have an UUID yet.");
        userService.save(getFirstExistingUser());
    }

    @Test
    public void save_EmailAlreadyInUse(){
        expectIllegalArgumentException("This email is already in use.");

        final User alreadyExistingEmailUser = getFirstExistingUser();
        alreadyExistingEmailUser.setId(null);

        userService.save(alreadyExistingEmailUser);
    }

    @Test
    public void save(){
        final User user = userService.save(createTestUser());
        Assert.assertNotNull(user);
        Assert.assertNotNull(user.getId());
        Assert.assertEquals(11, Lists.newArrayList(userRepository.findAll()).size());
        Assert.assertTrue(userRepository.findById(user.getId()).isPresent());
    }

    @Test
    public void update_UserNull(){
        expectIllegalArgumentException("The given user cannot be null.");
        userService.update(null);
    }

    @Test
    public void update_UserNoId(){
        expectIllegalArgumentException("The given user should have an id, otherwise it cannot be updated.");

        final User user = createTestUser();

        userService.update(user);
    }

    @Test
    public void update_UserDoesntExist(){
        exceptionRule.expect(UserNotFoundException.class);

        final User user = createTestUser();
        user.setId(UUID.randomUUID());

        userService.update(user);
    }

    @Test
    public void update_EmailCannotBeChanged(){
        expectIllegalArgumentException("A users email cannot be updated.");

        final User user = getFirstExistingUser();
        user.setEmail("nieuweEmail@magniet.nl");

        userService.update(user);
    }

    @Test
    public void update(){
        final User userToUpdate = getFirstExistingUser();
        final User updatedCopy = new User(userToUpdate.getPassword(), userToUpdate.getEmail(), userToUpdate.getFirstName(), userToUpdate.getLastName(), userToUpdate.getUserName(), userToUpdate.getDateOfBirth(), userToUpdate.getBio(), userToUpdate.getLocation(), userToUpdate.getProfilePicture(), userToUpdate.getRole());
        updatedCopy.setId(userToUpdate.getId());
        updatedCopy.setBio("A new bio set as a change to test.");

        final User returnValue = userService.update(updatedCopy);
        Assert.assertEquals(updatedCopy, returnValue);

        final User datasourceValue = userRepository.findById(userToUpdate.getId()).get();
        Assert.assertEquals(returnValue, datasourceValue);
        Assert.assertEquals("A new bio set as a change to test.", datasourceValue.getBio());
    }

    @Test
    public void delete_UserIdNull(){
        expectIllegalArgumentException("The given userId cannot be null.");
        userService.delete(null);
    }

    @Test
    public void delete_UserIdDoesntExist(){
        exceptionRule.expect(UserNotFoundException.class);
        userService.delete(UUID.randomUUID());
    }

    @Test
    public void delete(){
        final User user = getFirstExistingUser();
        userService.delete(user.getId());

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

        userService.follow(follower1.getId(), followed.getId());
        userService.follow(follower2.getId(), follower1.getId());

        userService.delete(follower1.getId());
        Assert.assertFalse(userRepository.findById(follower1.getId()).isPresent());
        Assert.assertEquals(0, userRepository.findById(followed.getId()).get().getFollowers().size());
        Assert.assertEquals(0, userRepository.findById(follower2.getId()).get().getFollowing().size());
    }

    @Test
    public void login_EmailNull(){
        expectIllegalArgumentException("The email parameter cannot be null.");
        userService.login(null, "password");
    }

    @Test
    public void login_EmailNotEmailFormat1(){
        expectIllegalArgumentException("The given email is not in correct email format.");
        userService.login("invalid.email.format.nl", "password");
    }

    @Test
    public void login_EmailNotEmailFormat2(){
        expectIllegalArgumentException("The given email is not in correct email format.");
        userService.login("invalid.email@formatnl", "password");
    }

    @Test
    public void login_NoUserWithGivenEmail(){
        expectIllegalArgumentException("The given email is not being used by an existing user.");
        userService.login("valid.email@format.nl", "password");
    }

    @Test
    public void login_PasswordNull(){
        expectIllegalArgumentException("The password parameter cannot be null.");
        userService.login("email.email@test.nl", null);
    }

    @Test
    public void login_PasswordLessThan8Characters(){
        expectIllegalArgumentException("The password must be at least 8 characters.");
        userService.login("email.email@test.nl", "passwor");
    }

    @Test
    public void login_WrongPassword(){
        expectIllegalArgumentException("The combination of email and password is incorrect.");
        final User firstUser = getFirstExistingUser();
        final User loggedInUser = userService.login(firstUser.getEmail(), "wrongPassword");
    }

    @Test
    public void login(){
        final User firstUser = getFirstExistingUser();
        final User loggedInUser = userService.login(firstUser.getEmail(), firstUser.getPassword());
        Assert.assertEquals(firstUser, loggedInUser);
    }

    @Test
    public void follow_UserThatFollowsIdNull1(){
        expectIllegalArgumentException("The user id's cannot be null.");
        userService.follow(null, UUID.randomUUID());
    }

    @Test
    public void follow_UserToBeFollowedIdNull1(){
        expectIllegalArgumentException("The user id's cannot be null.");
        userService.follow(UUID.randomUUID(), null);
    }

    @Test
    public void follow_UserThatFollowsIdDoesntExist(){
        exceptionRule.expect(UserNotFoundException.class);
        userService.follow(UUID.randomUUID(), getFirstExistingUser().getId());
    }

    @Test
    public void follow_UserToBeFollowedIdDoesntExist(){
        exceptionRule.expect(UserNotFoundException.class);
        userService.follow(getFirstExistingUser().getId(), UUID.randomUUID());
    }

    @Test
    public void follow_TryingToFollowMyself(){
        expectIllegalArgumentException("A user cannot follow itself.");
        userService.follow(getFirstExistingUser().getId(), getFirstExistingUser().getId());
    }

    @Test
    public void follow_AlreadyFollowingThatUser(){
        expectIllegalArgumentException("The given user is already being followed by this user.");
        List<User> users = Lists.newArrayList(userRepository.findAll());
        final User follower = users.get(0);
        final User followed = users.get(1);

        userService.follow(follower.getId(), followed.getId());
        userService.follow(follower.getId(), followed.getId());
    }

    @Test
    public void follow(){
        List<User> users = Lists.newArrayList(userRepository.findAll());
        final User follower1 = users.get(0);
        final User follower2 = users.get(1);
        final User followed = users.get(2);

        userService.follow(follower1.getId(), followed.getId());
        userService.follow(follower2.getId(), followed.getId());

        Assert.assertEquals(1, userRepository.findById(follower1.getId()).get().getFollowing().size());
        Assert.assertEquals(1, userRepository.findById(follower2.getId()).get().getFollowing().size());
        Assert.assertEquals(2, userRepository.findById(followed.getId()).get().getFollowers().size());
    }

    @Test
    public void unFollow_UserThatStopsFollowingIdNull1(){
        expectIllegalArgumentException("The user id's cannot be null.");
        userService.unFollow(null, UUID.randomUUID());
    }

    @Test
    public void unFollow_UserToStopFollowingIdNull1(){
        expectIllegalArgumentException("The user id's cannot be null.");
        userService.unFollow(UUID.randomUUID(), null);
    }

    @Test
    public void unFollow_UserThatStopsFollwoingIdDoesntExist(){
        exceptionRule.expect(UserNotFoundException.class);
        userService.unFollow(UUID.randomUUID(), getFirstExistingUser().getId());
    }

    @Test
    public void unFollow_UserToStopFollowingIdDoesntExist(){
        exceptionRule.expect(UserNotFoundException.class);
        userService.unFollow(getFirstExistingUser().getId(), UUID.randomUUID());
    }

    @Test
    public void unFollow_TryingToUnFollowMyself(){
        expectIllegalArgumentException("A user cannot un follow itself.");
        userService.unFollow(getFirstExistingUser().getId(), getFirstExistingUser().getId());
    }

    @Test
    public void unFollow_NotFollowingUserToStopFollowing(){
        expectIllegalArgumentException("The given user is not being followed by this user.");
        final List<User> users = Lists.newArrayList(userRepository.findAll());
        final User follower = users.get(0);
        final User followed = users.get(1);
        userService.unFollow(follower.getId(), followed.getId());
    }

    @Test
    public void unFollow(){
        final List<User> users = Lists.newArrayList(userRepository.findAll());
        final User follower1 = users.get(0);
        final User follower2 = users.get(1);
        final User followed = users.get(2);

        userService.follow(follower1.getId(), followed.getId());
        userService.follow(follower2.getId(), followed.getId());
        userService.unFollow(follower1.getId(), followed.getId());

        Assert.assertEquals(0, userRepository.findById(follower1.getId()).get().getFollowing().size());
        Assert.assertEquals(1, userRepository.findById(follower2.getId()).get().getFollowing().size());
        Assert.assertEquals(1, userRepository.findById(followed.getId()).get().getFollowers().size());
    }

    private void expectIllegalArgumentException(String message){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage(message);
    }

    private User getFirstExistingUser(){
        return userRepository.findAll().iterator().next();
    }

    private User createTestUser() {
        return new User("newPassWord", "newemail@mail.com", "newFN", "newLn",
                "newUN", Calendar.getInstance().getTime(), "newBio", "newLoc", null, Role.ROLE_USER);
    }
}