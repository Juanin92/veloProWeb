package com.veloproweb.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class IdentifyDocumentGeneratorTest {

    String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MMyy"));

    // Prueba para generar identificador para un ticket
    @Test
    void generateIdentifyDocumentTicket_valid(){
        String document = String.format("T-%s-0001",date);
        String result = IdentifyDocumentGenerator.generateIdentifyDocumentTicket(document);
        assertEquals(String.format("T-%s-0002",date), result);
    }
    @Test
    void generateIdentifyDocumentTicket_FirstDocument(){
        String result = IdentifyDocumentGenerator.generateIdentifyDocumentTicket(null);
        assertEquals(String.format("T-%s-0001",date), result);
    }
    @ParameterizedTest
    @ValueSource(strings = {"T_21", "TicketTest01"})
    void generateIdentifyDocumentTicket_DifferentFormatDocument(String document){
        String result = IdentifyDocumentGenerator.generateIdentifyDocumentTicket(document);
        assertEquals(String.format("T-%s-0001",date), result);
    }

    // Prueba para generar identificador para una boleta de venta
    @Test
    void generateIdentifyDocumentSale_valid(){
        String document = String.format("BO-%s-0001",date);
        String result = IdentifyDocumentGenerator.generateIdentifyDocumentSale(document);
        assertEquals(String.format("BO-%s-0002",date), result);
    }
    @Test
    void generateIdentifyDocumentSale_FirstDocument(){
        String result = IdentifyDocumentGenerator.generateIdentifyDocumentSale(null);
        assertEquals(String.format("BO-%s-0001",date), result);
    }
    @ParameterizedTest
    @ValueSource(strings = {"BO_21", "TicketTest01"})
    void generateIdentifyDocumentSale_DifferentFormatDocument(String document){
        String result = IdentifyDocumentGenerator.generateIdentifyDocumentSale(document);
        assertEquals(String.format("BO-%s-0001",date), result);
    }
}
