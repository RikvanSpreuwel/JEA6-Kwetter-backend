package nl.fontys.data.services.interfaces;

import nl.fontys.models.Kwetter;

import java.util.List;
import java.util.UUID;


public interface IKwetterService {
    /**
     * Fetches all kwetters from the datasource
     *
     * @return a list of kwetters
     */
    List<Kwetter> findAll();

    /**
     * Fetches all kwetters of which the message contains the given message string
     *
     * @param message (or part of the message) to search for
     * @return a list of kwetters, or an empty list if no kwetters are found
     */
    List<Kwetter> findAllByMessage(final String message);

    /**
     * Fetches all kwetters of which the author is the given userId
     *
     * @param authorId id of the author to search kwetters of
     * @return a list of kwetters, or an empty list if the author has no kwetters
     */
    List<Kwetter> findAllByAuthorId(final UUID authorId);

    /**
     * Fetches a kwetter by it's UUID
     * Throws an IllegalArgumentException if the UUID doesn't correspond to an existing kwetter
     *
     * @param id of the kwetter to fetch
     * @return the requested kwetter, or null if the kwetter does not exist
     */
    Kwetter findById(final UUID id);

    /**
     * Fetches all kwetters in the users's timeline, sorted by date
     * This contains kwetters of the user himself and the users he's following
     *
     * @param userId of the user to fetch the timeline of
     * @return the user's timeline, which is a list of Kwetters
     */
    List<Kwetter> getUserTimeline(final UUID userId);

    /**
     * Fetches the user object using the given UUID. Then creates a kwetter using the user's postKwetter method.
     * Then saves this kwetter to the datasource
     *
     * @param posterId id of the user that posts the kwetter
     * @param message to post
     * @return the saved kwetter and adds it's generated UUID
     */
    Kwetter save(final UUID posterId, final String message);

    /**
     * Deletes a user by it's given UUID
     * Throws an IllegalArgumentException if the UUID doesn't correspond to an existing kwetter
     *
     * @param id of the kwetter to delete
     * @return if the kwetter has been succesfully deleted
     */
    boolean delete(final UUID id);
}