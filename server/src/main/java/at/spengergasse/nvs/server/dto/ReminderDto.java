package at.spengergasse.nvs.server.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * A {@code Reminder} contains all information necessary to create proper reminders or display them.
 */

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReminderDto {

    /**
     * The identifier is a unique string, which is used to identify reminders.
     */
    private String identifier;

    /**
     * This field contains the text, which was entered by the user when received. When sent, the text will be replaced
     * by the text which will be displayed.
     */
    private String text;

    /**
     * This field is empty when received.
     */
    private LocalDateTime reminderDateTime;

}
