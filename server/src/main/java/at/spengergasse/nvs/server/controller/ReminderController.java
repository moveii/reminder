package at.spengergasse.nvs.server.controller;

import at.spengergasse.nvs.server.dto.ReminderDto;
import at.spengergasse.nvs.server.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping(path = "reminder")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping
    public List<ReminderDto> findAllReminders() {
        return reminderService.findAllReminders("");
    }

    @GetMapping(path = "{dateTime}")
    public List<ReminderDto> findAllRemindersByDateAndTime(@PathVariable LocalDateTime dateTime) {
        return reminderService.findAllRemindersByDateAndTime(dateTime, "");
    }

    @GetMapping(path = "/register")
    public SseEmitter register() {
        return reminderService.registerClient(null);
    }

    @PostMapping
    public ResponseEntity<ReminderDto> createReminder(@RequestBody ReminderDto reminderDto) {
        return ResponseEntity.of(reminderService.createReminder(reminderDto, ""));
    }

    @PutMapping
    public ResponseEntity<ReminderDto> modifyReminder(@RequestBody ReminderDto reminderDto) {
        return ResponseEntity.of(reminderService.modifyReminder(reminderDto));
    }

    @DeleteMapping(path = "{identifier}")
    public void deleteReminder(@PathVariable String identifier) {
        reminderService.deleteReminder(identifier);
    }

}
