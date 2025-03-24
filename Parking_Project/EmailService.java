package Parking_Project;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {
    public static void sendEmail(String recipient, int passCode) {
        final String senderEmail = "lohithparking10@gmail.com";
        final String senderPassword = "jxwu rwsi bnip sgse";

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Parking Slot Booking Confirmation");
            message.setText("Your booking is confirmed! Use this PassCode to free your parking slot: " + passCode);

            Transport.send(message);
            System.out.println("Email sent successfully to " + recipient);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
