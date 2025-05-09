package com.veloProWeb.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IdentifyDocumentGeneratorTest {

    // Prueba para generar identificador para un ticket
    @Test
    public void generateIdentifyDocumentTicket_valid(){
        String document = "T-0525-0001";
        String result = IdentifyDocumentGenerator.generateIdentifyDocumentTicket(document);
        assertEquals("T-0525-0002", result);
    }
    @Test
    public void generateIdentifyDocumentTicket_FirstDocument(){
        String result = IdentifyDocumentGenerator.generateIdentifyDocumentTicket(null);
        assertEquals("T-0525-0001", result);
    }
    @ParameterizedTest
    @ValueSource(strings = {"T_21", "TicketTest01"})
    public void generateIdentifyDocumentTicket_DifferentFormatDocument(String document){
        String result = IdentifyDocumentGenerator.generateIdentifyDocumentTicket(document);
        assertEquals("T-0525-0001", result);
    }

    // Prueba para generar identificador para una boleta de venta
    @Test
    public void generateIdentifyDocumentSale_valid(){
        String document = "BO-0525-0001";
        String result = IdentifyDocumentGenerator.generateIdentifyDocumentSale(document);
        assertEquals("BO-0525-0002", result);
    }
    @Test
    public void generateIdentifyDocumentSale_FirstDocument(){
        String result = IdentifyDocumentGenerator.generateIdentifyDocumentSale(null);
        assertEquals("BO-0525-0001", result);
    }
    @ParameterizedTest
    @ValueSource(strings = {"BO_21", "TicketTest01"})
    public void generateIdentifyDocumentSale_DifferentFormatDocument(String document){
        String result = IdentifyDocumentGenerator.generateIdentifyDocumentSale(document);
        assertEquals("BO-0525-0001", result);
    }
}
