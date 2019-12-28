package at.spengergasse.nvs.server.repository;

import at.spengergasse.nvs.server.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The {@code ReminderRepository} provides all methods for CRUD operations by default for reminders and an extra defined method
 * in order to search for reminders by date and time
 */

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, String> {

    /**
     * This method returns all reminders from a user.
     *
     * @param username the username of the user
     * @return the reminders from the user
     */
    List<Reminder> findAllByUserUsername(String username);

    /**
     * This method returns all reminders after a given time and date from a user.
     *
     * @param reminderDateTime only reminders after this date and time will be returned
     * @param username         the username of the user
     * @return the reminders after the given time and date from the user
     */
    List<Reminder> findAllByReminderDateTimeAfterAndUserUsername(LocalDateTime reminderDateTime, String username);
}
