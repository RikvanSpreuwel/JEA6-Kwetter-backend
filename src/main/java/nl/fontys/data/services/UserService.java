package nl.fontys.data.services;

import com.google.common.collect.Lists;
import nl.fontys.utils.EmailValidator;
import nl.fontys.utils.exceptions.UserNotFoundException;
import nl.fontys.data.repositories.IUserRepository;
import nl.fontys.data.repositories.JPAUserRepository;
import nl.fontys.data.services.interfaces.IUserService;
import nl.fontys.models.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements IUserService {
    private IUserRepository userRepository;

    @Autowired
    public UserService(JPAUserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return Lists.newArrayList(userRepository.findAll());
    }

    @Override
    public List<User> findAllByUserName(final String userName) {
        if (userName == null) throw new IllegalArgumentException("The given username cannot be null.");
        if (userName.isEmpty()) throw new IllegalArgumentException("The given username cannot be an empty string.");

        return userRepository.findAllByUserNameContaining(userName);
    }

    @Override
    public User findById(final UUID id) {
        if (id == null) throw new IllegalArgumentException("The given id cannot be null.");
        if (!userRepository.findById(id).isPresent())
            throw new UserNotFoundException(id);

        return userRepository.findById(id).get();
    }

    @Override
    public User save(final User user) {
        if (user == null) throw new IllegalArgumentException("The given user cannot be null.");
        if (user.getId() != null) throw new IllegalArgumentException("A new user cannot have an UUID yet.");
        if (userRepository.existsByEmail(user.getEmail())) throw new IllegalArgumentException("This email is already in use.");

        return userRepository.save(user);
    }

    @Override
    public User update(final User user) {
        if (user == null) throw new IllegalArgumentException("The given user cannot be null.");
        if (user.getId() == null)
            throw new IllegalArgumentException("The given user should have an id, otherwise it cannot be updated.");
        if (!userRepository.findById(user.getId()).isPresent()) throw new UserNotFoundException(user.getId());

        if (!user.getEmail().equals(
                userRepository.findById(user.getId()).get().getEmail()))
            throw new IllegalArgumentException("A users email cannot be updated.");

        return userRepository.save(user);
    }

    @Override
    public boolean delete(final UUID id) {
        if (id == null) throw new IllegalArgumentException("The given userId cannot be null.");
        if (!userRepository.findById(id).isPresent())
            throw new UserNotFoundException(id);

        removeFollowReferences(userRepository.findById(id).get());

        userRepository.deleteById(id);
        return !userRepository.findById(id).isPresent();
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

    @Override
    public User login(String email, String password) {
        if (email == null) throw new IllegalArgumentException("The email parameter cannot be null.");;
        if (!EmailValidator.isValidEmail(email)) throw new IllegalArgumentException("The given email is not in correct email format.");
        if (password == null) throw new IllegalArgumentException("The password parameter cannot be null.");
        if (password.length() < 8) throw new IllegalArgumentException("The password must be at least 8 characters.");
        if (!userRepository.existsByEmail(email)) throw new IllegalArgumentException("The given email is not being used by an existing user.");

        final User loggedInUser = userRepository.findByEmailAndPassword(email, password);
        if (loggedInUser == null) throw new IllegalArgumentException("The combination of email and password is incorrect.");

        return loggedInUser;
    }

    @Override
    public boolean follow(UUID userIdThatFollows, UUID userIdToFollow) {
        if (userIdThatFollows == null || userIdToFollow == null) throw new IllegalArgumentException("The user id's cannot be null.");
        if (userIdThatFollows.equals(userIdToFollow)) throw new IllegalArgumentException("A user cannot follow itself.");
        if (!userRepository.findById(userIdThatFollows).isPresent())
            throw new UserNotFoundException(userIdThatFollows);
        if (!userRepository.findById(userIdToFollow).isPresent())
            throw new UserNotFoundException(userIdToFollow);

        final User userThatFollows = userRepository.findById(userIdThatFollows).get();
        final User userToFollow = userRepository.findById(userIdToFollow).get();

        userThatFollows.follow(userToFollow);
        userRepository.save(userThatFollows);
        return true;
    }

    @Override
    public boolean unFollow(UUID userIdThatFollows, UUID userIdToUnFollow) {
        if (userIdThatFollows == null || userIdToUnFollow == null) throw new IllegalArgumentException("The user id's cannot be null.");
        if (userIdThatFollows.equals(userIdToUnFollow)) throw new IllegalArgumentException("A user cannot un follow itself.");
        if (!userRepository.findById(userIdThatFollows).isPresent())
            throw new UserNotFoundException(userIdThatFollows);
        if (!userRepository.findById(userIdToUnFollow).isPresent())
            throw new UserNotFoundException(userIdToUnFollow);

        final User userThatsFollowing = userRepository.findById(userIdThatFollows).get();
        final User userBeingFollowed = userRepository.findById(userIdToUnFollow).get();

        userThatsFollowing.unFollow(userBeingFollowed);
        userRepository.save(userThatsFollowing);
        return true;
    }
}
