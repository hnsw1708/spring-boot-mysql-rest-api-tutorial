package com.example.easynotes.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Note Model Tests")
class NoteTest {

    private Note note;

    @BeforeEach
    void setUp() {
        note = new Note();
    }

    // ---------------------------------------------------------------------------
    // ID
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("id field")
    class IdField {

        @Test
        @DisplayName("getId returns null when not set")
        void getId_returnsNullByDefault() {
            assertNull(note.getId());
        }

        @Test
        @DisplayName("setId / getId round-trip with positive value")
        void setAndGetId_positiveValue() {
            note.setId(42L);
            assertEquals(42L, note.getId());
        }

        @Test
        @DisplayName("setId / getId round-trip with null")
        void setAndGetId_null() {
            note.setId(99L);
            note.setId(null);
            assertNull(note.getId());
        }

        @Test
        @DisplayName("setId accepts Long.MAX_VALUE")
        void setAndGetId_maxLong() {
            note.setId(Long.MAX_VALUE);
            assertEquals(Long.MAX_VALUE, note.getId());
        }
    }

    // ---------------------------------------------------------------------------
    // Title
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("title field")
    class TitleField {

        @Test
        @DisplayName("getTitle returns null when not set")
        void getTitle_returnsNullByDefault() {
            assertNull(note.getTitle());
        }

        @Test
        @DisplayName("setTitle / getTitle round-trip with normal string")
        void setAndGetTitle_normalString() {
            note.setTitle("My First Note");
            assertEquals("My First Note", note.getTitle());
        }

        @Test
        @DisplayName("setTitle / getTitle round-trip with null")
        void setAndGetTitle_null() {
            note.setTitle("some title");
            note.setTitle(null);
            assertNull(note.getTitle());
        }

        @Test
        @DisplayName("setTitle / getTitle with empty string")
        void setAndGetTitle_emptyString() {
            // @NotBlank is a Bean Validation constraint; the model itself still stores the value
            note.setTitle("");
            assertEquals("", note.getTitle());
        }

        @Test
        @DisplayName("setTitle / getTitle with text block (Java 21)")
        void setAndGetTitle_textBlock() {
            var multiLine = """
                    Shopping List
                    """;
            note.setTitle(multiLine.strip());
            assertEquals("Shopping List", note.getTitle());
        }

        @Test
        @DisplayName("setTitle / getTitle with special characters")
        void setAndGetTitle_specialCharacters() {
            var special = "Title with \"quotes\" & <symbols> 日本語";
            note.setTitle(special);
            assertEquals(special, note.getTitle());
        }
    }

    // ---------------------------------------------------------------------------
    // Content
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("content field")
    class ContentField {

        @Test
        @DisplayName("getContent returns null when not set")
        void getContent_returnsNullByDefault() {
            assertNull(note.getContent());
        }

        @Test
        @DisplayName("setContent / getContent round-trip with normal string")
        void setAndGetContent_normalString() {
            note.setContent("Some note content.");
            assertEquals("Some note content.", note.getContent());
        }

        @Test
        @DisplayName("setContent / getContent round-trip with null")
        void setAndGetContent_null() {
            note.setContent("initial");
            note.setContent(null);
            assertNull(note.getContent());
        }

        @Test
        @DisplayName("setContent / getContent with empty string")
        void setAndGetContent_emptyString() {
            note.setContent("");
            assertEquals("", note.getContent());
        }

        @Test
        @DisplayName("setContent / getContent with large text block (Java 21)")
        void setAndGetContent_largeTextBlock() {
            var largeContent = """
                    Line 1: Introduction
                    Line 2: Body paragraph with details.
                    Line 3: Conclusion.
                    """;
            note.setContent(largeContent);
            assertTrue(note.getContent().contains("Line 1"));
            assertTrue(note.getContent().contains("Line 3"));
        }
    }

    // ---------------------------------------------------------------------------
    // createdAt
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("createdAt field")
    class CreatedAtField {

        @Test
        @DisplayName("getCreatedAt returns null when not set")
        void getCreatedAt_returnsNullByDefault() {
            assertNull(note.getCreatedAt());
        }

        @Test
        @DisplayName("setCreatedAt / getCreatedAt round-trip")
        void setAndGetCreatedAt_normalDate() {
            var now = new Date();
            note.setCreatedAt(now);
            assertEquals(now, note.getCreatedAt());
        }

