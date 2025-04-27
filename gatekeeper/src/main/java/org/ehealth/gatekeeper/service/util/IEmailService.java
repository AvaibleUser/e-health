package org.ehealth.gatekeeper.service.util;

import java.io.File;

import jakarta.mail.MessagingException;

public interface IEmailService {

    void sendTextEmail(String toEmail, String subject, String body);

    void sendHtmlEmail(String companyName, String toEmail, String subject, String htmlContent, File... attachments)
            throws MessagingException;
}
