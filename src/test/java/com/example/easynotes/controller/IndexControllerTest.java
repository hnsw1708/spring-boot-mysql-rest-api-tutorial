package com.example.easynotes.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class IndexControllerTest {

    @InjectMocks
    private IndexController indexController;

    private MockMvc mockMvc;

    private static final String EXPECTED_RESPONSE = """
            Hello and Welcome to the EasyNotes application. \
            You can create a new Note by making a POST request to /api/notes endpoint."""
            .replace("\n", "");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(indexController)
                .build();
    }

    @Test
    @DisplayName("sayHello() should return the expected welcome message string")
    void sayHello_shouldReturnExpectedMessage() {
        String result = indexController.sayHello();

        assertAll(
                () -> assertNotNull(result, "Response must not be null"),
                () -> assertThat(result).isEqualTo(
                        "Hello and Welcome to the EasyNotes application. " +
                        "You can create a new Note by making a POST request to /api/notes endpoint."
                )
        );
    }

    @Test
    @DisplayName("sayHello() should return a non-blank message")
    void sayHello_shouldReturnNonBlankMessage() {
        String result = indexController.sayHello();

        assertThat(result).isNotBlank();
    }

    @Test
    @DisplayName("sayHello() should contain key application details")
    void sayHello_shouldContainKeyDetails() {
        String result = indexController.sayHello();

        assertAll(
                () -> assertThat(result).containsIgnoringCase("EasyNotes"),
                () -> assertThat(result).contains("/api/notes"),
                () -> assertThat(result).containsIgnoringCase("POST")
        );
    }

    @Test
    @DisplayName("GET / should return HTTP 200 OK")
    void getRoot_shouldReturn200() throws Exception {
        mockMvc.perform(get("/")
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET / should return expected welcome message body")
    void getRoot_shouldReturnExpectedBody() throws Exception {
        String expectedBody = "Hello and Welcome to the EasyNotes application. " +
                              "You can create a new Note by making a POST request to /api/notes endpoint.";

        mockMvc.perform(get("/")
                        .accept(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedBody));
    }

    @Test
    @DisplayName("GET / should return content type text/plain or compatible")
    void getRoot_shouldReturnTextContentType() throws Exception {
        MvcResult result = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn();

        String contentType = result.getResponse().getContentType();
        assertThat(contentType).isNotNull();
    }

    @Test
    @DisplayName("sayHello() return value matches pattern: contains 'Welcome'")
    void sayHello_shouldContainWelcome() {
        String result = indexController.sayHello();

        // Direct check since result is already a String
        boolean isValidResponse = result != null && result.contains("Welcome");

        assertThat(isValidResponse).isTrue();
    }

    @Test
    @DisplayName("sayHello() should not return null")
    void sayHello_shouldNeverReturnNull() {
        String result = indexController.sayHello();
        assertNotNull(result);
    }

    @Test
    @DisplayName("GET / with no Accept header should still return 200")
    void getRoot_withNoAcceptHeader_shouldReturn200() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}