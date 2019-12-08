package at.spengergasse.nvs.server.controller;

import at.spengergasse.nvs.server.dto.ReminderDto;
import at.spengergasse.nvs.server.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This class provides all methods necessary for the REST-API.
 */

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping(path = "reminder")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    /**
     * Returns all reminders of the authenticated user when called via a HTTP-GET-REQUEST.
     *
     * @return all reminders of the authenticated user
     */
    @GetMapping
    public List<ReminderDto> findAllReminders() {
        return reminderService.findAllReminders("");
    }

    /**
     * Returns all reminders after the given date and time of the authenticated user when called via a HTTP-GET-REQUEST.
     *
     * @param dateTime the minimum date and time of the reminders
     * @return all reminders of the authenticated user after the given date and time
     */
    @GetMapping(path = "{dateTime}")
    public List<ReminderDto> findAllRemindersByDateAndTime(@PathVariable LocalDateTime dateTime) {
        return reminderService.findAllRemindersByDateAndTime(dateTime, "");
    }

    /**
     * Returns an server-side event stream linked to the authenticated user to refresh when a reminder is due when
     * called via a HTTP-GET-REQUEST.
     *
     * @return the SseEmitter
     * @see SseEmitter
     */
    @GetMapping(path = "/register")
    public SseEmitter register() {
        return reminderService.registerClient(null);
    }

    /**
     * Returns the analysed, translated and persisted {@code ReminderDto} linked to the authenticated user when called
     * via a HTTP-POST-REQUEST.
     *
     * @param reminderDto the reminderDto to be analysed, translated and persisted
     * @return the analysed, translated and persisted reminderDto
     */
    @PostMapping
    public ResponseEntity<ReminderDto> createReminder(@RequestBody ReminderDto reminderDto) {
        return ResponseEntity.of(reminderService.createReminder(reminderDto, ""));
    }

    /**
     * Returns the modified reminder when called via a HTTP-PUT-REQUEST.
     *
     * @param reminderDto the modified reminderDto
     * @return the modified reminder
     */
    @PutMapping
    public ResponseEntity<ReminderDto> modifyReminder(@RequestBody ReminderDto reminderDto) {
        return ResponseEntity.of(reminderService.modifyReminder(reminderDto));
    }

    /**
     * Deletes a reminder by its identifier.
     *
     * @param identifier the identifier of the reminder to delete
     */
    @DeleteMapping(path = "{identifier}")
    public void deleteReminder(@PathVariable String identifier) {
        reminderService.deleteReminder(identifier);
    }

}
