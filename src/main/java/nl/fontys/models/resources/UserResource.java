package nl.fontys.models.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import nl.fontys.api.controllers.KwetterController;
import nl.fontys.api.controllers.UserController;
import nl.fontys.models.entities.Kwetter;
import nl.fontys.models.entities.Role;
import nl.fontys.models.entities.User;
import org.springframework.hateoas.ResourceSupport;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Getter
public class UserResource extends ResourceSupport {
    private UUID userId;
    private String password;
    private String email;
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

    public UserResource(UUID userId, String password, String email, String firstName, String lastName,
                        String userName, Date dateOfBirth, String bio, String location, String profilePicture,
                        Role role, List<UserResource> following, List<UserResource> followers, List<KwetterResource> kwetters) {
        this.userId = userId;
        this.password = password;
        this.email = email;
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
        add(linkTo(methodOn(UserController.class).search("userNameToSearch")).withRel("Search"));
        add(linkTo(methodOn(KwetterController.class).getUserTimeline(userId)).withRel("Timeline"));
        add(linkTo(methodOn(UserController.class).get(userId)).withSelfRel());
    }
}