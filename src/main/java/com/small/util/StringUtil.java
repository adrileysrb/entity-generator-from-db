package com.small.util;

public class StringUtil {

    /**
     * Converte strings com underline para camelCase.
     * Exemplo: "first_name" -> "firstName"
     *
     * @param input a string no formato com underline
     * @return a string convertida para camelCase
     */
    public static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (char ch : input.toCharArray()) {
            if (ch == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(ch));
                capitalizeNext = false;
            } else {
                result.append(ch);
            }
        }

        return result.toString();
    }

    /**
     * Converte strings com underline ou letras minúsculas para o formato
     * PascalCase.
     * Exemplo: "t_person" -> "TPerson"
     *
     * @param input a string original (ex.: nome da tabela)
     * @return a string formatada para o padrão PascalCase
     */
    public static String toPascalCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char ch : input.toCharArray()) {
            if (ch == '_') {
                capitalizeNext = true; // Próxima letra será maiúscula
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(ch));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(ch));
            }
        }

        return result.toString();
    }
}
