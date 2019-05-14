package nl.fontys.utils.modelMapper.converters;

import nl.fontys.models.entities.Kwetter;
import nl.fontys.models.entities.User;
import nl.fontys.models.resources.KwetterResource;
import nl.fontys.models.resources.UserResource;
import org.modelmapper.AbstractConverter;

public class ToKwetterResourceModelConverter extends AbstractConverter<Kwetter, KwetterResource> {
    @Override
    protected KwetterResource convert(Kwetter kwetter) {
        return new KwetterResource(kwetter.getId(), kwetter.getMessage(), kwetter.getPostedOn(),
                getAuthorWithoutRecursion(kwetter.getAuthor()));
    }

    private UserResource getAuthorWithoutRecursion(User user){
        return new UserResource(user.getId(), user.getPassword(), user.getEmail(), user.getFirstName(),
                user.getLastName(), user.getUserName(), user.getDateOfBirth(), user.getBio(), user.getLocation(), user.getProfilePicture(),
                user.getRole(), null, null, null);
    }
}
