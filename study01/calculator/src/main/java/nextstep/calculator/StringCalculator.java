package nextstep.calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    public int add(String text) {
        if(isBlank(text)) {
            return 0;
        }

        return sum(toInts(split(text)));
    }

    private String[] split(String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String delimiter = m.group(1);
            return m.group(2).split(delimiter);
        }

        return text.split(",|:");
    }

    private boolean isBlank(String text) {
        return text == null || text.isEmpty();
    }

    private int[] toInts(String[] tokens) {
        int[] ints = new int[tokens.length];

        for (int i = 0; i < tokens.length; i++) {
            ints[i] = toPositive(tokens[i]);
        }

        return ints;
    }

    private int toPositive(String token) {
        int number = Integer.parseInt(token);

        if (number < 0) {
            throw new RuntimeException();
        }

        return number;
    }

    private int sum(int[] values) {
        int sum = 0;

        for (int value : values) {
            sum += value;
        }

        return sum;
    }
}
