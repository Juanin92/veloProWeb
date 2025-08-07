package com.veloproweb.util;

import java.time.LocalDate;

public class IdentifyDocumentGenerator {

    private IdentifyDocumentGenerator() {}

    /**
     * Genera el identificador del documento de ticket.
     * @param document - último documento con el formato creado
     * @return - identificador del documento del ticket
     */
    public static String generateIdentifyDocumentTicket(String document){
        return String.format("%s%s-%s", "T-", generateDate(), generateSequentialNumber(document));
    }

    /**
     * Genera el identificador del documento de venta (boleta).
     * @param document - último documento con el formato creado
     * @return - identificador del documento de venta
     */
    public static String generateIdentifyDocumentSale(String document){
        return String.format("%s%s-%s", "BO-", generateDate(), generateSequentialNumber(document));
    }

    /**
     * Genera el nuevo número secuencial.
     * El número secuencial se reinicia cada mes.
     * Si el documento no tiene fecha, se asigna el número 1.
     * @param document - último documento con el formato creado
     * @return - número secuencial formateado
     */
    private static String generateSequentialNumber(String document) {
        String currentMonthYear = generateDate();
        String extractedDate = extractDateDocument(document);
        if (extractedDate.equals("0000")){
            return String.format("%04d", 1);
        }
        int number = Integer.parseInt(extractSequentialNumber(document));
        return String.format("%04d", currentMonthYear.equals(extractedDate) ? number + 1 : 1);
    }

    /**
     * Genera la fecha en formato mes-año
     * @return - fecha en formato correspondiente
     */
    private static String generateDate(){
        String formattedMonth = String.format("%02d", LocalDate.now().getMonthValue());
        String formattedYear = String.valueOf(LocalDate.now().getYear()).substring(2);
        return String.format("%s%s", formattedMonth, formattedYear);
    }

    /**
     * Extrae la fecha del documento ya creado anteriormente.
     * @param document - último documento con el formato creado
     * @return - fecha en formato mes-año
     */
    private static String extractDateDocument(String document){
        if (document == null || document.isBlank() || !document.contains("-")) return "0000";
        String[] parts = document.split("-");
        return parts[1];
    }

    /**
     * Extrae el número secuencial del documento ya creado anteriormente.
     * @param document - último documento con el formato creado
     * @return - número secuencial
     */
    private static String extractSequentialNumber(String document){
        String[] parts = document.split("-");
        return parts[2];
    }
}
