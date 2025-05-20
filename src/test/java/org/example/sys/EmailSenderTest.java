package org.example.sys;

import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;
import javax.mail.AuthenticationFailedException;
import javax.mail.internet.AddressException;

import static org.junit.jupiter.api.Assertions.*;

class EmailSenderTest {

    private static final String VALID_FROM    = "your_test_account@gmail.com";
    private static final String VALID_PASSWORD = "wrong_password_for_test";
    private static final String VALID_TO      = "recipient@example.com";
    private static final String SUBJECT       = "Test Subject";
    private static final String BODY          = "Hello, this is a test.";

    @Test
    void testSendEmailWithInvalidCredentials() {
        // Expect authentication to fail when using wrong credentials
        MessagingException ex = assertThrows(MessagingException.class, () ->
                EmailSender.sendEmail(
                        VALID_TO,
                        VALID_FROM,
                        VALID_PASSWORD,
                        SUBJECT,
                        BODY
                )
        );
        // It may be an AuthenticationFailedException or contain auth failure text
        assertTrue(
                ex instanceof AuthenticationFailedException
                        || ex.getMessage().toLowerCase().contains("authentication"),
                () -> "Expected an authentication failure, but got: " + ex
        );
    }

    @Test
    void testSendEmailWithInvalidRecipientAddress() {
        // Passing a malformed recipient address should throw AddressException
        assertThrows(AddressException.class, () ->
                EmailSender.sendEmail(
                        "not-an-email",
                        VALID_FROM,
                        VALID_PASSWORD,
                        SUBJECT,
                        BODY
                )
        );
    }

    @Test
    void testSendEmailWithNullParameters() {
        // Null "to" address
        assertThrows(NullPointerException.class, () ->
                EmailSender.sendEmail(
                        null,
                        VALID_FROM,
                        VALID_PASSWORD,
                        SUBJECT,
                        BODY
                )
        );

        // Null "from" address
        assertThrows(NullPointerException.class, () ->
                EmailSender.sendEmail(
                        VALID_TO,
                        null,
                        VALID_PASSWORD,
                        SUBJECT,
                        BODY
                )
        );
    }
}
