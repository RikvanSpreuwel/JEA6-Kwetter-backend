package nl.fontys.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import nl.fontys.utils.Constants;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @JsonProperty("id")
    public UUID getId(){
        return this.id;
    }

    @Size(min = 8, message = "Password has a minimum of 8 characters.")
    private String password;

    @Transient
    @Size(min = 8, message = "Password has a minimum of 8 characters.")
    private String passwordConfirm;

    @NotNull(message = "Email cannot be null.")
    @Email(regexp = Constants.EMAIL_REGEX, message = "Email should be valid")
    private String email;

    private boolean verified;

    @NotNull(message = "FirstName cannot be null")
    private String firstName;

    @NotNull(message = "LastName cannot be null")
    private String lastName;

    @NotNull(message = "Username cannot be null")
    private String userName;

    @NotNull(message = "Date of Birth cannot be null")
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date dateOfBirth;

    @NotNull(message = "Bio cannot be null.")
    @Size(min = 1, max = 160, message = "Bio must be between 1 and 140 characters")
    private String bio;

    @NotNull(message = "Location cannot be null")
    private String location;

    @Nullable
    private String profilePicture;

    @NotNull(message = "Role cannot be null.")
    @Enumerated(EnumType.STRING)
    private Role role;

    @JoinTable(name = "follow")
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private List<User> following;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH, mappedBy = "following")
    private List<User> followers;

    @OneToMany(mappedBy = "author", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Kwetter> kwetters;

    public User() {
        this.verified = false;
        this.following = new ArrayList<>();
        this.following = new ArrayList<>();
        this.kwetters = new ArrayList<>();
    }

    public User(@NotNull(message = "Password cannot be null") String password, @Email(message = "Email should be valid") String email,
                @NotNull(message = "FirstName cannot be null") String firstName, @NotNull(message = "LastName cannot be null") String lastName,
                @NotNull(message = "Username cannot be null") String userName, @NotNull(message = "Date of Birth cannot be null") Date dateOfBirth,
                @Size(min = 1, max = 160, message = "Bio must be between 1 and 140 characters") String bio,
                @NotNull(message = "Location cannot be null") String location, @Nullable String profilePicture, Role role) {
        this.password = password;
        this.email = email;
        this.verified = false;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.dateOfBirth = dateOfBirth;
        this.bio = bio;
        this.location = location;
        this.profilePicture = profilePicture;
        this.role = role;
        this.following = new ArrayList<>();
        this.followers = new ArrayList<>();
        this.kwetters = new ArrayList<>();
    }

    private void addFollower(final User user) {
        this.followers.add(user);
    }

    private void removeFollower(final User user) {
        this.followers.remove(user);
    }

    public void follow(final User user){
        if (user == null) throw new IllegalArgumentException("The user cannot be null.");
        if (user.equals(this)) throw new IllegalArgumentException("A user cannot follow itself.");
        if (following.contains(user)) throw new IllegalArgumentException("The given user is already being followed by this user.");

        following.add(user);
        user.addFollower(this);
    }

    public void unFollow(final User user){
        if (user == null) throw new IllegalArgumentException("The user cannot be null.");
        if (user.equals(this)) throw new IllegalArgumentException("A user cannot un follow itself.");
        if (!following.contains(user)) throw new IllegalArgumentException("The given user is not being followed by this user.");

        following.remove(user);
        user.removeFollower(this);
    }

    public Kwetter postKwetter(final String message) {
        if (message == null) throw new IllegalArgumentException("The given message cannot be null.");
        if (message.length() < 1 || message.length() > 140)
            throw new IllegalArgumentException("The message has a minimum of 1 character and a maximum of 140 characters.");

        final Kwetter postedKwetter = new Kwetter(message, Calendar.getInstance().getTime(), this);
        kwetters.add(postedKwetter);
        return postedKwetter;
    }

    public boolean removeKwetter(final Kwetter kwetter){
        if (kwetter == null) throw new IllegalArgumentException("The given kwetter cannot be null.");
        if (!kwetters.contains(kwetter)) throw new IllegalArgumentException("This user has not posted this kwetter.");

        return kwetters.remove(kwetter);
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;

        return Objects.equals(id, user.id) &&
                password.equals(user.password) &&
                email.equals(user.email) &&
                firstName.equals(user.firstName) &&
                lastName.equals(user.lastName) &&
                userName.equals(user.userName) &&
                dateOfBirth.getYear() == user.dateOfBirth.getYear() &&
                dateOfBirth.getMonth() == user.dateOfBirth.getMonth() &&
                dateOfBirth.getDay() == user.dateOfBirth.getDay() &&
                bio.equals(user.bio) &&
                location.equals(user.location) &&
                Objects.equals(profilePicture, user.profilePicture) &&
                role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, password, email, firstName, lastName, userName, dateOfBirth, bio, location, profilePicture, role);
    }
}