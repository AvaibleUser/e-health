package org.ehealth.gatekeeper.service.util;

import static jakarta.mail.Message.RecipientType.TO;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
public class JavaMailServiceTest {

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private JavaMailService javaMailService;

    @Test
    void canSendTextEmail() {
        // given
        String toEmail = "this@is.boring";
        String subject = "this is a subject";
        String body = "this is a body";
        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setTo(toEmail);
        expectedMessage.setSubject(subject);
        expectedMessage.setText(body);

        // when
        javaMailService.sendTextEmail(toEmail, subject, body);

        // then
        then(mailSender).should().send(refEq(expectedMessage));
    }

    @Test
    void canSendHtmlEmail_WithAttachments() throws MessagingException {
        // given
        String company = "this is a company name";
        String toEmail = "this@is.boring";
        String subject = "this is a subject";
        String htmlContent = "<html></html>";
        File attachments = new File("this is a file");
        MimeMessage message = mock(MimeMessage.class);

        given(mailSender.createMimeMessage()).willReturn(message);

        // when
        javaMailService.sendHtmlEmail(company, toEmail, subject, htmlContent, attachments);

        // then
        then(message).should().setFrom(company);
        then(message).should().setRecipients(TO, toEmail);
        then(message).should().setSubject(subject);
        then(message).should().setContent(htmlContent, "text/html; charset=utf-8");
        then(mailSender).should().send(message);
    }
}
