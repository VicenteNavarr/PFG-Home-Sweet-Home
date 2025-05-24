package homeSweetHome.utils;

import javax.mail.internet.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javafx.scene.control.Alert;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

    public static void main(String[] args) {
        System.out.println("Iniciando MailSender...");
        if (args.length < 2) {
            System.err.println("Error: Debes proporcionar la ruta del archivo y el correo del destinatario.");
            return;
        }
        String filePath = args[0];
        String recipientEmail = args[1];
        System.out.println("Parámetros recibidos:");
        System.out.println(" - Ruta del archivo: " + filePath);
        System.out.println(" - Email destinatario: " + recipientEmail);
        sendEmailWithShoppingList(recipientEmail, filePath);
    }

    /**
     * Envio mail lista compra
     * @param recipientEmail
     * @param filePath 
     */
    private static void sendEmailWithShoppingList(String recipientEmail, String filePath) {
        
        final String senderEmail = "apphomesweethome@gmail.com";
        final String senderPassword = "mskh lydf tzib vguo";
        System.out.println("Configurando servidor SMTP...");
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        System.out.println("Autenticando usuario...");
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        try {
            System.out.println("Leyendo contenido del archivo...");
            String fileContent = "";
            try {
                Path path = Paths.get(filePath);
                fileContent = Files.readString(path);
                System.out.println("Contenido leído correctamente:\n" + fileContent);
            } catch (Exception e) {
                System.err.println("Error leyendo el archivo: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            System.out.println("Creando mensaje de correo...");
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Lista de Compras");
            message.setText("Aquí está tu lista de compras:\n\n" + fileContent);
            System.out.println("Enviando el correo...");
            Transport.send(message);

            System.out.println("Correo enviado correctamente a: " + recipientEmail);
        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
