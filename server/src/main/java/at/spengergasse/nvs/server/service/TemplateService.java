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

@Slf4j
@Service
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
    }

    Reminder convert(ReminderDto reminderDto, String username) {
        ReminderDto matchedReminder = matchTemplate(reminderDto);

        Optional<User> userOptional = userRepository.findById(username);
        User user = userOptional.orElseThrow(RuntimeException::new);

        return Reminder
                .builder()
                .identifier(UUID.randomUUID().toString())
                .reminderDateTime(matchedReminder.getReminderDateTime())
                .text(matchedReminder.getText())
                .user(user)
                .build();
    }

    private String definitionKey(String[] definition) {
        return definition[0];
    }

    private List<String> definitionValues(String[] definition) {
        return Arrays.asList(definition[1].split(","));
    }

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
                LocalDateTime localDateTime = calcDate(date, time, unit, duration, template.isPlus());

                return ReminderDto
                        .builder()
                        .reminderDateTime(localDateTime)
                        .text(text)
                        .build();
            }
        }

        throw new RuntimeException("Could not match reminder to any template!");
    }

    private Integer handleDuration(String duration) {
        try {
            return Integer.parseInt(find(duration, true));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String handleUnit(String unit) {
        return find(unit, true);
    }

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

    private LocalDateTime calcDate(LocalDate date, LocalTime time, String unit, Integer duration, boolean plus) {
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
                return plus ? dateTime.plusMinutes(duration) : dateTime.minusMinutes(duration);
            case "hour":
                return plus ? dateTime.plusHours(duration) : dateTime.minusHours(duration);
            case "day":
                return plus ? dateTime.plusDays(duration) : dateTime.minusDays(duration);
            case "week":
                return plus ? dateTime.plusWeeks(duration) : dateTime.minusWeeks(duration);
            case "month":
                return plus ? dateTime.plusMonths(duration) : dateTime.minusMonths(duration);
            case "year":
                return plus ? dateTime.plusYears(duration) : dateTime.minusYears(duration);
            default:
                throw new RuntimeException(unit + " is not a valid unit!");
        }
    }
}
