package nl.fontys.data.services.interfaces;

import nl.fontys.models.User;

import java.util.List;
import java.util.UUID;


public interface IUserService {
    /**
     * Fetches all users from the datasource
     *
     * @return a list of users
     */
    List<User> findAll();

    /**
     * Fetches all users of which the username contains the given username string
     *
     * @param userName (or part of the username) to search for
     * @return a list of users, or an empty list if no users are found
     */
    List<User> findAllByUserName(final String userName);

    /**
     * Fetches a user by it's UUID
     * Throws an IllegalArgumentException if the UUID doesn't correspond to an existing user
     *
     * @param id of the user to fetch
     * @return the requested user, or null if the user does not exist
     */
    User findById(final UUID id);

    /**
     * Saves a new user, throws an error if the user to be saved already has an UUID
     *                   and therefore already exists
     *
     * @param user to be saved
     * @return the saved user and adds it's generated UUID
     */
    User save(final User user);

    /**
     * Updates a user, throws an error if the given user does not already exist
     *
     * @param user to be updated
     * @return the updated user
     */
    User update(final User user);

    /**
     * Deletes a user by it's given UUID
     * Throws an IllegalArgumentException if the UUID doesn't correspond to an existing user
     *
     * @param id of the user to delete
     * @return if the user has been succesfully deleted
     */
    boolean delete(final UUID id);

    /**
     * Validates login credentials and returns the corresponding user when correct
     *
     * @param email of the user to login
     * @param password of the user to login
     * @return the user that's logging in or null if the credentials are invalid
     */
    User login(final String email, final String password);

    /**
     * Adds the second user to the first user's following list
     * and the first user to the second user's followers list
     *
     * Throws an IllegalArgumentException if any of the UUID's don't correspond
     * to existing users
     *
     * @param userIdThatFollows the id of the user that wants to follow the second user
     * @param userIdToFollow the id of the user to be followed
     * @return if the operation was executed succesfully
     */
    boolean follow(final UUID userIdThatFollows, final UUID userIdToFollow);

    /**
     * Removes the second user from the first user's following list
     * and the first user form the second user's followers list
     *
     * @param userIdThatFollows the id of the user that wants to stop following the second user
     * @param userIdToUnFollow the id of the user to stop following
     * @return if the operation was executed succesfully
     */
    boolean unFollow(final UUID userIdThatFollows, final UUID userIdToUnFollow);
}