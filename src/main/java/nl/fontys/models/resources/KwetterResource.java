package nl.fontys.models.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import nl.fontys.api.controllers.KwetterController;
import nl.fontys.models.entities.User;
import org.springframework.hateoas.ResourceSupport;
import java.util.Date;
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
        add(linkTo(methodOn(KwetterController.class).searchByMessage("message")).withRel("SearchByMessage"));
        if (author == null) {
            add(linkTo(KwetterController.class).slash("searchbyauthorid").slash("authorId").withRel("SearchByAuthor"));
        } else {
            add(linkTo(methodOn(KwetterController.class).searchByAuthorId(author.getUserId())).withRel("SearchByAuthor"));
        }
        add(linkTo(methodOn(KwetterController.class).get(kwetterId)).withSelfRel());
    }
}