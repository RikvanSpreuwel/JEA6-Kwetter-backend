package nl.fontys.data.repositories;

import nl.fontys.data.services.interfaces.IMailService;
import nl.fontys.models.entities.Kwetter;
import nl.fontys.models.entities.User;
import nl.fontys.utils.DatabaseInserter;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest
public class JPAKwetterRepositoryTests {
    @MockBean
    private IMailService mailService;

    @Autowired
    private JPAKwetterRepository kwetterRepository;

    @Autowired
    private JPAUserRepository userRepository;

    @Before
    public void setup(){
        DatabaseInserter.insertMockData(kwetterRepository, userRepository);
    }

    @Test
    public void findAll(){
        Assert.assertEquals(10,
                Lists.newArrayList(kwetterRepository.findAll()).size());
    }

    @Test
    public void findAllByMessage_FindsAll10Kwetters(){
        Assert.assertEquals(10,
                Lists.newArrayList(kwetterRepository.findAllByMessageContaining("post")).size());
    }

    @Test
    public void findAllByMessage_Finds1Match(){
        Assert.assertEquals(1,
                Lists.newArrayList(kwetterRepository.findAllByMessageContaining("5post")).size());
    }

    @Test
    public void findAllByAuthorId_ReturnsTheAuthorsKwetters(){
        final User user = getFirstExistingUser();
        final Kwetter kwetter = user.postKwetter("newKwetter message.");
        kwetterRepository.save(kwetter);

        Assert.assertEquals(2, Lists.newArrayList(kwetterRepository.findAllByAuthor_Id(user.getId())).size());
    }

    @Test
    public void findById_DoesntExist(){
        Assert.assertFalse(kwetterRepository.findById(UUID.randomUUID()).isPresent());
    }

    @Test
    public void findById(){
        final List<Kwetter> kwetters = Lists.newArrayList(kwetterRepository.findAll());
        final Kwetter kwetter = kwetterRepository.findById(kwetters.get(0).getId()).get();
        Assert.assertEquals(kwetters.get(0), kwetter);
    }

    @Test
    public void save(){
        final User author = userRepository.findAll().iterator().next();
        final Kwetter kwetter = author.postKwetter("Test kwetter");
        final Kwetter savedKwetter = kwetterRepository.save(kwetter);

        Assert.assertTrue(kwetterRepository.findById(savedKwetter.getId()).isPresent());
        Assert.assertEquals(kwetter.getAuthor(), userRepository.findById(author.getId()).get());
        Assert.assertTrue(userRepository.findById(author.getId()).get().getKwetters().contains(kwetter));
    }

    @Test
    public void delete(){
        final User user = getFirstExistingUser();
        final Kwetter deletedKwetter = user.getKwetters().get(0);

        kwetterRepository.deleteById(deletedKwetter.getId());
        Assert.assertFalse(kwetterRepository.findById(deletedKwetter.getId()).isPresent());
        Assert.assertTrue(userRepository.findById(user.getId()).get().getKwetters().isEmpty());
    }

    private User getFirstExistingUser(){
        return userRepository.findAll().iterator().next();
    }
}
