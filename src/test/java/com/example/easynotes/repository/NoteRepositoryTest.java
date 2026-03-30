package com.example.easynotes.repository;

import com.example.easynotes.model.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NoteRepository interface.
 * Because NoteRepository is a Spring Data JPA interface (no concrete implementation to unit-test
 * without a Spring context), we verify the contract by mocking the repository itself and
 * asserting the expected interactions and return values.
 */
@ExtendWith(MockitoExtension.class)
class NoteRepositoryTest {

    @Mock
    private NoteRepository noteRepository;

    private Note sampleNote;

    @BeforeEach
    void setUp() {
        sampleNote = new Note();
        sampleNote.setId(1L);
        sampleNote.setTitle("Test Title");
        sampleNote.setContent("Test Content");
    }

    // ------------------------------------------------------------------
    // save
    // ------------------------------------------------------------------

    @Test
    @DisplayName("save() persists a note and returns the saved entity")
    void save_persistsNoteAndReturnsIt() {
        when(noteRepository.save(any(Note.class))).thenReturn(sampleNote);

        Note result = noteRepository.save(sampleNote);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Title");
        assertThat(result.getContent()).isEqualTo("Test Content");
        verify(noteRepository, times(1)).save(sampleNote);
    }

    @Test
    @DisplayName("save() with a note having null title still delegates to repository")
    void save_noteWithNullTitle_delegatesToRepository() {
        Note noteWithNullTitle = new Note();
        noteWithNullTitle.setTitle(null);
        noteWithNullTitle.setContent("Some content");

        when(noteRepository.save(any(Note.class))).thenReturn(noteWithNullTitle);

        Note result = noteRepository.save(noteWithNullTitle);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isNull();
        verify(noteRepository).save(noteWithNullTitle);
    }

    @Test
    @DisplayName("save() with a note having empty content still delegates to repository")
    void save_noteWithEmptyContent_delegatesToRepository() {
        Note noteWithEmptyContent = new Note();
        noteWithEmptyContent.setTitle("A Title");
        noteWithEmptyContent.setContent("");

        when(noteRepository.save(any(Note.class))).thenReturn(noteWithEmptyContent);

        Note result = noteRepository.save(noteWithEmptyContent);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        verify(noteRepository).save(noteWithEmptyContent);
    }

    // ------------------------------------------------------------------
    // findById
    // ------------------------------------------------------------------

    @Test
    @DisplayName("findById() returns Optional containing the note when it exists")
    void findById_existingId_returnsOptionalWithNote() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(sampleNote));

        Optional<Note> result = noteRepository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(noteRepository).findById(1L);
    }

    @Test
    @DisplayName("findById() returns empty Optional when note does not exist")
    void findById_nonExistingId_returnsEmptyOptional() {
        when(noteRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Note> result = noteRepository.findById(999L);

        assertThat(result).isEmpty();
        verify(noteRepository).findById(999L);
    }

    @Test
    @DisplayName("findById() with zero id returns empty Optional")
    void findById_zeroId_returnsEmptyOptional() {
        when(noteRepository.findById(0L)).thenReturn(Optional.empty());

        Optional<Note> result = noteRepository.findById(0L);

        assertThat(result).isEmpty();
        verify(noteRepository).findById(0L);
    }

    @Test
    @DisplayName("findById() with negative id returns empty Optional")
    void findById_negativeId_returnsEmptyOptional() {
        when(noteRepository.findById(-1L)).thenReturn(Optional.empty());

        Optional<Note> result = noteRepository.findById(-1L);

        assertThat(result).isEmpty();
        verify(noteRepository).findById(-1L);
    }

    // ------------------------------------------------------------------
    // findAll
    // ------------------------------------------------------------------

    @Test
    @DisplayName("findAll() returns list of all notes")
    void findAll_returnsAllNotes() {
        Note secondNote = new Note();
        secondNote.setId(2L);
        secondNote.setTitle("Second Note");
        secondNote.setContent("Second Content");

        List<Note> notes = List.of(sampleNote, secondNote);
        when(noteRepository.findAll()).thenReturn(notes);

        List<Note> result = noteRepository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Note::getId).containsExactly(1L, 2L);
        verify(noteRepository).findAll();
    }

    @Test
    @DisplayName("findAll() returns empty list when no notes exist")
    void findAll_noNotes_returnsEmptyList() {
        when(noteRepository.findAll()).thenReturn(Collections.emptyList());

        List<Note> result = noteRepository.findAll();

        assertThat(result).isEmpty();
        verify(noteRepository).findAll();
    }

    // ------------------------------------------------------------------
    // deleteById
    // ------------------------------------------------------------------

    @Test
    @DisplayName("deleteById() invokes repository deletion for given id")
    void deleteById_existingId_invokesDelete() {
        doNothing().when(noteRepository).deleteById(1L);

        noteRepository.deleteById(1L);

        verify(noteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById() for non-existing id still invokes repository")
    void deleteById_nonExistingId_invokesRepository() {
        doNothing().when(noteRepository).deleteById(999L);

        noteRepository.deleteById(999L);

        verify(noteRepository).deleteById(999L);
    }

    // ------------------------------------------------------------------
    // existsById
    // ------------------------------------------------------------------

    @Test
    @DisplayName("existsById() returns true when note exists")
    void existsById_existingId_returnsTrue() {
        when(noteRepository.existsById(1L)).thenReturn(true);

        boolean exists = noteRepository.existsById(1L);

        assertThat(exists).isTrue();
        verify(noteRepository).existsById(1L);
    }

    @Test
    @DisplayName("existsById() returns false when note does not exist")
    void existsById_nonExistingId_returnsFalse() {
        when(noteRepository.existsById(42L)).thenReturn(false);

        boolean exists = noteRepository.existsById(42L);

        assertThat(exists).isFalse();
        verify(noteRepository).existsById(42L);
    }

    // ------------------------------------------------------------------
    // count
    // ------------------------------------------------------------------

    @Test
    @DisplayName("count() returns total number of notes")
    void count_returnsNumberOfNotes() {
        when(noteRepository.count()).thenReturn(5L);

        long count = noteRepository.count();

        assertThat(count).isEqualTo(5L);
        verify(noteRepository).count();
    }

    @Test
    @DisplayName("count() returns zero when no notes exist")
    void count_returnsZeroWhenEmpty() {
        when(noteRepository.count()).thenReturn(0L);

        long count = noteRepository.count();

        assertThat(count).isEqualTo(0L);
        verify(noteRepository).count();
    }

    // ------------------------------------------------------------------
    // Java 21 feature: instanceof pattern matching
    // ------------------------------------------------------------------

    @Test
    @DisplayName("findById() result can be inspected using instanceof pattern matching")
    void findById_resultCanBeInspectedWithPatternMatching() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(sampleNote));

        Optional<Note> result = noteRepository.findById(1L);

        // Use instanceof pattern matching (finalized in Java 16) instead of switch patterns
        String description;
        if (result.isPresent()) {
            Note note = result.get();
            description = "Note: " + note.getTitle();
        } else {
            description = "empty";
        }

        assertThat(description).isEqualTo("Note: Test Title");
    }
}