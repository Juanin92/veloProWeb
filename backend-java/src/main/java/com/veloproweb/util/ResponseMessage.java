package com.veloproweb.util;

import java.util.HashMap;
import java.util.Map;

public class ResponseMessage {

    /**
     * Crear un mensaje de respuesta en formato JSON
     * @param text - texto del mensaje
     * @return - un mapa con el mensaje
     */
    public static Map<String, String> message(String text) {
        Map<String, String> response = new HashMap<>();
        response.put("message", text);
        return response;
    }

    /**
     * Crear un mensaje de respuesta en formato JSON con una clave personalizada
     * @param key - clave del mensaje
     * @param text - texto del mensaje
     * @return - un mapa con el mensaje
     */
    public static Map<String, String> messageWithKey(String key, String text) {
        Map<String, String> response = new HashMap<>();
        response.put(key, text);
        return response;
    }

    /**
     * Crear un mensaje de respuesta en formato JSON con dos claves y valores
     * @param key - primera clave
     * @param value - valor de la primera clave
     * @param booleanKey - segunda clave
     * @param booleanValue - valor de la segunda clave
     * @return - un mapa con los mensajes
     */
    public static Map<String, Object> messageWithBooleanKey(String key, String value, String booleanKey,
                                                            boolean booleanValue) {
        Map<String, Object> response = new HashMap<>();
        response.put(key, value);
        response.put(booleanKey, booleanValue);
        return response;
    }

    /**
     * Crear un mensaje de respuesta en formato JSON con múltiples claves y valores
     * @param keyValues - pares clave-valor
     * @return - un mapa con los mensajes
     */
    public static Map<String, String> multiMessage(String... keyValues) {
        Map<String, String> response = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            response.put(keyValues[i], keyValues[i + 1]);
        }
        return response;
    }
}
