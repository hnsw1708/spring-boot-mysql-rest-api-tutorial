package com.example.easynotes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EasyNotesApplicationTest {

    /**
     * Verifies that the main entry point delegates to SpringApplication.run
     * without throwing any exception.
     */
    @Test
    void main_shouldStartApplicationWithoutException() {
        // Arrange
        ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            springApplicationMock
                    .when(() -> SpringApplication.run(eq(EasyNotesApplication.class), any(String[].class)))
                    .thenReturn(mockContext);

            // Act & Assert
            assertThatCode(() -> EasyNotesApplication.main(new String[]{})).doesNotThrowAnyException();

            // Verify SpringApplication.run was called exactly once with the correct class
            springApplicationMock.verify(
                    () -> SpringApplication.run(eq(EasyNotesApplication.class), any(String[].class)),
                    times(1)
            );
        }
    }

    /**
     * Verifies that the main entry point works correctly when provided with
     * command-line arguments.
     */
    @Test
    void main_shouldPassCommandLineArgumentsToSpringApplication() {
        // Arrange
        var args = new String[]{"--server.port=8081", "--spring.profiles.active=test"};
        ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            springApplicationMock
                    .when(() -> SpringApplication.run(eq(EasyNotesApplication.class), any(String[].class)))
                    .thenReturn(mockContext);

            // Act & Assert
            assertThatCode(() -> EasyNotesApplication.main(args)).doesNotThrowAnyException();

            springApplicationMock.verify(
                    () -> SpringApplication.run(eq(EasyNotesApplication.class), eq(args)),
                    times(1)
            );
        }
    }

    /**
     * Verifies that the main entry point handles a null args array gracefully
     * by delegating directly to SpringApplication.run.
     */
    @Test
    void main_shouldHandleNullArgsWithoutException() {
        // Arrange
        ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            springApplicationMock
                    .when(() -> SpringApplication.run(eq(EasyNotesApplication.class), any()))
                    .thenReturn(mockContext);

            // Act & Assert — passing null should not cause an NPE in our code
            assertThatCode(() -> EasyNotesApplication.main(null)).doesNotThrowAnyException();
        }
    }

    /**
     * Verifies that the EasyNotesApplication class can be instantiated
     * (required by some Spring Boot bootstrap mechanisms).
     */
    @Test
    void constructor_shouldInstantiateWithoutException() {
        assertThatCode(EasyNotesApplication::new).doesNotThrowAnyException();
    }

    /**
     * Verifies that the application class carries the @SpringBootApplication
     * annotation, which is essential for component scanning and auto-configuration.
     */
    @Test
    void class_shouldBeAnnotatedWithSpringBootApplication() {
        boolean hasAnnotation = EasyNotesApplication.class
                .isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class);

        org.assertj.core.api.Assertions.assertThat(hasAnnotation)
                .as("EasyNotesApplication must carry @SpringBootApplication")
                .isTrue();
    }

    /**
     * Verifies that the application class carries the @EnableJpaAuditing
     * annotation, which activates JPA entity auditing.
     */
    @Test
    void class_shouldBeAnnotatedWithEnableJpaAuditing() {
        boolean hasAnnotation = EasyNotesApplication.class
                .isAnnotationPresent(org.springframework.data.jpa.repository.config.EnableJpaAuditing.class);

        org.assertj.core.api.Assertions.assertThat(hasAnnotation)
                .as("EasyNotesApplication must carry @EnableJpaAuditing")
                .isTrue();
    }
}
