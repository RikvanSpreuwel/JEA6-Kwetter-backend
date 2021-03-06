package nl.fontys.models.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import nl.fontys.api.controllers.KwetterController;
import nl.fontys.api.controllers.UserController;
import org.springframework.hateoas.ResourceSupport;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Getter
public class KwetterResource extends ResourceSupport {
    private UUID kwetterId;
    private String message;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale = "UTC")
    private Date postedOn;
    private UserResource author;

    public KwetterResource(UUID kwetterId, String message, Date postedOn, UserResource author) {
        this.kwetterId = kwetterId;
        this.message = message;
        this.postedOn = postedOn;
        this.author = author;

        add(linkTo(KwetterController.class).withRel("Kwetters"));
        if (author != null) {
            add(linkTo(methodOn(KwetterController.class).searchByAuthorId(author.getUserId())).withRel("SearchByAuthor"));
            add(linkTo(methodOn(UserController.class).get(author.getUserId())).withRel("Author"));
        }
        add(linkTo(methodOn(KwetterController.class).get(kwetterId)).withSelfRel());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        KwetterResource that = (KwetterResource) o;
        return kwetterId.equals(that.kwetterId) &&
                message.equals(that.message) &&
                postedOn.equals(that.postedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), kwetterId, message, postedOn);
    }
}