package at.spengergasse.nvs.server.controller;

import at.spengergasse.nvs.server.config.JwtTokenUtil;
import at.spengergasse.nvs.server.dto.ReminderDto;
import at.spengergasse.nvs.server.model.AuthToken;
import at.spengergasse.nvs.server.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * This class provides all methods necessary for the REST-API for the Reminders.
 */

@RestController
@RequestMapping(path = "reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;
    private final JwtTokenUtil jwtTokenUtil;

    /**
     * Returns all reminders of the authenticated user when called via a HTTP-GET-REQUEST.
     *
     * @return all reminders of the authenticated user
     */
    @GetMapping(path = "all")
    public List<ReminderDto> findAllReminders(HttpServletRequest request) {
        return reminderService.findAllReminders(jwtTokenUtil.getUsernameFromRequest(request));
    }

    /**
     * Returns all reminders of the authenticated user whose date is in the future when called via a HTTP-GET-REQUEST.
     *
     * @return all reminders of the authenticated user whose date is in the future
     */
    @GetMapping
    public List<ReminderDto> findAllRemindersByDateAndTime(HttpServletRequest request) {
        AuthToken tokenFromRequest = jwtTokenUtil.getTokenFromRequest(request);
        return reminderService.findAllRemindersByDateAndTime(tokenFromRequest.getUsername());
    }

    /**
     * Returns the analysed, translated and persisted {@code ReminderDto} linked to the authenticated user when called
     * via a HTTP-POST-REQUEST.
     *
     * @param reminderDto the reminderDto to be analysed, translated and persisted
     * @return the analysed, translated and persisted reminderDto
     */
    @PostMapping
    public ResponseEntity<ReminderDto> createReminder(HttpServletRequest request,
                                                      @RequestBody ReminderDto reminderDto) {
        return ResponseEntity.of(reminderService.createReminder(reminderDto, jwtTokenUtil.getUsernameFromRequest(request)));
    }

    /**
     * Returns the modified reminder when called via a HTTP-PUT-REQUEST. Only the text should be updated!
     *
     * @param reminderDto the modified reminderDto
     * @return the modified reminder
     */
    @PutMapping
    public ResponseEntity<ReminderDto> modifyReminder(HttpServletRequest request,
                                                      @RequestBody ReminderDto reminderDto) {
        return ResponseEntity.of(reminderService.modifyReminder(reminderDto, jwtTokenUtil.getUsernameFromRequest(request)));
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
