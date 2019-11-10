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
     * With this method, reminders can be search by date and time.
     *
     * @param reminderDateTime the date and time of the reminder
     * @return a list of reminders with the given date and time
     */
    List<Reminder> findAllByReminderDateTime(LocalDateTime reminderDateTime);

}
