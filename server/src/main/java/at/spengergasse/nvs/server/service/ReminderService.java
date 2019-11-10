package at.spengergasse.nvs.server.service;

import at.spengergasse.nvs.server.dto.ReminderDto;
import at.spengergasse.nvs.server.model.Reminder;
import at.spengergasse.nvs.server.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class provides all methods for CRUD operations and checks every minute for reminders.
 *
 * @see Reminder
 */

@Component
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final ModelMapper modelMapper;

    /**
     * This method returns all reminders form the given user.
     *
     * @param username the username of the user
     * @return all reminders from the given user
     */
    public List<ReminderDto> findAllReminders(String username) {
        return reminderRepository
                .findAllByUserUsername(username)
                .stream()
                .map(model -> modelMapper.map(model, ReminderDto.class))
                .collect(Collectors.toList());
    }

    /**
     * This method returns all reminders after the given date and time from the given user.
     *
     * @param reminderDateTime the date and time
     * @param username         the username of the user
     * @return all reminders after the given date and time from the given user.
     */
    public List<ReminderDto> findAllRemindersByDateAndTime(LocalDateTime reminderDateTime, String username) {
        return reminderRepository
                .findAllByReminderDateTimeAfterAndUserUsername(reminderDateTime, username)
                .stream()
                .map(model -> modelMapper.map(model, ReminderDto.class))
                .collect(Collectors.toList());
    }

    /**
     * This method creates a reminder and persists it.
     *
     * @param reminderDto the reminderDto from which a reminder should be created
     * @return the reminderDto filled with all information to be able to display it
     */
    public Optional<ReminderDto> createReminder(ReminderDto reminderDto) {
        return Optional
                .of(reminderDto)
                .map(Reminder::new)
                .map(reminderRepository::save)
                .map(model -> modelMapper.map(model, ReminderDto.class));
    }

    /**
     * This method modifies the given reminder.
     *
     * @param reminderDto the reminderDto from which a reminder should be updated
     * @return the updated reminderDto
     */
    public Optional<ReminderDto> modifyReminder(ReminderDto reminderDto) {
        return Optional
                .of(reminderDto)
                .map(dto -> modelMapper.map(dto, Reminder.class))
                .map(reminderRepository::save)
                .map(model -> modelMapper.map(model, ReminderDto.class));
    }

    /**
     * This method deletes the reminder with the given identifier.
     *
     * @param identifier the identifier from the reminder to delete
     */
    public void deleteReminder(String identifier) {
        reminderRepository.deleteById(identifier);
    }

    /**
     * This method checks every minute for due reminders.
     *
     * @see Scheduled
     */
    @Scheduled(cron = "0 * * * * *")
    public void checkForDueReminders() {
        reminderRepository
                .findAllByReminderDateTime(LocalDateTime.now())
                .forEach(this::handleDueReminders);
    }

    /**
     * This method handles due reminders by sending events to the client.
     *
     * @param reminder the due reminder
     */
    private void handleDueReminders(Reminder reminder) {
        // TODO implement SseEmitters
    }
}
