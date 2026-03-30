package com.example.easynotes.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Note model tests")
class NoteTest {

    private Note note;

    @BeforeEach
    void setUp() {
        note = new Note();
    }

    // -------------------------------------------------------------------------
    // ID
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("id field")
    class IdField {

        @Test
        @DisplayName("getId returns null when not set")
        void getId_returnsNullByDefault() {
            assertNull(note.getId());
        }

        @Test
        @DisplayName("setId / getId round-trip")
        void setAndGetId() {
            note.setId(42L);
            assertEquals(42L, note.getId());
        }

        @Test
        @DisplayName("setId accepts null")
        void setId_null() {
            note.setId(1L);
            note.setId(null);
            assertNull(note.getId());
        }

        @Test
        @DisplayName("setId accepts zero")
        void setId_zero() {
            note.setId(0L);
            assertEquals(0L, note.getId());
        }

        @Test
        @DisplayName("setId accepts negative value")
        void setId_negative() {
            note.setId(-1L);
            assertEquals(-1L, note.getId());
        }
    }

    // -------------------------------------------------------------------------
    // Title
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("title field")
    class TitleField {

        @Test
        @DisplayName("getTitle returns null when not set")
        void getTitle_returnsNullByDefault() {
            assertNull(note.getTitle());
        }

        @Test
        @DisplayName("setTitle / getTitle round-trip with a regular string")
        void setAndGetTitle_regularString() {
            note.setTitle("My first note");
            assertEquals("My first note", note.getTitle());
        }

        @Test
        @DisplayName("setTitle / getTitle round-trip with a text block value")
        void setAndGetTitle_textBlock() {
            // Java 21 text block used for clarity
            var multiLineTitle = """
                    Meeting notes\
                    """;
            note.setTitle(multiLineTitle);
            assertEquals("Meeting notes", note.getTitle());
        }

        @Test
        @DisplayName("setTitle accepts null")
        void setTitle_null() {
            note.setTitle("initial");
            note.setTitle(null);
            assertNull(note.getTitle());
        }

        @Test
        @DisplayName("setTitle accepts empty string")
        void setTitle_empty() {
            note.setTitle("");
            assertEquals("", note.getTitle());
        }

        @Test
        @DisplayName("setTitle accepts blank string")
        void setTitle_blank() {
            note.setTitle("   ");
            assertEquals("   ", note.getTitle());
        }

        @Test
        @DisplayName("setTitle overwrites the previous value")
        void setTitle_overwrite() {
            note.setTitle("first");
            note.setTitle("second");
            assertEquals("second", note.getTitle());
        }
    }

    // -------------------------------------------------------------------------
    // Content
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("content field")
    class ContentField {

        @Test
        @DisplayName("getContent returns null when not set")
        void getContent_returnsNullByDefault() {
            assertNull(note.getContent());
        }

        @Test
        @DisplayName("setContent / getContent round-trip with a regular string")
        void setAndGetContent_regularString() {
            note.setContent("Some content here");
            assertEquals("Some content here", note.getContent());
        }

        @Test
        @DisplayName("setContent / getContent round-trip with a multiline text block")
        void setAndGetContent_textBlock() {
            var body = """
                    Line one
                    Line two
                    Line three
                    """;
            note.setContent(body);
            assertTrue(note.getContent().contains("Line one"));
            assertTrue(note.getContent().contains("Line two"));
            assertTrue(note.getContent().contains("Line three"));
        }

        @Test
        @DisplayName("setContent accepts null")
        void setContent_null() {
            note.setContent("original");
            note.setContent(null);
            assertNull(note.getContent());
        }

        @Test
        @DisplayName("setContent accepts empty string")
        void setContent_empty() {
            note.setContent("");
            assertEquals("", note.getContent());
        }

        @Test
        @DisplayName("setContent overwrites the previous value")
        void setContent_overwrite() {
            note.setContent("old content");
            note.setContent("new content");
            assertEquals("new content", note.getContent());
        }
    }

    // -------------------------------------------------------------------------
    // createdAt
    // -------------------------------------------------------------------------
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
        void setAndGetCreatedAt() {
            var now = new Date();
            note.setCreatedAt(now);
            assertEquals(now, note.getCreatedAt());
        }

        @Test
        @DisplayName("setCreatedAt accepts null")
        void setCreatedAt_null() {
            note.setCreatedAt(new Date());
            note.setCreatedAt(null);
            assertNull(note.getCreatedAt());
        }

