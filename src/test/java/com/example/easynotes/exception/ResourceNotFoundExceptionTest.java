package com.example.easynotes.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ResourceNotFoundException Tests")
class ResourceNotFoundExceptionTest {

    // -------------------------------------------------------------------------
    // Helper factory
    // -------------------------------------------------------------------------

    private static ResourceNotFoundException create(String resourceName, String fieldName, Object fieldValue) {
        return new ResourceNotFoundException(resourceName, fieldName, fieldValue);
    }

    // -------------------------------------------------------------------------
    // @ResponseStatus annotation
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Class-level annotation")
    class AnnotationTests {

        @Test
        @DisplayName("Class should be annotated with @ResponseStatus(NOT_FOUND)")
        void shouldBeAnnotatedWithNotFoundStatus() {
            var annotation = ResourceNotFoundException.class.getAnnotation(ResponseStatus.class);

            assertThat(annotation).isNotNull();
            assertThat(annotation.value()).isEqualTo(HttpStatus.NOT_FOUND);
        }

        @Test
        @DisplayName("Class should extend RuntimeException")
        void shouldExtendRuntimeException() {
            var exception = create("Note", "id", 1L);

            assertThat(exception).isInstanceOf(RuntimeException.class);
        }
    }

    // -------------------------------------------------------------------------
    // Constructor / message formatting
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Constructor and message formatting")
    class ConstructorTests {

        @Test
        @DisplayName("Message should follow the pattern '<resource> not found with <field> : \'<value>\''")
        void shouldFormatMessageCorrectly() {
            var exception = create("Note", "id", 42L);

            assertThat(exception.getMessage()).isEqualTo("Note not found with id : '42'");
        }

        @Test
        @DisplayName("Message with String fieldValue should be formatted correctly")
        void shouldFormatMessageWithStringFieldValue() {
            var exception = create("User", "username", "john_doe");

            assertThat(exception.getMessage()).isEqualTo("User not found with username : 'john_doe'");
        }

        @Test
        @DisplayName("Message uses text-block expected value for multi-word resource")
        void shouldFormatMessageForMultiWordResource() {
            var expected = """
                    MyResource not found with email : 'test@example.com'""".stripLeading();

            var exception = create("MyResource", "email", "test@example.com");

            assertThat(exception.getMessage()).isEqualTo(expected);
        }

        @Test
        @DisplayName("Integer fieldValue should be converted to string in message")
        void shouldHandleIntegerFieldValue() {
            var exception = create("Product", "id", 99);

            assertThat(exception.getMessage()).contains("99");
        }

        @Test
        @DisplayName("Null fieldValue should appear as 'null' in message")
        void shouldHandleNullFieldValue() {
            var exception = create("Resource", "field", null);

            assertThat(exception.getMessage()).isEqualTo("Resource not found with field : 'null'");
        }

        @Test
        @DisplayName("Empty string values should be preserved in message")
        void shouldHandleEmptyStringValues() {
            var exception = create("", "", "");

            assertThat(exception.getMessage()).isEqualTo(" not found with  : ''");
        }
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Getter methods")
    class GetterTests {

        @Test
        @DisplayName("getResourceName() should return the value supplied in the constructor")
        void getResourceName_shouldReturnConstructorValue() {
            var exception = create("Note", "id", 1L);

            assertThat(exception.getResourceName()).isEqualTo("Note");
        }

        @Test
        @DisplayName("getFieldName() should return the value supplied in the constructor")
        void getFieldName_shouldReturnConstructorValue() {
            var exception = create("Note", "title", "My Note");

            assertThat(exception.getFieldName()).isEqualTo("title");
        }

        @Test
        @DisplayName("getFieldValue() should return the exact object supplied in the constructor")
        void getFieldValue_shouldReturnConstructorValue() {
            Object value = 123L;
            var exception = create("Note", "id", value);

            assertThat(exception.getFieldValue()).isSameAs(value);
        }

        @Test
        @DisplayName("getFieldValue() should support arbitrary object types")
        void getFieldValue_shouldSupportPatternMatching() {
            record NoteId(int id) {}
            var noteId = new NoteId(7);
            var exception = create("Note", "noteId", noteId);

            // Use instanceof pattern matching instead of switch pattern matching
            Object fieldValue = exception.getFieldValue();
            String description;
            if (fieldValue instanceof NoteId ni) {
                description = "NoteId with id=" + ni.id();
            } else if (fieldValue == null) {
                description = "null value";
            } else {
                description = "other";
            }

            assertThat(description).isEqualTo("NoteId with id=7");
        }

        @Test
        @DisplayName("getFieldValue() should return null when null was supplied")
        void getFieldValue_shouldReturnNullWhenNullSupplied() {
            var exception = create("Resource", "field", null);

            assertThat(exception.getFieldValue()).isNull();
        }

        @Test
        @DisplayName("getResourceName() should return null when null was supplied")
        void getResourceName_shouldReturnNullWhenNullSupplied() {
            var exception = create(null, "id", 1L);

            assertThat(exception.getResourceName()).isNull();
        }

        @Test
        @DisplayName("getFieldName() should return null when null was supplied")
        void getFieldName_shouldReturnNullWhenNullSupplied() {
            var exception = create("Note", null, 1L);

            assertThat(exception.getFieldName()).isNull();
        }
    }

    // -------------------------------------------------------------------------
    // Throwability
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Throwability")
    class ThrowabilityTests {

        @Test
        @DisplayName("Should be throwable and catchable as RuntimeException")
        void shouldBeThrowableAndCatchableAsRuntimeException() {
            var thrownException = assertThrows(RuntimeException.class, () -> {
                throw create("Note", "id", 5L);
            });

            assertThat(thrownException.getMessage()).isEqualTo("Note not found with id : '5'");
        }

        @Test
        @DisplayName("Should be throwable and catchable as ResourceNotFoundException")
        void shouldBeThrowableAndCatchableAsResourceNotFoundException() {
            var thrownException = assertThrows(ResourceNotFoundException.class, () -> {
                throw create("Tag", "name", "java");
            });

            assertAll(
                    () -> assertThat(thrownException.getResourceName()).isEqualTo("Tag"),
                    () -> assertThat(thrownException.getFieldName()).isEqualTo("name"),
                    () -> assertThat(thrownException.getFieldValue()).isEqualTo("java")
            );
        }
    }
}