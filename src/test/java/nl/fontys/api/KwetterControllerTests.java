package nl.fontys.api;

import nl.fontys.utils.exceptions.KwetterNotFoundException;
import nl.fontys.api.controllers.KwetterController;
import nl.fontys.data.services.KwetterService;
import nl.fontys.models.entities.Kwetter;
import org.junit.Test;
import org.junit.runner.RunWith;
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
@WebMvcTest(value = KwetterController.class, secure = false)
public class KwetterControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private KwetterService kwetterService;

    @Test
    public void get_All_ReturnsArray() throws Exception {
        final Kwetter kwetter = new Kwetter("test", Calendar.getInstance().getTime(), null);
        final List<Kwetter> kwetters = new ArrayList<Kwetter>(){{add(kwetter);}};

        given(kwetterService.findAll()).willReturn(kwetters);

        mvc.perform(get("/kwetters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].message", is(kwetter.getMessage())));
    }

    @Test
    public void get_All_EmptyList_ReturnsEmptyArray() throws Exception {
        given(kwetterService.findAll()).willReturn(new ArrayList<>());

        mvc.perform(get("/kwetters"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void get_All_ByMessage_ReturnsArray() throws Exception {
        final Kwetter kwetter = new Kwetter("test", Calendar.getInstance().getTime(), null);
        final List<Kwetter> kwetters = new ArrayList<Kwetter>(){{add(kwetter);}};

        given(kwetterService.findAllByMessage("test")).willReturn(kwetters);

        mvc.perform(get("/kwetters/searchbymessage?message=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].message", is(kwetter.getMessage())));
    }

    @Test
    public void get_All_ByMessage_NoResults_ReturnsEmptyArray() throws Exception {
        given(kwetterService.findAllByMessage("test")).willReturn(new ArrayList<>());

        mvc.perform(get("/kwetters/searchbymessage?message=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void get_All_ByAuthorId_ReturnsArray() throws Exception {
        final Kwetter kwetter = new Kwetter("test", Calendar.getInstance().getTime(), null);
        final List<Kwetter> kwetters = new ArrayList<Kwetter>(){{add(kwetter);}};
        final UUID authorId = UUID.randomUUID();

        given(kwetterService.findAllByAuthorId(authorId)).willReturn(kwetters);

        mvc.perform(get("/kwetters/searchbyauthorid?authorId=" + authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].message", is(kwetter.getMessage())));
    }

    @Test
    public void get_All_ByAuthorId_NoResults_ReturnsEmptyArray() throws Exception {
        final UUID authorId = UUID.randomUUID();

        given(kwetterService.findAllByAuthorId(authorId)).willReturn(new ArrayList<>());

        mvc.perform(get("/kwetters/searchbyauthorid?authorId=" + authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void get_ById_ReturnsKwetter() throws Exception {
        final Kwetter kwetter = new Kwetter("test", Calendar.getInstance().getTime(), null);
        kwetter.setId(UUID.randomUUID());

        given(kwetterService.findById(kwetter.getId())).willReturn(kwetter);

        mvc.perform(get("/kwetters/" + kwetter.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(kwetter.getId().toString())));
    }

    @Test
    public void post_ReturnsCreatedKwetter() throws Exception {
        final Kwetter kwetter = new Kwetter("message", Calendar.getInstance().getTime(), null);
        kwetter.setId(UUID.randomUUID());
        UUID fakeUserID = UUID.randomUUID();

        given(kwetterService.save(fakeUserID, "message")).willReturn(kwetter);

        mvc.perform(post("/kwetters/" + fakeUserID)
                .contentType(MediaType.APPLICATION_JSON)
                .content("message"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(kwetter.getId().toString())))
                .andExpect(jsonPath("$.message", is("message")));
    }

    /**
     * A test to check that spring automatically catches null parameters
     * @throws Exception
     */
    @Test
    public void get_ById_NullIdParameter_AutomaticallyReturnsBadRequest() throws Exception {
        mvc.perform(get("/kwetters/" + null))
                .andExpect(status().isBadRequest());
    }

    /**
     * A test to check that custom exceptions are caught by the advice classes
     * @throws Exception
     */
    @Test
    public void get_ById_NonExistingIdParameter_ThrowsNotFoundException_ReturnsNotFoundMessage() throws Exception{
        UUID testUUID = UUID.randomUUID();
        given(kwetterService.findById(testUUID))
                .willThrow(new KwetterNotFoundException(testUUID));

        mvc.perform(get("/kwetters/" + testUUID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", is("Could not find a kwetter with id: " + testUUID)));
    }
}
