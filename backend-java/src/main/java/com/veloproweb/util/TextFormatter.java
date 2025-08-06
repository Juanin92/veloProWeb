package com.veloproweb.util;

public class TextFormatter {

    private TextFormatter() {
    }

    /**
     * Convierte la primera letra de cada palabra en mayúscula
     * @param value cadena de texto a capitalizar
     * @return palabra capitalizada
     */
    public static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String[] words = value.split(" ");
        StringBuilder capitalized = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(word.substring(0, 1).toUpperCase());
                capitalized.append(word.substring(1).toLowerCase());
                capitalized.append(" ");
            }
        }
        return capitalized.toString().trim();
    }

    /**
     * Convierte cada palabra en mayúsculas.
     * @param value - cadena de texto a convertir
     * @return - devuelve la cadena en mayúscula
     */
    public static String upperCaseWord(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String[] words = value.split(" ");
        StringBuilder capitalized = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(word.toUpperCase()).append(" ");
            }
        }
        return capitalized.toString().trim();
    }
}
