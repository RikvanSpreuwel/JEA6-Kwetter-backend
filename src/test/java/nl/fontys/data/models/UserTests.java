package nl.fontys.data.models;

import nl.fontys.models.entities.Kwetter;
import nl.fontys.models.entities.Role;
import nl.fontys.models.entities.User;
import nl.fontys.utils.TestConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTests {
    private User user1;
    private User user2;

    @Before
    public void setup(){
        createTestUsers();
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void follow_InputNull(){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("The user cannot be null.");
        user1.follow(null);
    }

    @Test
    public void follow_TryingToFollowMyself(){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("A user cannot follow itself.");
        user1.follow(user1);
    }

    @Test
    public void follow_AlreadyFollowingThatUser(){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("The given user is already being followed by this user.");
        user1.follow(user2);
        user1.follow(user2);
    }

    @Test
    public void follow(){
        user1.follow(user2);
        Assert.assertEquals(1, user1.getFollowing().size());
        Assert.assertEquals(1, user2.getFollowers().size());
    }

    @Test
    public void unFollow_InputNull(){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("The user cannot be null.");
        user1.unFollow(null);
    }

    @Test
    public void unFollow_TryingToUnfollowMyself(){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("A user cannot un follow itself.");
        user1.unFollow(user1);
    }

    @Test
    public void unFollow_TryingToUnfollowWhoYouAreNotFollowing(){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("The given user is not being followed by this user.");
        user1.unFollow(user2);
    }

    @Test
    public void unFollow(){
        user1.follow(user2);
        Assert.assertEquals(1, user1.getFollowing().size());
        Assert.assertEquals(1, user2.getFollowers().size());
        user1.unFollow(user2);
        Assert.assertEquals(0, user1.getFollowing().size());
        Assert.assertEquals(0, user2.getFollowers().size());
    }

    @Test
    public void postKwetter_MessageNull(){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("The given message cannot be null.");
        user1.postKwetter(null);
    }

    @Test
    public void postKwetter_MessageEmptyString(){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("The message has a minimum of 1 character and a maximum of 140 characters.");
        user1.postKwetter("");
    }

    @Test
    public void postKwetter_MessageExceedingCharacterLimit(){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("The message has a minimum of 1 character and a maximum of 140 characters.");
        user1.postKwetter(TestConstants.kwetterMessage141Characters);
    }

    @Test
    public void postKwetter(){
        final Kwetter kwetter = user1.postKwetter("Kwetter!");
        Assert.assertNotNull(kwetter);
        Assert.assertEquals("Kwetter!", kwetter.getMessage());
        Assert.assertEquals(1, user1.getKwetters().size());
        Assert.assertEquals(user1, user1.getKwetters().get(0).getAuthor());
    }

    private void createTestUsers(){
        user1 = new User("passwordTest1", "email@Test1.nl", "fnTest1", "lnTest1", "unTest1", Calendar.getInstance().getTime(), "bioTest1", "locTest1", null, Role.ROLE_USER);
        user1.setId(UUID.randomUUID());
        user2 = new User("passwordTest2", "email@Test2.nl", "fnTest2", "lnTest2", "unTest2", Calendar.getInstance().getTime(), "bioTest2", "locTest2", null, Role.ROLE_USER);
        user2.setId(UUID.randomUUID());
    }
}
