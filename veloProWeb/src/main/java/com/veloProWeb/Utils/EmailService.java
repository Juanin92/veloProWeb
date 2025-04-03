package com.veloProWeb.Utils;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Entity.User.LocalData;
import com.veloProWeb.Model.Entity.User.User;
import com.veloProWeb.Model.Enum.PaymentMethod;
import com.veloProWeb.Service.User.Interface.ILocalDataService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired private ILocalDataService localDataService;
    @Autowired private PdfService pdfService;
    private LocalData localData;

    /*
     * Configura el JavaMailSender para enviar correos electrónicos.
     * @return JavaMailSenderImpl configurado.
     */
    private JavaMailSenderImpl getJavaMailSender() {
        localData = getLocalData();
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(localData.getEmail());
        mailSender.setPassword(localData.getEmailSecurityApp());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    public void sendOverdueTicketNotification(Customer customer, TicketHistory ticket) {
        String to = customer.getEmail();
        String subject = "Aviso: Su ticket ha vencido";
        String text = "Hola " + customer.getName() + ",\n\n" +
                "Te recordamos que tu boleta " + ticket.getDocument() + " ha vencido." +
                "\nFecha de compra: " + ticket.getDate() + "." +
                "\n\nPor favor, póngase en contacto con nosotros para realizar el pago." +
                "\n\n¡Gracias por su atención!";

        sendSimpleEmail(to, subject, text);
    }

    /**
     * Envía un correo electrónico al cliente con la boleta de venta.
     * @param sale la venta que se va a enviar por correo.
     */
    public void sendSalesReceiptEmail(Sale sale) {
        if (sale.getPaymentMethod() == PaymentMethod.MIXTO || sale.getPaymentMethod() == PaymentMethod.PRESTAMO){
            String to = sale.getCustomer().getEmail();
            String subject = "Confirmación de Venta - " + sale.getDocument();
            String text = generateTextEmail(sale);

            if (!"x@x.xxx".equals(to)) {
                try {
                    // Generar el PDF antes de enviarlo
                    localData = getLocalData();
                    String filePath = pdfService.generateSalesReceiptPDF(sale, localData);

                    JavaMailSenderImpl mailSender = getJavaMailSender();
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true);

                    helper.setFrom(localData.getEmail());
                    helper.setTo(to);
                    helper.setSubject(subject);
                    helper.setText(text);

                    FileSystemResource file = new FileSystemResource(filePath);
                    if (file.exists()) {
                        helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
                    } else {
                        throw new IllegalArgumentException("El archivo PDF no existe: " + filePath);
                    }

                    mailSender.send(message);
                    pdfService.deleteSalesReceiptPDF(sale.getDocument());
                } catch (Exception e) {
                    pdfService.deleteSalesReceiptPDF(sale.getDocument());
                    throw new IllegalArgumentException("No fue posible enviar el correo: " + e.getMessage());
                }
            }
        }
    }

    /*
     * Envía un email al usuario con instrucciones para restablecer su contraseña.
     * @param user el usuario al que se le enviará el correo.
     * @param code el código de restablecimiento de contraseña.
     */
    public void sendPasswordResetCode(User user, String code) {
        String to = user.getEmail();
        String subject = "Instrucciones para Restablecer tu Contraseña";
        String text = String.format("""
                Hola %s,

                Hemos recibido una solicitud para restablecer tu contraseña. \
                Por favor, utiliza el siguiente código momentáneo para proceder:

                %s

                Este código es válido solo por 3 horas. \
                Al ingresar, deberás cambiar tu contraseña a una nueva, utilizando este código como tu contraseña actual.\

                Si no solicitaste este cambio, puedes ignorar este mensaje.\


                ¡Gracias!""", user.getName(), code);

        sendSimpleEmail(to, subject, text);
    }

    /**
     * Envía un correo electrónico simple.
     * @param to      destinatario del correo.
     * @param subject asunto del correo.
     * @param text    cuerpo del correo.
     */
    private void sendSimpleEmail(String to, String subject, String text) {
        try {
            JavaMailSenderImpl mailSender = getJavaMailSender();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalArgumentException("No fue posible enviar el correo: " + e.getMessage());
        }
    }

    /**
     * Obtiene los datos locales necesarios para la configuración del correo.
     *
     * @return LocalData objeto con los datos locales.
     * @throws IllegalArgumentException si no se encuentran datos locales.
     */
    private LocalData getLocalData() {
        List<LocalData> list = localDataService.getDataToEmail();
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron datos locales.");
        }
        return list.getFirst();
    }

    /*
     * Genera el texto del correo electrónico según el método de pago de la venta.
     * @param sale la venta para la cual se generará el texto del correo.
     * @return el texto del correo electrónico.
     */
    private String generateTextEmail(Sale sale) {
        switch (sale.getPaymentMethod()) {
            case PRESTAMO:
                return String.format(
                        """
                                Hola %s,

                                Se ha generado una venta con el documento %s \
                                mediante el método de préstamo por un total de $%d.

                                Agradecemos su compra. Adjuntamos la boleta de su compra para su referencia.

                                ¡Gracias por elegirnos!""",
                        sale.getCustomer().getName(), sale.getDocument(), sale.getTotalSale());
            case MIXTO:
                int amountPaid = extractAmountPaid(sale.getComment());
                int totalDebt = sale.getTotalSale() + amountPaid;
                return String.format(
                        """
                                Hola %s,

                                Se ha generado una venta con el documento %s \
                                mediante el método mixto por un total de $%d.

                                Monto abonado: $%d
                                Deuda restante: $%d

                                Agradecemos su compra. Adjuntamos la boleta de su compra para su referencia.

                                ¡Gracias por elegirnos!""",
                        sale.getCustomer().getName(), sale.getDocument(), totalDebt,
                        amountPaid, sale.getTotalSale());
            default:
                return "";
        }
    }

    /*
     * Extrae la cantidad pagada del comentario de la venta.
     * @param comment el comentario de la venta.
     * @return la cantidad pagada extraída, o 0 si no se encuentra.
     */
    private int extractAmountPaid(String comment) {
        if (comment == null || !comment.matches(".*\\$\\d+.*")) {
            return 0;
        }
        return Integer.parseInt(comment.replaceAll("[^0-9]", ""));
    }
}

