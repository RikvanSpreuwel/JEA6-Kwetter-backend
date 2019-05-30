package nl.fontys.websocket.models;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class OutputMessage {
    private UUID authorId;
    private Date postedOn;

    public OutputMessage(UUID authorId, Date postedOn) {
        this.authorId = authorId;
        this.postedOn = postedOn;
    }
}
