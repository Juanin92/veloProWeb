package com.veloproweb.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.veloproweb.model.dto.sale.SaleDetailResponseDTO;
import com.veloproweb.model.entity.sale.Sale;
import com.veloproweb.model.entity.data.LocalData;
import com.veloproweb.model.enums.PaymentMethod;
import com.veloproweb.service.sale.interfaces.ISaleDetailService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class PdfService {

   private final ISaleDetailService saleDetailService;
   private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

    /**
     * Genera un archivo PDF del comprobante de venta
     * @param sale - Objeto con los datos de la venta
     * @param companyData - Datos de la empresa
     * @return - Ruta del archivo PDF generado
     * @throws Exception - Excepción en caso de error al generar el PDF
     */
    public String generateSalesReceiptPDF(Sale sale, LocalData companyData) throws Exception {
        String filePath = "src/main/resources/static/pdf/boleta_" + sale.getDocument() + ".pdf";
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Fuente y tamaños
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

        // Título "BOLETA"
        Paragraph title = new Paragraph("BOLETA", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Número de boleta
        Paragraph receiptNumber = new Paragraph("N°: " + sale.getDocument().split("_")[1], boldFont);
        receiptNumber.setAlignment(Element.ALIGN_CENTER);
        document.add(receiptNumber);

        // Logo
        Image logo = Image.getInstance("src/main/resources/static/Img/principalLogo.png");
        logo.setAlignment(Image.ALIGN_RIGHT);
        logo.scaleToFit(100, 100);
        document.add(logo);

        // Datos principales
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        document.add(new Paragraph("Fecha: " + sale.getDate().format(dtf), normalFont));

        if (sale.getPaymentMethod().equals(PaymentMethod.PRESTAMO) || sale.getPaymentMethod().equals(PaymentMethod.MIXTO)) {
            document.add(new Paragraph("Cliente: " + sale.getCustomer(), normalFont));
        }

        document.add(new Paragraph("Forma de Pago: " + sale.getPaymentMethod(), normalFont));
        document.add(new Paragraph("Total: $" + sale.getTotalSale(), normalFont));
        document.add(new Paragraph("Descuento: $" + sale.getDiscount(), normalFont));
        document.add(new Paragraph("Impuesto: $" + sale.getTax(), normalFont));
        document.add(new Paragraph("Deuda: $" + (sale.getTotalSale() - sale.getDiscount()), normalFont));

        if (sale.getComment() != null && !sale.getComment().contains("null")) {
            document.add(new Paragraph("Comentario: " + sale.getComment(), normalFont));
        }

        // Espaciado
        document.add(new Paragraph(" "));

        // Tabla de productos
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 8, 3});

        table.addCell(new PdfPCell(new Phrase("Cantidad", boldFont)));
        table.addCell(new PdfPCell(new Phrase("Descripción", boldFont)));
        table.addCell(new PdfPCell(new Phrase("Precio", boldFont)));

        List<SaleDetailResponseDTO> detailList = saleDetailService.getDetailsBySaleId(sale.getId());
        for (SaleDetailResponseDTO item : detailList) {
            table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), normalFont)));
            table.addCell(new PdfPCell(new Phrase(item.getDescriptionProduct(), normalFont)));
            table.addCell(new PdfPCell(new Phrase("$" + item.getPrice(), normalFont)));
        }
        document.add(table);

        // Total general
        document.add(new Paragraph("Total General: $" + sale.getTotalSale(), boldFont));

        // Datos de la empresa
        if (companyData != null) {
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Teléfono: " + companyData.getPhone(), normalFont));
            document.add(new Paragraph("Email: " + companyData.getEmail(), normalFont));
            document.add(new Paragraph("Dirección: " + companyData.getAddress(), normalFont));
        }

        // Mensaje al final
        document.add(new Paragraph(" "));
        document.add(new Paragraph("*Esta no es una boleta válida como comprobante", normalFont));

        document.close();
        return filePath;
    }

    /**
     * Elimina el archivo PDF de la boleta generada
     * @param documentNumber - Número de documento de la boleta a eliminar
     */
    public void deleteSalesReceiptPDF(String documentNumber) {
        String filePath = "src/main/resources/static/pdf/boleta_" + documentNumber + ".pdf";
        Path path = Paths.get(filePath);

        try {
            Files.delete(path);
            logger.info("Archivo eliminado correctamente: {}", filePath);
        } catch (IOException e) {
            logger.warn("No se pudo eliminar el archivo: {}", filePath, e);
        }
    }
}
