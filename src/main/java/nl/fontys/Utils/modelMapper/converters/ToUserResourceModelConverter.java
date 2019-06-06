package nl.fontys.utils.modelMapper.converters;

import nl.fontys.models.entities.Kwetter;
import nl.fontys.models.entities.User;
import nl.fontys.models.resources.KwetterResource;
import nl.fontys.models.resources.UserResource;
import org.modelmapper.AbstractConverter;

import java.util.List;
import java.util.stream.Collectors;

public class ToUserResourceModelConverter extends AbstractConverter<User, UserResource> {
    @Override
    protected UserResource convert(User user) {
        return new UserResource(user.getId(), user.getPassword(), user.getEmail(), user.isVerified(), user.getFirstName(), user.getLastName(),
                user.getUserName(), user.getDateOfBirth(), user.getBio(), user.getLocation(), user.getProfilePicture(), user.getRole(),
                getUserResourcesWithoutRecursion(user.getFollowing()),
                getUserResourcesWithoutRecursion(user.getFollowers()),
                getKwetterResourcesWithoutRecursion(user.getKwetters()));
    }

    private List<UserResource> getUserResourcesWithoutRecursion(List<User> users) {
        if (users == null) return null;
        return users.stream().map(user -> new UserResource(user.getId(), user.getPassword(), user.getEmail(), user.isVerified(), user.getFirstName(),
                user.getLastName(), user.getUserName(), user.getDateOfBirth(), user.getBio(), user.getLocation(), user.getProfilePicture(),
                user.getRole(), null, null, null)).collect(Collectors.toList());
    }

    private List<KwetterResource> getKwetterResourcesWithoutRecursion(List<Kwetter> kwetters) {
        if (kwetters == null) return null;
        return kwetters.stream().map(kwetter -> new KwetterResource(kwetter.getId(), kwetter.getMessage(),
                kwetter.getPostedOn(), null)).collect(Collectors.toList());
    }
}
