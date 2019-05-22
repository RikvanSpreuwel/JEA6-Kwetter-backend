package nl.fontys.api;

import nl.fontys.models.resources.UserResource;
import nl.fontys.utils.exceptions.UserNotFoundException;
import nl.fontys.api.controllers.UserController;
import nl.fontys.data.services.UserService;
import nl.fontys.models.entities.Role;
import nl.fontys.models.entities.User;
import nl.fontys.utils.JsonSerializer;
import nl.fontys.utils.modelmapper.converters.ToKwetterResourceModelConverter;
import nl.fontys.utils.modelmapper.converters.ToUserResourceModelConverter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class, secure = false)
public class UserControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ModelMapper modelMapper;

    private static ModelMapper modelMapperForTesting;

    @BeforeClass
    public static void setup(){
        modelMapperForTesting = new ModelMapper();
        modelMapperForTesting.addConverter(new ToKwetterResourceModelConverter());
        modelMapperForTesting.addConverter(new ToUserResourceModelConverter());
    }

    @Test
    public void get_All_ReturnsArray() throws Exception {
        final User user = createTestUser();
        user.setId(UUID.randomUUID());

        final List<User> users = new ArrayList<User>(){{add(user);}};

        given(userService.findAll()).willReturn(users);
        given(modelMapper.map(users, new TypeToken<List<UserResource>>(){}.getType()))
                .willReturn(modelMapperForTesting.map(users, new TypeToken<List<UserResource>>(){}.getType()));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userResources", hasSize(1)))
                .andExpect(jsonPath("$._embedded.userResources[0].userId", is(user.getId().toString())));
    }

    @Test
    public void get_All_EmptyListOfUsers_ReturnsEmptyArray() throws Exception {
        given(userService.findAll()).willReturn(new ArrayList<>());
        given(modelMapper.map(new ArrayList<>(), new TypeToken<List<UserResource>>(){}.getType()))
                .willReturn(modelMapperForTesting.map(new ArrayList<>(), new TypeToken<List<UserResource>>(){}.getType()));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }

    @Test
    public void get_All_ByUserName_ReturnsArray() throws Exception {
        final User user = createTestUser();
        user.setId(UUID.randomUUID());
        final List<User> users = new ArrayList<User>(){{add(user);}};

        given(userService.findAllByUserName("test")).willReturn(users);
        given(modelMapper.map(users, new TypeToken<List<UserResource>>(){}.getType()))
                .willReturn(modelMapperForTesting.map(users, new TypeToken<List<UserResource>>(){}.getType()));

        mvc.perform(get("/users/search?userName=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userResources", hasSize(1)))
                .andExpect(jsonPath("$._embedded.userResources[0].userId", is(user.getId().toString())));

    }

    @Test
    public void get_All_ByUserName_NoResults_ReturnsEmptyArray() throws Exception{
        given(userService.findAll()).willReturn(new ArrayList<>());
        given(modelMapper.map(new ArrayList<>(), new TypeToken<List<UserResource>>(){}.getType()))
                .willReturn(modelMapperForTesting.map(new ArrayList<>(), new TypeToken<List<UserResource>>(){}.getType()));

        mvc.perform(get("/users/search?userName=test"))
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }

    @Test
    public void get_ById_ReturnsUser() throws Exception {
        final User user = createTestUser();
        user.setId(UUID.randomUUID());

        given(userService.findById(user.getId())).willReturn(user);
        given(modelMapper.map(user, UserResource.class))
                .willReturn(modelMapperForTesting.map(user, UserResource.class));

        mvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(user.getId().toString())));
    }

    @Test
    public void post_ValidUserJsonObject_ReturnsUserWithId() throws Exception {
        final User postUser = createTestUser();
        final User postUserCopyWithId = createTestUser();
        postUserCopyWithId.setId(UUID.randomUUID());

        given(userService.save(postUser)).willReturn(postUserCopyWithId);
        given(modelMapper.map(postUserCopyWithId, UserResource.class))
                .willReturn(modelMapperForTesting.map(postUserCopyWithId, UserResource.class));

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonSerializer.toJson(postUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(postUserCopyWithId.getId().toString())));
    }

    @Test
    public void put_ValidUserJsonObject_ReturnsUser() throws Exception {
        final User putUser = createTestUser();
        putUser.setId(UUID.randomUUID());

        given(userService.update(putUser)).willReturn(putUser);
        given(modelMapper.map(putUser, UserResource.class))
                .willReturn(modelMapperForTesting.map(putUser, UserResource.class));

        mvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonSerializer.toJson(putUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(putUser.getId().toString())));
    }

    @Test
    public void follow_ReturnsTrue() throws Exception {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        given(userService.follow(uuid1, uuid2)).willReturn(true);

        mvc.perform(put("/users/" + uuid1 + "/follow/" + uuid2))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void unfollow_ReturnsTrue() throws Exception {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        given(userService.unFollow(uuid1, uuid2)).willReturn(true);

        mvc.perform(delete("/users/" + uuid1 + "/unfollow/" + uuid2))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }


    /**
     * A test to check if the model validation is working.
     *
     * We will not test every used annotation/property, we may assume that the official javax
     * package is tested by itself
     * @throws Exception
     */
    @Test
    public void post_IncorrectUserInput_AutomaticallyReturnsBadRequest() throws Exception {
        final User postUser = createTestUser();
        postUser.setEmail("notvalid@email");

        mvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonSerializer.toJson(postUser)))
                .andExpect(status().isBadRequest());
    }

    /**
     * A test to check that spring automatically catches null parameters
     * @throws Exception
     */
    @Test
    public void get_ById_NullIdParameter_AutomaticallyReturnsBadRequest() throws Exception {
        mvc.perform(get("/users/" + null))
                .andExpect(status().isBadRequest());
    }

    /**
     * A test to check that custom exceptions are caught by the advice classes
     * @throws Exception
     */
    @Test
    public void get_ById_NonExistingId_ThrowsNotFoundException_ReturnsNotFoundMessage() throws Exception{
        UUID testUUID = UUID.randomUUID();
        given(userService.findById(testUUID))
                .willThrow(new UserNotFoundException(testUUID));

        mvc.perform(get("/users/" + testUUID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Could not find a user with id: " + testUUID)));
    }




    private User createTestUser(){
        return new User("newPassWord", "newmail@mail.com", "newFN",
                "newLn", "newUN", Calendar.getInstance().getTime(), "newBio",
                "newLoc", null, Role.ROLE_USER);
    }
}
