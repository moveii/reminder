package at.spengergasse.nvs.server.model;

import at.spengergasse.nvs.server.dto.ReminderDto;
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
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Reminder {

    /**
     * The identifier is a unique string, which is used to identify reminders
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

    /**
     * Maps {@code ReminderDtos} to {@code Reminders} via custom mapping to transform text entered by the user to
     * a date, time and text.
     *
     * @param reminderDto the object which has to be converted to a reminder via custom mapping
     * @see ReminderDto
     * @see Reminder
     */
    public Reminder(ReminderDto reminderDto) {
        // TODO implement custom mapping
    }
}
