package homeSweetHome.utils;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class PasswordRecoveryMailSender {

    public static void main(String[] args) {
        System.out.println("Iniciando PasswordRecoveryMailSender...");
        if (args.length < 2) {
            System.err.println("Error: Debes proporcionar el correo del destinatario y la contraseña.");
            return;
        }
        String recipientEmail = args[0];
        String userPassword = args[1];

        System.out.println("Parámetros recibidos:");
        System.out.println(" - Email destinatario: " + recipientEmail);
        System.out.println(" - Contraseña del usuario: " + userPassword);

        sendPasswordRecoveryEmail(recipientEmail, userPassword);
    }

    /**
     * Envio mail con contraseña
     * @param recipientEmail
     * @param userPassword 
     */
    private static void sendPasswordRecoveryEmail(String recipientEmail, String userPassword) {
        
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
            System.out.println("Creando mensaje de correo...");
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Recuperación de Contraseña");
            message.setText("Tu contraseña es: " + userPassword + ". Recuerda cambiarla en ajustes si lo deseas.");

            System.out.println("Enviando el correo...");
            Transport.send(message);

            System.out.println("Correo de recuperación enviado correctamente a: " + recipientEmail);
        } catch (MessagingException e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
