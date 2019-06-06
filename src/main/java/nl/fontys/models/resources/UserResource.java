package nl.fontys.models.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import nl.fontys.api.controllers.KwetterController;
import nl.fontys.api.controllers.UserController;
import nl.fontys.models.entities.Role;
import org.springframework.hateoas.ResourceSupport;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Getter
public class UserResource extends ResourceSupport {
    private UUID userId;
    private String password;
    private String email;
    private boolean verified;
    private String firstName;
    private String lastName;
    private String userName;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date dateOfBirth;
    private String bio;
    private String location;
    private String profilePicture;
    private Role role;

    private List<UserResource> following;
    private List<UserResource> followers;
    private List<KwetterResource> kwetters;

    public UserResource(UUID userId, String password, String email, boolean verified, String firstName, String lastName,
                        String userName, Date dateOfBirth, String bio, String location, String profilePicture,
                        Role role, List<UserResource> following, List<UserResource> followers, List<KwetterResource> kwetters) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.verified = verified;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.dateOfBirth = dateOfBirth;
        this.bio = bio;
        this.location = location;
        this.profilePicture = profilePicture;
        this.role = role;
        this.following = following;
        this.followers = followers;
        this.kwetters = kwetters;

        add(linkTo(UserController.class).withRel("Users"));
        add(linkTo(methodOn(KwetterController.class).getUserTimeline(userId)).withRel("Timeline"));
        add(linkTo(methodOn(UserController.class).get(userId)).withSelfRel());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserResource that = (UserResource) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(password, that.password) &&
                Objects.equals(email, that.email) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(dateOfBirth, that.dateOfBirth) &&
                Objects.equals(bio, that.bio) &&
                Objects.equals(location, that.location) &&
                Objects.equals(profilePicture, that.profilePicture) &&
                role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userId, password, email, firstName, lastName, userName, dateOfBirth, bio, location, profilePicture, role);
    }
}