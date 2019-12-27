package at.spengergasse.nvs.server.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code Templates} are generated at program startup from the templates.rmd file.
 */

@Getter
public class Template {

    /**
     * This list of strings contains all matchers (e.g. [DATE]) which need to be matched to fulfill this template.
     */
    private List<String> matchers;

    /**
     * This string array contains all extra words in the template, which need to be replaced to fulfill this template.
     */
    private String[] replace;

    /**
     * If this fields value is true, the duration is added to the date/time. Otherwise it is subtracted.
     */
    private boolean addition;

    /**
     * If this fields value is true, the first word of the template has to be replaced.
     */
    private boolean firstReplaced;

    /**
     * The constructor converts the given text to a {@code Template}.
     *
     * @param text the text, which was entered by the user.
     */
    public Template(String text) {
        String[] splittedText = text.split(";");

        if (splittedText.length < 1) {
            throw new RuntimeException("Template is not long enough!");
        } else if (splittedText.length < 2) {
            splittedText = new String[]{splittedText[0], ""};
        }

        String[] words = splittedText[0].split(" ");

        matchers = Arrays
                .stream(words)
                .filter(string -> string.matches("\\[(.*?)\\]"))
                .collect(Collectors.toList());

        replace = Arrays
                .stream(words)
                .filter(string -> !string.matches("\\[(.*?)\\]"))
                .map(string -> string.concat(" "))
                .toArray(String[]::new);

        firstReplaced = List.of(replace).contains(words[0] + " ");
        addition = splittedText[1].contains("+");
    }

    /**
     * This method returns an array of empty strings, whose length equals the length of the matcher list.
     *
     * @return an array of emtpy strings, whose length equals the length of the matcher list.
     */
    public String[] emptyArray() {
        int length = replace.length;
        String[] array = new String[length];

        for (int i = 0; i < length; i++) {
            array[i] = "";
        }

        return array;
    }
}
