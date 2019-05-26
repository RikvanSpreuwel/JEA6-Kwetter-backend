package nl.fontys.data.services;

import nl.fontys.utils.exceptions.KwetterNotFoundException;
import nl.fontys.utils.exceptions.UserNotFoundException;
import nl.fontys.data.repositories.JPAKwetterRepository;
import nl.fontys.data.repositories.JPAUserRepository;
import nl.fontys.data.services.interfaces.IKwetterService;
import nl.fontys.models.entities.Kwetter;
import nl.fontys.models.entities.User;
import nl.fontys.utils.DatabaseInserter;
import nl.fontys.utils.TestConstants;
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

import java.util.List;
import java.util.UUID;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
public class KwetterServiceTests {
    @Autowired
    private JPAKwetterRepository kwetterRepository;

    @Autowired
    private JPAUserRepository userRepository;

    @Autowired
    private IKwetterService kwetterService;

    @Before
    public void setup(){
        DatabaseInserter.insertMockData(kwetterRepository, userRepository);
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void findAll(){
        final List<Kwetter> kwetters = kwetterService.findAll();
        Assert.assertEquals(kwetterRepository.findAll(), kwetters);
    }

    @Test
    public void findAllByMessage_MessageNull(){
        expectIllegalArgumentException("The given message cannot be null.");
        kwetterService.findAllByMessage(null);
    }

    @Test
    public void findAllByMessage_MessageEmptyString(){
        expectIllegalArgumentException("The given message cannot be an empty string.");
        kwetterService.findAllByMessage("");
    }

    @Test
    public void findAllByMessage_FindsAll10Kwetters(){
        final List<Kwetter> kwetters = kwetterService.findAllByMessage("post");
        Assert.assertEquals(10, kwetters.size());
        Assert.assertEquals(kwetterRepository.findAllByMessageContaining("post"), kwetters);
    }

    @Test
    public void findAllByMessage_Finds1Match(){
        final List<Kwetter> kwetters = kwetterService.findAllByMessage("5post");
        Assert.assertEquals(1, kwetters.size());
        Assert.assertEquals(kwetterRepository.findAllByMessageContaining("5post"), kwetters);
    }

    @Test
    public void findAllByAuthorId_AuthorIdNull(){
        expectIllegalArgumentException("AuthorId is required.");
        kwetterService.findAllByAuthorId(null);
    }

    @Test
    public void findAllByAuthorId_NotExistingId(){
        expectIllegalArgumentException("The user corresponding to the given id does not exist.");
        kwetterService.findAllByAuthorId(UUID.randomUUID());
    }

    @Test
    public void findAllByAuthorId_ReturnsTheAuthorsKwetters(){
        final User user = getFirstExistingUser();
        kwetterService.save(user.getId(), "newKwetter message.");

        Assert.assertEquals(2, kwetterService.findAllByAuthorId(user.getId()).size());
    }

    @Test
    public void findById_IdNull(){
        expectIllegalArgumentException("The given id cannot be null.");
        kwetterService.findById(null);
    }

    @Test
    public void findById_NonExistingId(){
        exceptionRule.expect(KwetterNotFoundException.class);
        kwetterService.findById(UUID.randomUUID());
    }

    @Test
    public void findById(){
        final List<Kwetter> kwetters = Lists.newArrayList(kwetterRepository.findAll());
        final Kwetter kwetter = kwetterService.findById(kwetters.get(0).getId());
        Assert.assertEquals(kwetters.get(0), kwetter);
    }

    @Test
    public void save_PosterIdNull(){
        expectIllegalArgumentException("The given posterId cannot be null.");
        kwetterService.save(null, "testmessage");
    }

    @Test
    public void save_PosterIdDoesntExist(){
        exceptionRule.expect(UserNotFoundException.class);
        kwetterService.save(UUID.randomUUID(), "testmessage");
    }

    @Test
    public void save_MessageNull(){
        expectIllegalArgumentException("The given message cannot be null.");
        kwetterService.save(getFirstExistingUser().getId(), null);
    }

    @Test
    public void save_MessageEmptyString(){
        expectIllegalArgumentException("The message has a minimum of 1 character and a maximum of 140 characters.");
        kwetterService.save(getFirstExistingUser().getId(), "");
    }

    @Test
    public void save_MessageExceedingCharacterLimit(){
        expectIllegalArgumentException("The message has a minimum of 1 character and a maximum of 140 characters.");
        kwetterService.save(getFirstExistingUser().getId(), TestConstants.kwetterMessage141Characters);
    }

    @Test
    public void save(){
        final Kwetter kwetter = kwetterService.save(getFirstExistingUser().getId(), "Test kwetter");
        Assert.assertEquals(kwetter, kwetterRepository.findById(kwetter.getId()).get());
        Assert.assertEquals(kwetter.getAuthor(), getFirstExistingUser());
        Assert.assertTrue(userRepository.findById(kwetter.getAuthor().getId()).get().getKwetters().contains(kwetter));
    }

    @Test
    public void delete_KwetterIdNull(){
        expectIllegalArgumentException("The given kwetterId cannot be null.");
        kwetterService.delete(null);
    }

    @Test
    public void delete_KwetterIdDoesntExist(){
        exceptionRule.expect(KwetterNotFoundException.class);
        kwetterService.delete(UUID.randomUUID());
    }

    @Test
    public void delete(){
        final User user = getFirstExistingUser();
        final Kwetter deletedKwetter = user.getKwetters().get(0);

        Assert.assertTrue(kwetterService.delete(deletedKwetter.getId()));
        Assert.assertFalse(kwetterRepository.findById(deletedKwetter.getId()).isPresent());
        Assert.assertTrue(userRepository.findById(user.getId()).get().getKwetters().isEmpty());
    }

    private void expectIllegalArgumentException(String message){
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage(message);
    }

    private User getFirstExistingUser(){
        return userRepository.findAll().iterator().next();
    }
}
