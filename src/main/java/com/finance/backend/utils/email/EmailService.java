package com.finance.backend.utils.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String from;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String from) {

        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendActionEmail(
            String to,
            String subject,
            String title,
            String message,
            String buttonText,
            String actionUrl) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);

            String html = buildActionEmail(
                    title,
                    message,
                    buttonText,
                    actionUrl);

            helper.setText(html, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException exception) {
            throw new IllegalStateException(
                    "No fue posible enviar el correo",
                    exception);
        }
    }

    private String buildActionEmail(
            String title,
            String message,
            String buttonText,
            String actionUrl) {

        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                </head>

                <body style="
                    margin: 0;
                    padding: 0;
                    background-color: #f5f5f5;
                    font-family: Arial, Helvetica, sans-serif;
                    color: #171717;
                ">

                    <table
                        width="100%%"
                        cellpadding="0"
                        cellspacing="0"
                        style="
                            padding: 40px 16px;
                            background-color: #f5f5f5;
                        "
                    >
                        <tr>
                            <td align="center">

                                <table
                                    width="100%%"
                                    cellpadding="0"
                                    cellspacing="0"
                                    style="
                                        max-width: 560px;
                                        background-color: #ffffff;
                                        border: 1px solid #e5e5e5;
                                        border-radius: 16px;
                                        overflow: hidden;
                                    "
                                >

                                    <tr>
                                        <td
                                            align="center"
                                            style="
                                                padding: 32px 32px 16px 32px;
                                            "
                                        >
                                            <div style="
                                                font-size: 13px;
                                                font-weight: 700;
                                                letter-spacing: 3px;
                                                color: #737373;
                                            ">
                                                FINANCE
                                            </div>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td
                                            align="center"
                                            style="
                                                padding: 8px 32px 0 32px;
                                            "
                                        >
                                            <h1 style="
                                                margin: 0;
                                                font-size: 26px;
                                                line-height: 34px;
                                                color: #171717;
                                            ">
                                                %s
                                            </h1>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td
                                            align="center"
                                            style="
                                                padding: 16px 40px 8px 40px;
                                            "
                                        >
                                            <p style="
                                                margin: 0;
                                                font-size: 15px;
                                                line-height: 24px;
                                                color: #525252;
                                            ">
                                                %s
                                            </p>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td
                                            align="center"
                                            style="
                                                padding: 24px 32px;
                                            "
                                        >
                                            <a
                                                href="%s"
                                                style="
                                                    display: inline-block;
                                                    padding: 13px 24px;
                                                    background-color: #171717;
                                                    color: #ffffff;
                                                    text-decoration: none;
                                                    font-size: 14px;
                                                    font-weight: 600;
                                                    border-radius: 8px;
                                                "
                                            >
                                                %s
                                            </a>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td
                                            style="
                                                padding: 8px 40px 32px 40px;
                                            "
                                        >
                                            <p style="
                                                margin: 0 0 8px 0;
                                                font-size: 12px;
                                                line-height: 18px;
                                                color: #737373;
                                            ">
                                                Si el botón no funciona, copia y pega este enlace en tu navegador:
                                            </p>

                                            <p style="
                                                margin: 0;
                                                font-size: 12px;
                                                line-height: 18px;
                                                word-break: break-all;
                                            ">
                                                <a
                                                    href="%s"
                                                    style="
                                                        color: #404040;
                                                    "
                                                >
                                                    %s
                                                </a>
                                            </p>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="
                                            border-top: 1px solid #eeeeee;
                                            padding: 20px 32px;
                                            text-align: center;
                                        ">
                                            <p style="
                                                margin: 0;
                                                font-size: 11px;
                                                color: #a3a3a3;
                                            ">
                                                Este es un correo automático de Finance.
                                            </p>
                                        </td>
                                    </tr>

                                </table>

                            </td>
                        </tr>
                    </table>

                </body>
                </html>
                """.formatted(
                title,
                title,
                message,
                actionUrl,
                buttonText,
                actionUrl,
                actionUrl);
    }
}