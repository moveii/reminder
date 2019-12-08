package at.spengergasse.nvs.server.service;

import at.spengergasse.nvs.server.dto.ReminderDto;
import at.spengergasse.nvs.server.model.Reminder;
import at.spengergasse.nvs.server.model.User;
import at.spengergasse.nvs.server.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class provides all methods for CRUD operations and checks every minute for reminders.
 *
 * @see Reminder
 */

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReminderService {

    private final TemplateService templateService;
    private final ReminderRepository reminderRepository;
    private final ModelMapper modelMapper;

    private HashMap<User, SseEmitter> sseEmitters = new HashMap<>();

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
     * @see TemplateService
     */
    public Optional<ReminderDto> createReminder(ReminderDto reminderDto, String username) {
        return Optional
                .of(reminderDto)
                .map(dto -> templateService.convert(dto, username))
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
     * Registers a {@code SseEmitter} and links it to the given user. If the same user logs in again while the other stream
     * is running, the stream will be stopped and a new one will be started.
     *
     * @param user the authenticated user requesting a server-side event-stream
     * @return the registered {@code SseEmitter} linked to he given user
     * @see SseEmitter
     **/
    public SseEmitter registerClient(User user) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitter.onCompletion(() -> sseEmitters.remove(user, sseEmitter));
        sseEmitter.onError(throwable -> {
            sseEmitters.remove(user, sseEmitter);
            log.warn("SSE-Error: {} for {}", throwable.getLocalizedMessage(), user.getUsername());
        });
        sseEmitter.onTimeout(() -> {
            sseEmitters.remove(user, sseEmitter);
            log.warn("SSE-Timeout for {}", user.getUsername());
        });

        sseEmitters.computeIfPresent(user, (user_temp, sseEmitter_temp) -> {
            sseEmitter_temp.complete();
            return sseEmitter_temp;
        });

        return sseEmitters.put(user, sseEmitter);
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
     * This method handles due reminders by sending events to the authenticated client.
     *
     * @param reminder the due reminder
     */
    private void handleDueReminders(Reminder reminder) {
        this.sseEmitters.computeIfPresent(reminder.getUser(), (user, sseEmitter) -> {
                    Optional
                            .of(reminder)
                            .map(model -> modelMapper.map(model, ReminderDto.class))
                            .ifPresent(reminderDto -> {
                                try {
                                    sseEmitter.send(reminderDto);
                                } catch (IOException e) {
                                    log.warn("Could not send {} to user {}.", reminder.getIdentifier(), user.getUsername());
                                }
                            });
                    return sseEmitter;
                }
        );
    }
}
