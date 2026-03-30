package com.example.easynotes.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class IndexControllerTest {

    @InjectMocks
    private IndexController indexController;

    private MockMvc mockMvc;

    private static final String EXPECTED_MESSAGE = """
            Hello and Welcome to the EasyNotes application. \
            You can create a new Note by making a POST request to /api/notes endpoint."""
            .replace(System.lineSeparator(), "");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(indexController)
                .build();
    }

    // -----------------------------------------------------------------------
    // Direct unit tests on the controller method
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("sayHello() returns the expected welcome message")
    void sayHello_returnsExpectedMessage() {
        String expectedMessage = "Hello and Welcome to the EasyNotes application. "
                + "You can create a new Note by making a POST request to /api/notes endpoint.";

        String actual = indexController.sayHello();

        assertThat(actual).isEqualTo(expectedMessage);
    }

    @Test
    @DisplayName("sayHello() result is not null")
    void sayHello_resultIsNotNull() {
        String result = indexController.sayHello();

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("sayHello() result is not blank")
    void sayHello_resultIsNotBlank() {
        String result = indexController.sayHello();

        assertThat(result).isNotBlank();
    }

    @Test
    @DisplayName("sayHello() result contains EasyNotes")
    void sayHello_resultContainsApplicationName() {
        String result = indexController.sayHello();

        assertThat(result).contains("EasyNotes");
    }

    @Test
    @DisplayName("sayHello() result contains the /api/notes endpoint hint")
    void sayHello_resultContainsApiNotesEndpoint() {
        String result = indexController.sayHello();

        assertThat(result).contains("/api/notes");
    }

    @Test
    @DisplayName("sayHello() result mentions POST request")
    void sayHello_resultMentionsPostRequest() {
        String result = indexController.sayHello();

        assertThat(result).containsIgnoringCase("POST");
    }

    // -----------------------------------------------------------------------
    // MockMvc / HTTP-layer tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("GET / returns HTTP 200")
    void getRoot_returnsHttp200() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET / returns plain text content type")
    void getRoot_returnsPlainTextContentType() throws Exception {
        mockMvc.perform(get("/").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }

    @Test
    @DisplayName("GET / returns the expected welcome body")
    void getRoot_returnsExpectedBody() throws Exception {
        String expectedBody = "Hello and Welcome to the EasyNotes application. "
                + "You can create a new Note by making a POST request to /api/notes endpoint.";

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedBody));
    }

    @Test
    @DisplayName("GET / with trailing slash returns HTTP 200")
    void getRoot_withAcceptJson_returnsOk() throws Exception {
        mockMvc.perform(get("/").accept(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST / is not mapped and returns 405 Method Not Allowed")
    void postRoot_returnsMethodNotAllowed() throws Exception {
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .post("/")
        ).andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("PUT / is not mapped and returns 405 Method Not Allowed")
    void putRoot_returnsMethodNotAllowed() throws Exception {
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .put("/")
        ).andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("DELETE / is not mapped and returns 405 Method Not Allowed")
    void deleteRoot_returnsMethodNotAllowed() throws Exception {
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/")
        ).andExpect(status().isMethodNotAllowed());
    }

    // -----------------------------------------------------------------------
    // Idempotency / consistency tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("sayHello() returns the same value on multiple invocations")
    void sayHello_isIdempotent() {
        String first  = indexController.sayHello();
        String second = indexController.sayHello();
        String third  = indexController.sayHello();

        assertThat(first).isEqualTo(second).isEqualTo(third);
    }
}
