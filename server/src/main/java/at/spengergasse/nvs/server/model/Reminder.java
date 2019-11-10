package at.spengergasse.nvs.server.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

/**
 * A {@code Reminder} contains all information necessary to remind the specified user (if logged in).
 *
 * @see User
 */

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Reminder {

    /**
     * The identifier is a unique string, which identifies Reminders
     */

    @Id
    private String identifier; // will be set with `UUID.randomUUID().toString()` later

    /**
     * This field contains the text, which will be displayed when the user is reminded.
     */
    private String text;

    /**
     * In this field, the date and time of the reminder is stored. Seconds will be ignored.
     */
    private LocalDateTime reminderDateTime;

    /**
     * This field links the reminder to a user.
     */
    @ManyToOne
    private User user;

}
