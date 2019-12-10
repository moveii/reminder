package at.spengergasse.nvs.server.service;

import at.spengergasse.nvs.server.dto.ReminderDto;
import at.spengergasse.nvs.server.model.Reminder;
import at.spengergasse.nvs.server.model.Template;
import at.spengergasse.nvs.server.model.User;
import at.spengergasse.nvs.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The purpose of this service is to convert the users input to a {@code Reminder} by using templates.
 *
 * @see Template
 * @see Reminder
 */

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TemplateService {

    private final UserRepository userRepository;

    @Value("templates.rmd")
    private File templateFile;

    @Value("replacements.rmd")
    private File replacementsFiles;

    @Value("definitions.rmd")
    private File definitionsFiles;

    private List<Template> templates;
    private Map<String, List<String>> replacements;
    private Map<String, List<String>> definitions;

    /**
     * Converts a {@code ReminderDto} into a {@code Reminder} by using templates with the corresponding username.
     *
     * @param reminderDto the reminderDto to be converted
     * @param username    the username of the current user
     * @return a {@code Reminder}, which is filled with all necessary data
     */
    Reminder convert(ReminderDto reminderDto, String username) {
        ReminderDto matchedReminder = matchTemplate(reminderDto);

        Optional<User> userOptional = userRepository.findById(username);
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("No user " + username + " found!"));

        return Reminder
                .builder()
                .identifier(UUID.randomUUID().toString())
                .reminderDateTime(matchedReminder.getReminderDateTime())
                .text(matchedReminder.getText())
                .user(user)
                .build();
    }

    /**
     * Tries to match the given {@code ReminderDto} to a template. The templates are specified in the templates.rmd file
     * and must be sorted by the number of matchers (e.g. [UNIT]) and by the number of preceding words. The plus or minus
     * after the semicolon (end of template) indicates which operation to use when trying to calculate the date involving
     * [DURATION].
     *
     * <pre>
     * For instance,
     *
     * 1. [DURATION] [UNIT] after [DATE] at [TIME] [TEXT];+
     * 2. at [DURATION] [UNIT] at [TIME] [TEXT];+
     * 3. [DURATION] [UNIT] before [DATE] [TEXT];-
     * </pre>
     *
     * @param reminderDto the reminderDto to be matched to a template
     * @return a {@code ReminderDto} with all the necessary data
     * @see Template
     */
    private ReminderDto matchTemplate(ReminderDto reminderDto) {
        String unmatchedText = reminderDto.getText();

        for (Template template : templates) {
            boolean match = Stream
                    .of(template.getReplace())
                    .allMatch(unmatchedText::contains);

            if (!match) {
                continue;
            }

            String replacedText = StringUtils.replaceEach(unmatchedText, template.getReplace(), template.emptyArray());
            String[] splittedText = replacedText.split(" ");
            splittedText = Arrays.stream(splittedText)
                    .filter(string -> !string.equalsIgnoreCase("uhr"))
                    .toArray(String[]::new);

            List<String> matchers = template.getMatchers();

            Integer duration = null;
            String unit = null;
            LocalDate date = null;
            LocalTime time = null;
            String text = null;

            for (int i = 0; i < matchers.size(); i++) {

                if (splittedText.length < matchers.size()) {
                    break;
                }

                String matcher = matchers.get(i);

                switch (matcher) {
                    case "[DURATION]":
                        duration = handleDuration(splittedText[i]);
                        break;
                    case "[UNIT]":
                        unit = handleUnit(splittedText[i]);
                        break;
                    case "[DATE]":
                        date = handleDate(splittedText[i]);
                        break;
                    case "[TIME]":
                        time = handleTime(splittedText[i]);
                        break;
                    case "[TEXT]":
                        text = handleText(splittedText, i);
                        break;
                    default:
                        log.warn(matcher + " is not registered!");
                }
            }
            if ((unit != null || duration != null || date != null || time != null) & text != null) {
                LocalDateTime localDateTime = calcDate(date, time, unit, duration, template.isAddition());

                return ReminderDto
                        .builder()
                        .reminderDateTime(localDateTime)
                        .text(text)
                        .build();
            }
        }

        throw new RuntimeException("Could not match reminder to any template!");
    }

    /**
     * Extracts the duration from a string which contains either an integer or words that are understood by the system.
     * These words are specified in the definitions.rmd file.
     *
     * @param duration a string which contains an integer or words that are understood by the system
     * @return the duration as an integer
     */
    private Integer handleDuration(String duration) {
        try {
            return Integer.parseInt(find(duration, true));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Tries to translate the unit entered by the user to a unit which is understood by the system. These translations are
     * specified in the definitions.rmd file.
     *
     * @param unit the unit which is tried to be translated
     * @return the translated unit
     */
    private String handleUnit(String unit) {
        return find(unit, true);
    }

    /**
     * Tries to translate the date entered by the user to a {@code LocalDate}. The following date formats are supported:
     * yyyy-MM-dd, dd-MM-yyyy and dd.MM.YYYY. Additionally, today, tomorrow and the day after tomorrow is understood too.
     *
     * @param date the string which contains the date entered by the user
     * @return a {@code LocalDate} with the date or null when the date cannot be mapped
     */
    private LocalDate handleDate(String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (!GenericValidator.isDate(date, "yyyy-MM-dd", false)) {
            if (GenericValidator.isDate(date, "dd-MM-yyyy", false)) {
                dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            } else if (GenericValidator.isDate(date, "dd.MM.yyyy", false)) {
                dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            } else {
                String keyFromValue = find(date, true);
                switch (keyFromValue) {
                    case "today":
                        return LocalDate
                                .now();
                    case "tomorrow":
                        return LocalDate
                                .now()
                                .plusDays(1);
                    case "dat":
                        return LocalDate
                                .now()
                                .plusDays(2);
                    default:
                        return null;
                }
            }
        }

        return LocalDate.parse(date, dateTimeFormatter);
    }

    /**
     * Tries to translate the time entered by the user to a {@code LocalTime}. The following time formats are supported:
     * HH:mm and HH.
     *
     * @param time the string which contains the time entered by the user
     * @return a {@code LocalTime} with the time or null when the date cannot be mapped
     */
    private LocalTime handleTime(String time) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime localTime;

        try {
            localTime = LocalTime.parse(time, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            try {
                dateTimeFormatter = DateTimeFormatter.ofPattern("HH");
                localTime = LocalTime.parse(time, dateTimeFormatter);
            } catch (DateTimeParseException ex) {
                return null;
            }
        }

        return localTime;
    }

    /**
     * This method takes the index of the array where the text starts and appends all following content of the array.
     *
     * @param words an array of words entered by the user
     * @param start the index of the array where the text starts
     * @return the text of the reminder
     */
    private String handleText(String[] words, int start) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = start; i < words.length; i++) {
            String word = words[i];
            String replacement = find(word, false);
            stringBuilder.append(replacement);
            if (i < words.length - 1) {
                stringBuilder.append(" ");
            }
        }

        return stringBuilder.toString();
    }

    /**
     * This method either uses the definitions or replacements (depending on {@code useDefinitions}) to translate common
     * words to words the program can work with.
     *
     * @param search         the string which should be searched for in one of the two maps
     * @param useDefinitions tells the system to whether use the definitions or the replacements for this method call
     * @return the matching string or if nothing is found, search
     */
    private String find(String search, boolean useDefinitions) {
        Map<String, List<String>> map = useDefinitions ? definitions : replacements;

        Optional<Map.Entry<String, List<String>>> entry = map
                .entrySet()
                .stream()
                .filter(entries -> entries.getValue().contains(search.toLowerCase()))
                .findFirst();

        if (entry.isPresent()) {
            return entry
                    .get()
                    .getKey();
        } else {
            return search;
        }
    }

    /**
     * Calculates the {@code LocalDateTime} by using the date, time, unit, duration and the operation (addition or minus).
     *
     * @param date     the date to entered by the user
     * @param time     the time to entered by the user
     * @param unit     the unit of the duration
     * @param duration the duration
     * @param addition which operation to user (addition or subtraction)
     * @return the calculated date to remind the user
     */
    private LocalDateTime calcDate(LocalDate date, LocalTime time, String unit, Integer duration, boolean addition) {
        if (date == null || time == null) {
            if (date == null && time != null) {
                date = LocalDate.now();
            } else if (date != null) {
                time = LocalTime.of(12, 0);
            } else {
                date = LocalDate.now();
                time = LocalTime.of(12, 0);
            }
        }

        LocalDateTime dateTime = date.atTime(time);

        if (unit == null) {
            return dateTime;
        }

        switch (unit) {
            case "minute":
                return addition ? dateTime.plusMinutes(duration) : dateTime.minusMinutes(duration);
            case "hour":
                return addition ? dateTime.plusHours(duration) : dateTime.minusHours(duration);
            case "day":
                return addition ? dateTime.plusDays(duration) : dateTime.minusDays(duration);
            case "week":
                return addition ? dateTime.plusWeeks(duration) : dateTime.minusWeeks(duration);
            case "month":
                return addition ? dateTime.plusMonths(duration) : dateTime.minusMonths(duration);
            case "year":
                return addition ? dateTime.plusYears(duration) : dateTime.minusYears(duration);
            default:
                throw new RuntimeException(unit + " is not a valid unit!");
        }
    }

    /**
     * This method is called on program startup and initializes the templates, replacements and definitions.
     *
     * @throws IOException this exception will be thrown when the files cannot be read
     */
    @PostConstruct
    private void init() throws IOException {
        templates = Files
                .readAllLines(Paths.get(templateFile.getPath()))
                .stream()
                .filter(template -> !template.isEmpty())
                .map(Template::new)
                .collect(Collectors.toList());

        replacements = Files
                .readAllLines(Paths.get(replacementsFiles.getPath()))
                .stream()
                .filter(definition -> !definition.isEmpty())
                .map(definition -> definition.split("="))
                .collect(Collectors.toMap(this::definitionKey, this::definitionValues));

        definitions = Files
                .readAllLines(Paths.get(definitionsFiles.getPath()))
                .stream()
                .filter(definition -> !definition.isEmpty())
                .map(definition -> definition.split("="))
                .collect(Collectors.toMap(this::definitionKey, this::definitionValues));

        // ONLY FOR TESTING PURPOSES! WILL BE REPLACED WITH PROPER AUTHENTICATION LATER
        User user = User.builder()
                .username("testuser123")
                .password("thispasswordissuper")
                .build();

        userRepository.save(user);
    }

    /**
     * Returns the key for the definitions and replacements.
     *
     * @param definition an array which consists of the key [0] and the values [1]
     * @return the key for the definitions and replacements
     */
    private String definitionKey(String[] definition) {
        return definition[0];
    }

    /**
     * Returns the values for the definitions and replacements.
     *
     * @param definition an array which consists of the key [0] and the values [1]
     * @return the values for the definitions and replacements
     */
    private List<String> definitionValues(String[] definition) {
        return Arrays.asList(definition[1].split(","));
    }
}
