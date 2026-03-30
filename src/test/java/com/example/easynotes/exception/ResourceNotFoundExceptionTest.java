package com.example.easynotes.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResourceNotFoundException Tests")
class ResourceNotFoundExceptionTest {

    // ---------------------------------------------------------------------------
    // Helper record to group the three constructor arguments together (Java 21)
    // ---------------------------------------------------------------------------
    record ExceptionArgs(String resourceName, String fieldName, Object fieldValue) {}

    // ---------------------------------------------------------------------------
    // @ResponseStatus annotation checks
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("Class-level annotation")
    class AnnotationTests {

        @Test
        @DisplayName("@ResponseStatus value should be HttpStatus.NOT_FOUND")
        void shouldBeAnnotatedWithNotFoundStatus() {
            ResponseStatus annotation = ResourceNotFoundException.class
                    .getAnnotation(ResponseStatus.class);

            assertNotNull(annotation, "@ResponseStatus annotation must be present");
            assertEquals(HttpStatus.NOT_FOUND, annotation.value(),
                    "HTTP status must be 404 NOT_FOUND");
        }

        @Test
        @DisplayName("class must extend RuntimeException")
        void shouldExtendRuntimeException() {
            assertTrue(RuntimeException.class.isAssignableFrom(ResourceNotFoundException.class));
        }
    }

    // ---------------------------------------------------------------------------
    // Constructor & message formatting
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("Constructor and message formatting")
    class ConstructorTests {

        @Test
        @DisplayName("message is formatted correctly for typical string field value")
        void shouldFormatMessageCorrectlyForStringFieldValue() {
            var args = new ExceptionArgs("Note", "title", "My Note");
            var ex = new ResourceNotFoundException(args.resourceName(), args.fieldName(), args.fieldValue());

            String expectedMessage = "Note not found with title : 'My Note'";
            assertEquals(expectedMessage, ex.getMessage());
        }

        @Test
        @DisplayName("message is formatted correctly for numeric field value")
        void shouldFormatMessageCorrectlyForNumericFieldValue() {
            var ex = new ResourceNotFoundException("User", "id", 42L);

            assertEquals("User not found with id : '42'", ex.getMessage());
        }

        @Test
        @DisplayName("message is formatted correctly using text block expectation")
        void shouldFormatMessageUsingTextBlockExpectation() {
            var ex = new ResourceNotFoundException("Order", "orderId", 100);

            String expected = """
                    Order not found with orderId : '100'""".stripLeading();
            assertEquals(expected, ex.getMessage());
        }

        @Test
        @DisplayName("message is formatted correctly when fieldValue is null")
        void shouldFormatMessageWhenFieldValueIsNull() {
            var ex = new ResourceNotFoundException("Resource", "field", null);

            assertEquals("Resource not found with field : 'null'", ex.getMessage());
        }

        @Test
        @DisplayName("resourceName is stored correctly")
        void shouldStoreResourceName() {
            var ex = new ResourceNotFoundException("Note", "id", 1L);
            assertEquals("Note", ex.getResourceName());
        }

        @Test
        @DisplayName("fieldName is stored correctly")
        void shouldStoreFieldName() {
            var ex = new ResourceNotFoundException("Note", "id", 1L);
            assertEquals("id", ex.getFieldName());
        }

        @Test
        @DisplayName("fieldValue is stored correctly for Long")
        void shouldStoreFieldValueForLong() {
            var ex = new ResourceNotFoundException("Note", "id", 99L);
            assertEquals(99L, ex.getFieldValue());
        }

        @Test
        @DisplayName("fieldValue is stored correctly for String")
        void shouldStoreFieldValueForString() {
            var ex = new ResourceNotFoundException("Note", "title", "Hello");
            assertEquals("Hello", ex.getFieldValue());
        }

        @Test
        @DisplayName("fieldValue is stored correctly when null")
        void shouldStoreNullFieldValue() {
            var ex = new ResourceNotFoundException("Note", "id", null);
            assertNull(ex.getFieldValue());
        }
    }

    // ---------------------------------------------------------------------------
    // Null / empty inputs for String parameters
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("Null and empty String inputs")
    class NullAndEmptyInputTests {

