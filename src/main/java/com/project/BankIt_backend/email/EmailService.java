package com.project.BankIt_backend.email;

import com.project.BankIt_backend.common.exception.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;


    public void sendMailForOtp(String recipient, String otp) {

        try {

            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(recipient);
            mailMessage.setSubject("BankIt Password Reset OTP");

            mailMessage.setText(
                    """
                    Hello,
    
                    We received a request to reset your BankIt password.
    
                    Your One-Time Password (OTP) is: %s
    
                    This OTP is valid for 5 minutes.
    
                    Do not share this OTP with anyone. BankIt will never ask for your OTP.
    
                    If you did not request a password reset, simply ignore this email.
    
                    Thank you,
    
                    BankIt Security Team
                    """.formatted(otp)
            );

            javaMailSender.send(mailMessage);

        } catch (MailException ex) {

            throw new EmailSendingException(
                    "Unable to send OTP email."
            );
        }
    }

    // Send simple mail
    public String sendSimpleMail(EmailDetails details) {

        try {

            SimpleMailMessage mailMessage =
                    new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            javaMailSender.send(mailMessage);

            return "Mail Sent Successfully";

        } catch (Exception e) {

            return "Error while sending mail";
        }
    }

    // Send mail with attachment
    public String sendMailWithAttachment(
            EmailDetails details) {

        MimeMessage mimeMessage =
                javaMailSender.createMimeMessage();

        MimeMessageHelper helper;

        try {

            helper =
                    new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setText(details.getMsgBody());
            helper.setSubject(details.getSubject());

            FileSystemResource file =
                    new FileSystemResource(
                            new File(details.getAttachment()));

            helper.addAttachment(
                    file.getFilename(), file);

            javaMailSender.send(mimeMessage);

            return "Mail Sent Successfully";

        } catch (MessagingException e) {

            return "Error while sending mail";
        }
    }

}
