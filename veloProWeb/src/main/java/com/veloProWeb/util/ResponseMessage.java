package com.veloProWeb.util;

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
}