        @Test
        @DisplayName("setCreatedAt / getCreatedAt round-trip with null")
        void setAndGetCreatedAt_null() {
            note.setCreatedAt(new Date());
            note.setCreatedAt(null);
            assertNull(note.getCreatedAt());
        }

        @Test
        @DisplayName("getCreatedAt returns the exact same Date instance that was set")
        void getCreatedAt_returnsSameInstance() {
            var date = new Date(1_000_000L);
            note.setCreatedAt(date);
            assertSame(date, note.getCreatedAt());
        }
    }

    // ---------------------------------------------------------------------------
    // updatedAt
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("updatedAt field")
    class UpdatedAtField {

        @Test
        @DisplayName("getUpdatedAt returns null when not set")
        void getUpdatedAt_returnsNullByDefault() {
            assertNull(note.getUpdatedAt());
        }

        @Test
        @DisplayName("setUpdatedAt / getUpdatedAt round-trip")
        void setAndGetUpdatedAt_normalDate() {
            var now = new Date();
            note.setUpdatedAt(now);
            assertEquals(now, note.getUpdatedAt());
        }

        @Test
        @DisplayName("setUpdatedAt / getUpdatedAt round-trip with null")
        void setAndGetUpdatedAt_null() {
            note.setUpdatedAt(new Date());
            note.setUpdatedAt(null);
            assertNull(note.getUpdatedAt());
        }

        @Test
        @DisplayName("getUpdatedAt returns the exact same Date instance that was set")
        void getUpdatedAt_returnsSameInstance() {
            var date = new Date(2_000_000L);
            note.setUpdatedAt(date);
            assertSame(date, note.getUpdatedAt());
        }

        @Test
        @DisplayName("updatedAt can be set independently from createdAt")
        void updatedAt_independentFromCreatedAt() {
            var created = new Date(1_000L);
            var updated = new Date(2_000L);
            note.setCreatedAt(created);
            note.setUpdatedAt(updated);

            assertNotEquals(note.getCreatedAt(), note.getUpdatedAt());
            assertEquals(created, note.getCreatedAt());
            assertEquals(updated, note.getUpdatedAt());
        }
    }

    // ---------------------------------------------------------------------------
    // Full object state
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("Full Note state")
    class FullNoteState {

        @Test
        @DisplayName("All fields can be set and retrieved in one note")
        void allFields_setAndGet() {
            var created = new Date(500L);
            var updated = new Date(1500L);

            note.setId(1L);
            note.setTitle("Complete Note");
            note.setContent("""
                    This is the full content
                    of a complete note.
                    """);
            note.setCreatedAt(created);
            note.setUpdatedAt(updated);

            assertAll(
                    () -> assertEquals(1L, note.getId()),
                    () -> assertEquals("Complete Note", note.getTitle()),
                    () -> assertTrue(note.getContent().contains("full content")),
                    () -> assertEquals(created, note.getCreatedAt()),
                    () -> assertEquals(updated, note.getUpdatedAt())
            );
        }

        @Test
        @DisplayName("Two distinct Note instances hold independent state")
        void twoNotes_independentState() {
            var noteA = new Note();
            var noteB = new Note();

            noteA.setId(1L);
            noteA.setTitle("Note A");

            noteB.setId(2L);
            noteB.setTitle("Note B");

            assertNotEquals(noteA.getId(), noteB.getId());
            assertNotEquals(noteA.getTitle(), noteB.getTitle());
        }

        @Test
        @DisplayName("Overwriting title does not affect content")
        void overwritingTitle_doesNotAffectContent() {
            note.setTitle("Original Title");
            note.setContent("Important content");
            note.setTitle("Updated Title");

            assertEquals("Updated Title", note.getTitle());
            assertEquals("Important content", note.getContent());
        }

        @Test
        @DisplayName("Pattern matching instanceof check on Note (Java 21)")
        void patternMatching_instanceOfNote() {
            Object obj = note;
            note.setId(7L);
            note.setTitle("Pattern Note");

            if (obj instanceof Note n) {
                assertEquals(7L, n.getId());
                assertEquals("Pattern Note", n.getTitle());
            } else {
                fail("Object should be an instance of Note");
            }
        }
    }
}
