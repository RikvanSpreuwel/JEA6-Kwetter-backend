package nl.fontys.data.services;

import com.google.common.collect.Lists;
import nl.fontys.utils.exceptions.KwetterNotFoundException;
import nl.fontys.utils.exceptions.UserNotFoundException;
import nl.fontys.data.repositories.IKwetterRepository;
import nl.fontys.data.repositories.IUserRepository;
import nl.fontys.data.repositories.JPAKwetterRepository;
import nl.fontys.data.repositories.JPAUserRepository;
import nl.fontys.data.services.interfaces.IKwetterService;
import nl.fontys.models.entities.Kwetter;
import nl.fontys.models.entities.User;
import nl.fontys.websocket.MessageHandlingController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KwetterService implements IKwetterService {
    private IKwetterRepository kwetterRepository;

    private IUserRepository userRepository;
    private MessageHandlingController messageHandlingController;

    @Autowired
    public KwetterService(JPAKwetterRepository kwetterRepository, JPAUserRepository userRepository, MessageHandlingController messageHandlingController){
        this.kwetterRepository = kwetterRepository;
        this.userRepository = userRepository;
        this.messageHandlingController = messageHandlingController;
    }

    @Override
    public List<Kwetter> findAll() {
        return Lists.newArrayList(kwetterRepository.findAll());
    }

    @Override
    public List<Kwetter> findAllByMessage(String message) {
        if (message == null) throw new IllegalArgumentException("The given message cannot be null.");
        if (message.isEmpty()) throw new IllegalArgumentException("The given message cannot be an empty string.");

        return kwetterRepository.findAllByMessageContaining(message);
    }

    @Override
    public List<Kwetter> findAllByAuthorId(UUID authorId) {
        if (authorId == null) throw new IllegalArgumentException("AuthorId is required.");
        if (!userRepository.findById(authorId).isPresent())
            throw new IllegalArgumentException("The user corresponding to the given id does not exist.");

        return kwetterRepository.findAllByAuthor_Id(authorId);
    }

    @Override
    public Kwetter findById(final UUID id) {
        if (id == null) throw new IllegalArgumentException("The given id cannot be null.");
        if (!kwetterRepository.findById(id).isPresent())
            throw new KwetterNotFoundException(id);

        return kwetterRepository.findById(id).get();
    }

    @Override
    public List<Kwetter> getUserTimeline(UUID userId) {
        if (userId == null) throw new IllegalArgumentException("The given userId cannot be null");
        if (!userRepository.findById(userId).isPresent())
            throw new UserNotFoundException(userId);

        User userOfRequestedTimeline = userRepository.findById(userId).get();

        List<Kwetter> timeline = new ArrayList<>(kwetterRepository.findAllByAuthor_Id(userId));
        userOfRequestedTimeline.getFollowing().forEach(user -> timeline.addAll(kwetterRepository.findAllByAuthor_Id(user.getId())));
        timeline.sort(Comparator.comparing(Kwetter::getPostedOn).reversed());

        return timeline;
    }

    @Override
    public Kwetter save(final UUID posterId, final String message) {
        if (posterId == null) throw new IllegalArgumentException("The given posterId cannot be null.");
        if (!userRepository.findById(posterId).isPresent())
            throw new UserNotFoundException(posterId);

        final User poster = userRepository.findById(posterId).get();

        final Kwetter kwetter = poster.postKwetter(message);

        final Kwetter savedKwetter = kwetterRepository.save(kwetter);

        messageHandlingController.send(savedKwetter);

        return kwetterRepository.save(kwetter);
    }

    @Override
    public boolean delete(final UUID id) {
        if (id == null) throw new IllegalArgumentException("The given kwetterId cannot be null.");
        if (!kwetterRepository.findById(id).isPresent())
            throw new KwetterNotFoundException(id);

        kwetterRepository.deleteById(id);

        return !kwetterRepository.existsById(id);
    }
}
