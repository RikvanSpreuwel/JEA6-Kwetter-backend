package nl.fontys.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Kwetter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    @Getter
    @Setter
    private UUID id;

    @Size(min = 1, max = 140, message = "The message must be between 1 and 140 characters")
    @Getter
    private String message;

    @NotNull(message = "PostedOn cannot be null")
    @Getter
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", locale = "UTC")
    private Date postedOn;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "authorId")
    @Getter
    @Setter
    private User author;

    public Kwetter(String message, Date postedOn, User author) {
        this.message = message;
        this.postedOn = postedOn;
        this.author = author;
    }

    public Kwetter() {
    }

    @Override
    public String toString() {
        return "Kwetter{" +
                "message='" + message + '\'' +
                ", postedOn=" + postedOn +
                ", author=" + author +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kwetter kwetter = (Kwetter) o;
        return id.equals(kwetter.id) &&
                message.equals(kwetter.message) &&
                postedOn.equals(kwetter.postedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, postedOn);
    }
}
