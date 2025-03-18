package com.jonathanfoucher.securityexample.controllers;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jonathanfoucher.securityexample.controllers.advisers.CustomResponseEntityExceptionHandler;
import com.jonathanfoucher.securityexample.data.dto.UserDto;
import com.jonathanfoucher.securityexample.mocks.MockedAuthentication;
import com.jonathanfoucher.securityexample.services.SecurityService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@SpringJUnitConfig({AuthController.class, SecurityService.class, CustomResponseEntityExceptionHandler.class})
@EnableMethodSecurity
class AuthControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private AuthController authController;
    @Autowired
    @MockitoSpyBean
    private SecurityService securityService;
    @Autowired
    private CustomResponseEntityExceptionHandler customResponseEntityExceptionHandler;

    private static final Logger log = (Logger) LoggerFactory.getLogger(AuthController.class);
    private static final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();

    private static final String UNAUTHENTICATED_PATH = "/unauthenticated";
    private static final String AUTHENTICATED_PATH = "/authenticated";
    private static final String ADMIN_PATH = "/admin";
    private static final String AUTHORIZED_JOB_PATH = "/authorized_job";
    private static final String UUID_PATH = "/uuid/{uuid}";

    private static final Pattern TIMESTAMP_REGEX_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$");
    private static final String DEFAULT_TYPE = "about:blank";

    private static final String UUID_VALUE = "d0418816-a0f2-4d78-822a-7618403be312";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String JOB = "IT engineer";

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(customResponseEntityExceptionHandler)
                .setMessageConverters()
                .build();

        listAppender.list.clear();
        listAppender.start();
        log.addAppender(listAppender);
    }

    @AfterEach
    void reset() {
        log.detachAppender(listAppender);
        listAppender.stop();
    }

    @Test
    void getOnUnauthenticatedPath() throws Exception {
        // WHEN / THEN
        mockMvc.perform(get(UNAUTHENTICATED_PATH))
                .andExpect(status().isOk());

        verify(securityService, never()).isAuthorizedJob(any());
        verify(securityService, never()).isUuidEquals(any(), any());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertNotNull(logsList.getFirst());
        assertEquals(Level.INFO, logsList.getFirst().getLevel());
        assertEquals("Unauthenticated request received", logsList.getFirst().getFormattedMessage());
    }

    @Test
    @MockedAuthentication
    void getOnAuthenticatedPath() throws Exception {
        // WHEN / THEN
        mockMvc.perform(get(AUTHENTICATED_PATH))
                .andExpect(status().isOk());

        verify(securityService, never()).isAuthorizedJob(any());
        verify(securityService, never()).isUuidEquals(any(), any());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertNotNull(logsList.getFirst());
        assertEquals(Level.INFO, logsList.getFirst().getLevel());
        assertEquals("Authenticated request received", logsList.getFirst().getFormattedMessage());
    }

    @Test
    @MockedAuthentication(roles = "ADMIN")
    void getOnAdminPath() throws Exception {
        // WHEN / THEN
        mockMvc.perform(get(ADMIN_PATH))
                .andExpect(status().isOk());

        verify(securityService, never()).isAuthorizedJob(any());
        verify(securityService, never()).isUuidEquals(any(), any());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertNotNull(logsList.getFirst());
        assertEquals(Level.INFO, logsList.getFirst().getLevel());
        assertEquals("Admin request received", logsList.getFirst().getFormattedMessage());
    }

    @Test
    @MockedAuthentication
    void getOnAdminPathWithoutAdminRole() throws Exception {
        // WHEN / THEN
        mockMvc.perform(get(ADMIN_PATH))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type", equalTo(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.title", equalTo(FORBIDDEN.getReasonPhrase())))
                .andExpect(jsonPath("$.status", equalTo(FORBIDDEN.value())))
                .andExpect(jsonPath("$.detail", equalTo("Access Denied")))
                .andExpect(jsonPath("$.instance", equalTo("uri=/admin")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.timestamp", matchesPattern(TIMESTAMP_REGEX_PATTERN)));

        verify(securityService, never()).isAuthorizedJob(any());
        verify(securityService, never()).isUuidEquals(any(), any());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }

    @Test
    @MockedAuthentication(uuid = UUID_VALUE, firstName = FIRST_NAME, lastName = LAST_NAME, job = "developer")
    void getOnAuthorizedJob() throws Exception {
        // WHEN / THEN
        mockMvc.perform(get(AUTHORIZED_JOB_PATH))
                .andExpect(status().isOk());

        ArgumentCaptor<UserDto> capturedUser = ArgumentCaptor.forClass(UserDto.class);
        verify(securityService, times(1)).isAuthorizedJob(capturedUser.capture());
        verify(securityService, never()).isUuidEquals(any(), any());

        UserDto user = capturedUser.getValue();
        assertNotNull(user);
        assertEquals(UUID.fromString(UUID_VALUE), user.getUuid());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals("developer", user.getJob());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertNotNull(logsList.getFirst());
        assertEquals(Level.INFO, logsList.getFirst().getLevel());
        assertEquals("Authorized job request received", logsList.getFirst().getFormattedMessage());
    }

    @Test
    @MockedAuthentication(uuid = UUID_VALUE, firstName = FIRST_NAME, lastName = LAST_NAME, job = JOB)
    void getOnAuthorizedJobWithoutAuthorizedJob() throws Exception {
        // WHEN / THEN
        mockMvc.perform(get(AUTHORIZED_JOB_PATH))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type", equalTo(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.title", equalTo(FORBIDDEN.getReasonPhrase())))
                .andExpect(jsonPath("$.status", equalTo(FORBIDDEN.value())))
                .andExpect(jsonPath("$.detail", equalTo("Access Denied")))
                .andExpect(jsonPath("$.instance", equalTo("uri=/authorized_job")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.timestamp", matchesPattern(TIMESTAMP_REGEX_PATTERN)));

        ArgumentCaptor<UserDto> capturedUser = ArgumentCaptor.forClass(UserDto.class);
        verify(securityService, times(1)).isAuthorizedJob(capturedUser.capture());
        verify(securityService, never()).isUuidEquals(any(), any());

        UserDto user = capturedUser.getValue();
        assertNotNull(user);
        assertEquals(UUID.fromString(UUID_VALUE), user.getUuid());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals(JOB, user.getJob());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }

    @Test
    @MockedAuthentication(uuid = UUID_VALUE, firstName = FIRST_NAME, lastName = LAST_NAME, job = JOB)
    void getOnUuid() throws Exception {
        // WHEN / THEN
        mockMvc.perform(get(UUID_PATH, UUID_VALUE))
                .andExpect(status().isOk());

        ArgumentCaptor<UserDto> capturedUser = ArgumentCaptor.forClass(UserDto.class);
        verify(securityService, never()).isAuthorizedJob(any());
        verify(securityService, times(1)).isUuidEquals(capturedUser.capture(), eq(UUID.fromString(UUID_VALUE)));

        UserDto user = capturedUser.getValue();
        assertNotNull(user);
        assertEquals(UUID.fromString(UUID_VALUE), user.getUuid());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals(JOB, user.getJob());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(1, logsList.size());
        assertNotNull(logsList.getFirst());
        assertEquals(Level.INFO, logsList.getFirst().getLevel());
        assertEquals("Uuid d0418816-a0f2-4d78-822a-7618403be312 matching requested uuid d0418816-a0f2-4d78-822a-7618403be312", logsList.getFirst().getFormattedMessage());
    }

    @Test
    @MockedAuthentication(uuid = UUID_VALUE, firstName = FIRST_NAME, lastName = LAST_NAME, job = JOB)
    void getOnUuidWithWrongUuid() throws Exception {
        // WHEN / THEN
        mockMvc.perform(get(UUID_PATH, "9b38e5b2-4422-40ca-9e3b-64271c89c5a9"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type", equalTo(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.title", equalTo(FORBIDDEN.getReasonPhrase())))
                .andExpect(jsonPath("$.status", equalTo(FORBIDDEN.value())))
                .andExpect(jsonPath("$.detail", equalTo("Access Denied")))
                .andExpect(jsonPath("$.instance", equalTo("uri=/uuid/9b38e5b2-4422-40ca-9e3b-64271c89c5a9")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.timestamp", matchesPattern(TIMESTAMP_REGEX_PATTERN)));

        ArgumentCaptor<UserDto> capturedUser = ArgumentCaptor.forClass(UserDto.class);
        verify(securityService, never()).isAuthorizedJob(any());
        verify(securityService, times(1)).isUuidEquals(capturedUser.capture(), eq(UUID.fromString("9b38e5b2-4422-40ca-9e3b-64271c89c5a9")));

        UserDto user = capturedUser.getValue();
        assertNotNull(user);
        assertEquals(UUID.fromString(UUID_VALUE), user.getUuid());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals(JOB, user.getJob());

        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(0, logsList.size());
    }
}
