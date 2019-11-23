package at.spengergasse.nvs.server.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A template is generated
 */

@Getter
public class Template {

    private List<String> matchers;
    private String[] replace;
    private boolean plus;

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

        plus = splittedText[1].contains("+");
    }

    public String[] emptyArray() {
        int length = replace.length;
        String[] array = new String[length];

        for (int i = 0; i < length; i++) {
            array[i] = "";
        }

        return array;
    }
}
