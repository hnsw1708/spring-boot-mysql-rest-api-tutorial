package com.example.easynotes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EasyNotesApplicationTest {

    /**
     * Verifies that the EasyNotesApplication class can be instantiated without errors.
     * While the class is primarily a Spring Boot entry point, ensuring it is
     * instantiable is a basic sanity check.
     */
    @Test
    void applicationClassInstantiationShouldSucceed() {
        assertDoesNotThrow(() -> {
            EasyNotesApplication application = new EasyNotesApplication();
            assertNotNull(application, "EasyNotesApplication instance should not be null");
        });
    }

    /**
     * Verifies that the main method delegates to SpringApplication.run
     * with the correct application class and arguments.
     */
    @Test
    void mainMethodShouldInvokeSpringApplicationRun() {
        String[] args = {};

        try (MockedStatic<SpringApplication> mockedSpringApplication = mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext mockContext = Mockito.mock(ConfigurableApplicationContext.class);
            mockedSpringApplication
                    .when(() -> SpringApplication.run(eq(EasyNotesApplication.class), any(String[].class)))
                    .thenReturn(mockContext);

            assertDoesNotThrow(() -> EasyNotesApplication.main(args));

            mockedSpringApplication.verify(
                    () -> SpringApplication.run(eq(EasyNotesApplication.class), eq(args)),
                    times(1)
            );
        }
    }

    /**
     * Verifies that the main method handles non-empty args array correctly
     * and still delegates to SpringApplication.run.
     */
    @Test
    void mainMethodWithArgsShouldInvokeSpringApplicationRun() {
        String[] args = {"--server.port=8080", "--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> mockedSpringApplication = mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext mockContext = Mockito.mock(ConfigurableApplicationContext.class);
            mockedSpringApplication
                    .when(() -> SpringApplication.run(eq(EasyNotesApplication.class), any(String[].class)))
                    .thenReturn(mockContext);

            assertDoesNotThrow(() -> EasyNotesApplication.main(args));

            mockedSpringApplication.verify(
                    () -> SpringApplication.run(eq(EasyNotesApplication.class), eq(args)),
                    times(1)
            );
        }
    }

    /**
     * Verifies that the main method handles a null-equivalent empty args array
     * without throwing exceptions.
     */
    @Test
    void mainMethodWithEmptyArgsShouldNotThrow() {
        String[] emptyArgs = new String[0];

        try (MockedStatic<SpringApplication> mockedSpringApplication = mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext mockContext = Mockito.mock(ConfigurableApplicationContext.class);
            mockedSpringApplication
                    .when(() -> SpringApplication.run(eq(EasyNotesApplication.class), any(String[].class)))
                    .thenReturn(mockContext);

            assertDoesNotThrow(() -> EasyNotesApplication.main(emptyArgs));
        }
    }

    /**
     * Verifies that the @SpringBootApplication and @EnableJpaAuditing annotations
     * are present on the application class using Java 21 pattern matching for instanceof.
     */
    @Test
    void applicationClassShouldHaveRequiredAnnotations() {
        Class<?> appClass = EasyNotesApplication.class;

        var springBootAnnotation = appClass.getAnnotation(
                org.springframework.boot.autoconfigure.SpringBootApplication.class);
        var jpaAuditingAnnotation = appClass.getAnnotation(
                org.springframework.data.jpa.repository.config.EnableJpaAuditing.class);

        assertNotNull(springBootAnnotation,
                "@SpringBootApplication annotation should be present on EasyNotesApplication");
        assertNotNull(jpaAuditingAnnotation,
                "@EnableJpaAuditing annotation should be present on EasyNotesApplication");
    }
}
