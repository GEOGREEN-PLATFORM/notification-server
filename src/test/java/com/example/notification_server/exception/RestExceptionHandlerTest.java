package com.example.notification_server.exception;


import com.example.notification_server.util.ExceptionStringUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

import java.nio.file.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RestExceptionHandlerTest {

    private RestExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RestExceptionHandler();
    }

    @Test
    void handleIllegalArgumentException_returnsBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("bad arg");
        ResponseEntity<ApplicationError> resp = handler.handleIllegalArgumentExceptionException(ex);

        assertEquals(400, resp.getStatusCodeValue());
        ApplicationError body = resp.getBody();
        assertNotNull(body);
        assertEquals(ExceptionStringUtil.BAD_REQUEST_ERROR_TITLE, body.getTitle());
        assertEquals("bad arg", body.getMessage());
        assertNull(body.getMessages());
    }

    @Test
    void handleHttpMessageNotReadableException_returnsBadRequestWithFixedMessage() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("invalid json");
        ResponseEntity<ApplicationError> resp = handler.handleHttpMessageNotReadableException(ex);

        assertEquals(400, resp.getStatusCodeValue());
        ApplicationError body = resp.getBody();
        assertNotNull(body);
        assertEquals(ExceptionStringUtil.BAD_REQUEST_ERROR_TITLE, body.getTitle());
        assertEquals("Неверное тело запроса", body.getMessage());
    }

    @Test
    void handleEntityNotFoundException_returnsNotFound() {
        EntityNotFoundException ex = new EntityNotFoundException("not found");
        ResponseEntity<ApplicationError> resp = handler.handleNoResourceFoundException(ex);

        assertEquals(404, resp.getStatusCodeValue());
        ApplicationError body = resp.getBody();
        assertNotNull(body);
        assertEquals(ExceptionStringUtil.NOT_FOUND_ERROR_TITLE, body.getTitle());
        assertEquals("not found", body.getMessage());
    }

    @Test
    void handleConflictException_returnsConflict() {
        ConflictException ex = new ConflictException("already exists");
        ResponseEntity<ApplicationError> resp = handler.handleConflictException(ex);

        assertEquals(409, resp.getStatusCodeValue());
        ApplicationError body = resp.getBody();
        assertNotNull(body);
        assertEquals(ExceptionStringUtil.CONFLICT_ERROR_TITLE, body.getTitle());
        assertEquals("already exists", body.getMessage());
    }

    @Test
    void handleCustomAccessDeniedException_returnsForbidden() {
        CustomAccessDeniedException ex = new CustomAccessDeniedException("denied");
        ResponseEntity<ApplicationError> resp = handler.handleCustomAccessDeniedException(ex);

        assertEquals(403, resp.getStatusCodeValue());
        ApplicationError body = resp.getBody();
        assertNotNull(body);
        assertEquals(ExceptionStringUtil.FORBIDDEN_ERROR_TITLE, body.getTitle());
        assertEquals("denied", body.getMessage());
    }

    @Test
    void handleAccessDeniedException_returnsForbidden() throws Exception {
        AccessDeniedException ex = new AccessDeniedException("access denied");
        ResponseEntity<ApplicationError> resp = handler.handleAccessDeniedException(ex);

        assertEquals(403, resp.getStatusCodeValue());
        ApplicationError body = resp.getBody();
        assertNotNull(body);
        assertEquals(ExceptionStringUtil.FORBIDDEN_ERROR_TITLE, body.getTitle());
        assertEquals("access denied", body.getMessage());
    }

    @Test
    void handleUnauthorizedException_returnsUnauthorized() {
        InvalidBearerTokenException ex = new InvalidBearerTokenException("invalid token");
        ResponseEntity<ApplicationError> resp = handler.handleAuthenticationException(ex);

        assertEquals(401, resp.getStatusCodeValue());
        ApplicationError body = resp.getBody();
        assertNotNull(body);
        assertEquals(ExceptionStringUtil.UNAUTHORIZED_ERROR_TITLE, body.getTitle());
        assertEquals("invalid token", body.getMessage());
    }

    @Test
    void handleGenericException_returnsInternalServerError() {
        Exception ex = new Exception("oops");
        ResponseEntity<ApplicationError> resp = handler.handleException(ex);

        assertEquals(500, resp.getStatusCodeValue());
        ApplicationError body = resp.getBody();
        assertNotNull(body);
        assertEquals(ExceptionStringUtil.INTERNAL_SERVER_ERROR_ERROR_TITLE, body.getTitle());
        assertEquals("Внутренняя ошибка сервера", body.getMessage());
    }
}
