package at.spengergasse.nvs.server.service;

import at.spengergasse.nvs.server.dto.ReminderDto;
import at.spengergasse.nvs.server.model.Reminder;
import at.spengergasse.nvs.server.repository.ReminderRepository;
import at.spengergasse.nvs.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
    private final UserRepository userRepository;
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
     * This method returns all reminders whose date is in the future from the given user.
     *
     * @param username the username of the user
     * @return all reminders after the given date and time from the given user.
     */
    public List<ReminderDto> findAllRemindersByDateAndTime(String username) {
        return reminderRepository
                .findAllByReminderDateTimeAfterAndUserUsername(LocalDateTime.now(), username)
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
    public Optional<ReminderDto> modifyReminder(ReminderDto reminderDto, String username) {
        return Optional
                .of(reminderDto)
                .map(dto -> modelMapper.map(dto, Reminder.class))
                .map(model -> {
                    model.setUser(userRepository
                            .findById(username)
                            .orElseThrow(() -> new IllegalArgumentException("No user " + username + " found!")));

                    return model;
                })
                .map(reminderRepository::save)
                .map(model -> modelMapper.map(model, ReminderDto.class));
    }

    /**
     * This method deletes the reminder with the given identifier.
     *
     * @param identifier the identifier from the reminder to delete
     */
    public void deleteReminder(String identifier) {
        reminderRepository.findById(identifier).ifPresent(reminder -> reminderRepository.deleteById(reminder.getIdentifier()));
    }
}