        @Test
        @DisplayName("null resourceName is stored and reflected in message")
        void shouldHandleNullResourceName() {
            var ex = new ResourceNotFoundException(null, "id", 1L);

            assertNull(ex.getResourceName());
            assertTrue(ex.getMessage().startsWith("null not found with"),
                    "Message should start with 'null not found with'");
        }

        @Test
        @DisplayName("null fieldName is stored and reflected in message")
        void shouldHandleNullFieldName() {
            var ex = new ResourceNotFoundException("Note", null, 1L);

            assertNull(ex.getFieldName());
            assertTrue(ex.getMessage().contains("null"),
                    "Message should contain 'null' for field name");
        }

        @Test
        @DisplayName("empty resourceName is stored as empty string")
        void shouldHandleEmptyResourceName() {
            var ex = new ResourceNotFoundException("", "id", 1L);

            assertEquals("", ex.getResourceName());
            assertEquals(" not found with id : '1'", ex.getMessage());
        }

        @Test
        @DisplayName("empty fieldName is stored as empty string")
        void shouldHandleEmptyFieldName() {
            var ex = new ResourceNotFoundException("Note", "", 1L);

            assertEquals("", ex.getFieldName());
            assertEquals("Note not found with  : '1'", ex.getMessage());
        }
    }

    // ---------------------------------------------------------------------------
    // Exception is throwable / catchable
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("Exception throwability")
    class ThrowabilityTests {

        @Test
        @DisplayName("exception can be thrown and caught as RuntimeException")
        void shouldBeThrowableAsCaughtRuntimeException() {
            assertThrows(RuntimeException.class, () -> {
                throw new ResourceNotFoundException("Note", "id", 5L);
            });
        }

        @Test
        @DisplayName("exception can be thrown and caught as ResourceNotFoundException")
        void shouldBeThrowableAsCaughtResourceNotFoundException() {
            var ex = assertThrows(ResourceNotFoundException.class, () -> {
                throw new ResourceNotFoundException("Note", "id", 5L);
            });

            assertAll(
                    () -> assertEquals("Note", ex.getResourceName()),
                    () -> assertEquals("id", ex.getFieldName()),
                    () -> assertEquals(5L, ex.getFieldValue()),
                    () -> assertEquals("Note not found with id : '5'", ex.getMessage())
            );
        }

        @Test
        @DisplayName("pattern matching instanceof works with caught exception (Java 21)")
        void shouldSupportPatternMatchingInstanceof() {
            RuntimeException thrown = new ResourceNotFoundException("Tag", "name", "java");

            // Java 21 pattern matching
            if (thrown instanceof ResourceNotFoundException rnfe) {
                assertAll(
                        () -> assertEquals("Tag", rnfe.getResourceName()),
                        () -> assertEquals("name", rnfe.getFieldName()),
                        () -> assertEquals("java", rnfe.getFieldValue())
                );
            } else {
                fail("Expected ResourceNotFoundException but got " + thrown.getClass().getName());
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Parameterised tests for various resource / field combinations
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("Parameterised resource name tests")
    class ParameterisedTests {

        @ParameterizedTest(name = "resourceName=''{0}''")
        @ValueSource(strings = {"Note", "User", "Order", "Product", "Category"})
        @DisplayName("getResourceName returns the provided resource name")
        void shouldReturnResourceName(String resourceName) {
            var ex = new ResourceNotFoundException(resourceName, "id", 1L);
            assertEquals(resourceName, ex.getResourceName());
        }

        @ParameterizedTest(name = "fieldName=''{0}''")
        @ValueSource(strings = {"id", "email", "title", "slug", "uuid"})
        @DisplayName("getFieldName returns the provided field name")
        void shouldReturnFieldName(String fieldName) {
            var ex = new ResourceNotFoundException("Note", fieldName, 1L);
            assertEquals(fieldName, ex.getFieldName());
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("getFieldValue returns null when null is provided")
        void shouldReturnNullFieldValue(Object fieldValue) {
            var ex = new ResourceNotFoundException("Note", "id", fieldValue);
            assertNull(ex.getFieldValue());
        }
    }
}
