package com.small.util;

public class StringUtil {

    public static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder camelCaseString = new StringBuilder();
        boolean capitalizeNext = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '_' || c == '-' || c == ' ') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                camelCaseString.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                camelCaseString.append(Character.toLowerCase(c));
            }
        }

        return camelCaseString.toString();
    }

    public static String toPascalCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String camelCase = toCamelCase(input);
        return capitalizeFirstLetter(camelCase);
    }

    private static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
