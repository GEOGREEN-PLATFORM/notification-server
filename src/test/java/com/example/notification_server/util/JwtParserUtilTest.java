package com.example.notification_server.util;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtParserUtilTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @InjectMocks
    private JwtParserUtil jwtParserUtil;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String RAW_TOKEN = "token123";
    private static final String FULL_TOKEN = BEARER_PREFIX + RAW_TOKEN;

    @Test
    void extractEmailFromJwt_validToken_returnsEmail() {
        String expectedEmail = "user@example.com";
        Jwt jwt = new Jwt(
                RAW_TOKEN,
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("email", expectedEmail)
        );
        when(jwtDecoder.decode(RAW_TOKEN)).thenReturn(jwt);

        String actual = jwtParserUtil.extractEmailFromJwt(FULL_TOKEN);

        assertEquals(expectedEmail, actual, "Should return the email claim from the token");
        verify(jwtDecoder).decode(RAW_TOKEN);
    }

    @Test
    void extractEmailFromJwt_missingEmailClaim_throwsException() {
        Jwt jwtWithoutEmail = new Jwt(
                RAW_TOKEN,
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("", "")
        );
        when(jwtDecoder.decode(RAW_TOKEN)).thenReturn(jwtWithoutEmail);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> jwtParserUtil.extractEmailFromJwt(FULL_TOKEN));
        assertEquals("Некорректное значение поля в токене: email", ex.getMessage());
        verify(jwtDecoder).decode(RAW_TOKEN);
    }

    @Test
    void extractEmailFromJwt_tokenWithoutBearerPrefix_throwsException() {
        String invalidTokenString = "InvalidPrefix";

        assertThrows(NullPointerException.class,
                () -> jwtParserUtil.extractEmailFromJwt(invalidTokenString));
    }
}

