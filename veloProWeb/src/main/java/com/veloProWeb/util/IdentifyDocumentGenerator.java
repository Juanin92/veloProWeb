package com.veloProWeb.util;

import java.time.LocalDate;

public class IdentifyDocumentGenerator {

    public static String generateIdentifyDocumentTicket(String document){
        return String.format("%s%s-%s", "T-", generateDate(), generateSequentialNumber(document));
    }

    public static String generateIdentifyDocumentSale(String document){
        return String.format("%s%s-%s", "BO-", generateDate(), generateSequentialNumber(document));
    }

    private static String generateSequentialNumber(String document) {
        String currentMonthYear = generateDate();
        String extractedDate = extractDateDocument(document);
        if (extractedDate.equals("0000")){
            return String.format("%04d", 1);
        }
        int number = Integer.parseInt(extractSequentialNumber(document));
        return String.format("%04d", currentMonthYear.equals(extractedDate) ? number + 1 : 1);
    }

    private static String generateDate(){
        String formattedMonth = String.format("%02d", LocalDate.now().getMonthValue());
        String formattedYear = String.valueOf(LocalDate.now().getYear()).substring(2);
        return String.format("%s%s", formattedMonth, formattedYear);
    }

    private static String extractDateDocument(String document){
        if (document == null || document.isBlank() || !document.contains("-")) return "0000";
        String[] parts = document.split("-");
        return parts[1];
    }

    private static String extractSequentialNumber(String document){
        String[] parts = document.split("-");
        return parts[2];
    }
}