        @Test
        @DisplayName("getCreatedAt returns the exact same Date instance")
        void getCreatedAt_returnsSameInstance() {
            var date = new Date(1_000_000L);
            note.setCreatedAt(date);
            assertSame(date, note.getCreatedAt());
        }

        @Test
        @DisplayName("setCreatedAt overwrites previous value")
        void setCreatedAt_overwrite() {
            var first  = new Date(1_000L);
            var second = new Date(2_000L);
            note.setCreatedAt(first);
            note.setCreatedAt(second);
            assertEquals(second, note.getCreatedAt());
        }
    }

    // -------------------------------------------------------------------------
    // updatedAt
    // -------------------------------------------------------------------------
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
        void setAndGetUpdatedAt() {
            var now = new Date();
            note.setUpdatedAt(now);
            assertEquals(now, note.getUpdatedAt());
        }

        @Test
        @DisplayName("setUpdatedAt accepts null")
        void setUpdatedAt_null() {
            note.setUpdatedAt(new Date());
            note.setUpdatedAt(null);
            assertNull(note.getUpdatedAt());
        }

        @Test
        @DisplayName("getUpdatedAt returns the exact same Date instance")
        void getUpdatedAt_returnsSameInstance() {
            var date = new Date(9_999_999L);
            note.setUpdatedAt(date);
            assertSame(date, note.getUpdatedAt());
        }

        @Test
        @DisplayName("setUpdatedAt overwrites previous value")
        void setUpdatedAt_overwrite() {
            var first  = new Date(1_000L);
            var second = new Date(2_000L);
            note.setUpdatedAt(first);
            note.setUpdatedAt(second);
            assertEquals(second, note.getUpdatedAt());
        }
    }

    // -------------------------------------------------------------------------
    // Combined / integration-style checks
    // -------------------------------------------------------------------------
    @Nested
    @DisplayName("combined field checks")
    class CombinedChecks {

        @Test
        @DisplayName("fully populated Note holds all values independently")
        void fullyPopulatedNote() {
            var createdAt  = new Date(1_000L);
            var updatedAt  = new Date(2_000L);

            note.setId(1L);
            note.setTitle("Title");
            note.setContent("Content");
            note.setCreatedAt(createdAt);
            note.setUpdatedAt(updatedAt);

            assertAll(
                    () -> assertEquals(1L,        note.getId()),
                    () -> assertEquals("Title",   note.getTitle()),
                    () -> assertEquals("Content", note.getContent()),
                    () -> assertEquals(createdAt, note.getCreatedAt()),
                    () -> assertEquals(updatedAt, note.getUpdatedAt())
            );
        }

        @Test
        @DisplayName("two distinct Note instances are independent")
        void twoDistinctInstances_areIndependent() {
            var noteA = new Note();
            var noteB = new Note();

            noteA.setId(1L);
            noteA.setTitle("Note A");

            noteB.setId(2L);
            noteB.setTitle("Note B");

            assertAll(
                    () -> assertNotEquals(noteA.getId(),   noteB.getId()),
                    () -> assertNotEquals(noteA.getTitle(), noteB.getTitle())
            );
        }

        @Test
        @DisplayName("createdAt and updatedAt can hold the same Date value")
        void createdAt_and_updatedAt_canHoldSameValue() {
            var now = new Date();
            note.setCreatedAt(now);
            note.setUpdatedAt(now);

            assertEquals(note.getCreatedAt(), note.getUpdatedAt());
        }

        @Test
        @DisplayName("updatedAt is after createdAt in normal usage")
        void updatedAt_isAfter_createdAt() {
            var createdAt = new Date(1_000L);
            var updatedAt = new Date(5_000L);

            note.setCreatedAt(createdAt);
            note.setUpdatedAt(updatedAt);

            assertTrue(note.getUpdatedAt().after(note.getCreatedAt()));
        }

        @Test
        @DisplayName("pattern matching instanceof works with Note (Java 21 feature)")
        void patternMatchingInstanceof() {
            Object obj = note;
            note.setId(7L);
            note.setTitle("Pattern");

            // Java 21 pattern matching for instanceof
            if (obj instanceof Note n) {
                assertEquals(7L,        n.getId());
                assertEquals("Pattern", n.getTitle());
            } else {
                fail("obj should be an instance of Note");
            }
        }
    }
}
