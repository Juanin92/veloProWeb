package com.veloProWeb.Utils;

import com.veloProWeb.Model.Entity.Customer.Customer;
import com.veloProWeb.Model.Entity.Customer.TicketHistory;
import com.veloProWeb.Model.Entity.Sale.Sale;
import com.veloProWeb.Model.Entity.User.LocalData;
import com.veloProWeb.Model.Entity.User.User;
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
    private LocalData localData;

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

    public void sendSalesReceiptEmail(Customer customer, Sale sale, String filePath) {
        String to = customer.getEmail();
        String subject = "Confirmación de Venta - " + sale.getDocument();
        String text = "Hola " + customer.getName() + ",\n\n" +
                "Se ha generado una venta con el documento " + sale.getDocument() +
                " mediante el método de préstamo por un total de $" + sale.getTotalSale() + "." +
                "\n\nAgradecemos su compra. Adjuntamos la boleta de su compra para su referencia." +
                "\n\n¡Gracias por elegirnos!";

        if (!"x@x.xxx".equals(to)) {
            try {
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
            } catch (Exception e) {
                throw new IllegalArgumentException("No fue posible enviar el correo: " + e.getMessage());
            }
        }
    }

    public void sendPasswordResetCode(User user, String code) {
        String to = user.getEmail();
        String subject = "Instrucciones para Restablecer tu Contraseña";
        String text = "Hola " + user.getName() + ",\n\n" +
                "Hemos recibido una solicitud para restablecer tu contraseña. " +
                "Por favor, utiliza el siguiente código para proceder:\n\n" +
                code + "\n\n" +
                "Ingresa este código en el campo correspondiente para actualizar tu contraseña." +
                "\nSi no solicitaste este cambio, puedes ignorar este mensaje." +
                "\n\n¡Gracias!";

        sendSimpleEmail(to, subject, text);
    }

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

    private LocalData getLocalData() {
        List<LocalData> list = localDataService.getData();
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron datos locales.");
        }
        return list.getFirst();
    }
}

