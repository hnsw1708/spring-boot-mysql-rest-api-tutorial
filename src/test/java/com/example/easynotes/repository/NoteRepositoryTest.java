package com.example.easynotes.repository;

import com.example.easynotes.model.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NoteRepository using Mockito mocks.
 * Since NoteRepository is a Spring Data JPA interface, we mock it directly
 * to verify interactions and expected return values.
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
        sampleNote.setTitle("Test Note");
        sampleNote.setContent("Test Content");
    }

    // -----------------------------------------------------------------------
    // save
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("save() should persist and return the note")
    void save_shouldReturnSavedNote() {
        when(noteRepository.save(any(Note.class))).thenReturn(sampleNote);

        Note saved = noteRepository.save(sampleNote);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getTitle()).isEqualTo("Test Note");
        assertThat(saved.getContent()).isEqualTo("Test Content");
        verify(noteRepository, times(1)).save(sampleNote);
    }

    @Test
    @DisplayName("save() with note having null title should still delegate to repository")
    void save_withNullTitle_shouldDelegateToRepository() {
        Note noteWithNullTitle = new Note();
        noteWithNullTitle.setId(2L);
        noteWithNullTitle.setTitle(null);
        noteWithNullTitle.setContent("Some content");

        when(noteRepository.save(any(Note.class))).thenReturn(noteWithNullTitle);

        Note saved = noteRepository.save(noteWithNullTitle);

        assertThat(saved).isNotNull();
        assertThat(saved.getTitle()).isNull();
        verify(noteRepository, times(1)).save(noteWithNullTitle);
    }

    @Test
    @DisplayName("save() with note having empty content should still delegate to repository")
    void save_withEmptyContent_shouldDelegateToRepository() {
        Note noteWithEmptyContent = new Note();
        noteWithEmptyContent.setId(3L);
        noteWithEmptyContent.setTitle("Empty Content Note");
        noteWithEmptyContent.setContent("");

        when(noteRepository.save(any(Note.class))).thenReturn(noteWithEmptyContent);

        Note saved = noteRepository.save(noteWithEmptyContent);

        assertThat(saved).isNotNull();
        assertThat(saved.getContent()).isEmpty();
        verify(noteRepository, times(1)).save(noteWithEmptyContent);
    }

    // -----------------------------------------------------------------------
    // findAll
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findAll() should return all notes")
    void findAll_shouldReturnAllNotes() {
        Note note2 = new Note();
        note2.setId(2L);
        note2.setTitle("Second Note");
        note2.setContent("Second Content");

        when(noteRepository.findAll()).thenReturn(List.of(sampleNote, note2));

        List<Note> notes = noteRepository.findAll();

        assertThat(notes).isNotNull().hasSize(2);
        assertThat(notes).extracting(Note::getId).containsExactly(1L, 2L);
        assertThat(notes).extracting(Note::getTitle)
                .containsExactly("Test Note", "Second Note");
        verify(noteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findAll() should return empty list when no notes exist")
    void findAll_shouldReturnEmptyList_whenNoNotesExist() {
        when(noteRepository.findAll()).thenReturn(List.of());

        List<Note> notes = noteRepository.findAll();

        assertThat(notes).isNotNull().isEmpty();
        verify(noteRepository, times(1)).findAll();
    }

    // -----------------------------------------------------------------------
    // findById
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("findById() should return Optional containing the note when found")
    void findById_shouldReturnNote_whenFound() {
        when(noteRepository.findById(1L)).thenReturn(Optional.of(sampleNote));

        Optional<Note> result = noteRepository.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getTitle()).isEqualTo("Test Note");
        verify(noteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("findById() should return empty Optional when note not found")
    void findById_shouldReturnEmpty_whenNotFound() {
        when(noteRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Note> result = noteRepository.findById(999L);

        assertThat(result).isNotPresent();
        verify(noteRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("findById() with id zero should return empty Optional")
    void findById_withIdZero_shouldReturnEmpty() {
        when(noteRepository.findById(0L)).thenReturn(Optional.empty());

        Optional<Note> result = noteRepository.findById(0L);

        assertThat(result).isEmpty();
        verify(noteRepository, times(1)).findById(0L);
    }

    @Test
    @DisplayName("findById() with negative id should return empty Optional")
    void findById_withNegativeId_shouldReturnEmpty() {
        when(noteRepository.findById(-1L)).thenReturn(Optional.empty());

        Optional<Note> result = noteRepository.findById(-1L);

        assertThat(result).isEmpty();
        verify(noteRepository, times(1)).findById(-1L);
    }

    // -----------------------------------------------------------------------
    // existsById
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("existsById() should return true when note exists")
    void existsById_shouldReturnTrue_whenNoteExists() {
        when(noteRepository.existsById(1L)).thenReturn(true);

        boolean exists = noteRepository.existsById(1L);

        assertThat(exists).isTrue();
        verify(noteRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("existsById() should return false when note does not exist")
    void existsById_shouldReturnFalse_whenNoteDoesNotExist() {
        when(noteRepository.existsById(999L)).thenReturn(false);

        boolean exists = noteRepository.existsById(999L);

        assertThat(exists).isFalse();
        verify(noteRepository, times(1)).existsById(999L);
    }

    // -----------------------------------------------------------------------
    // deleteById
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteById() should invoke delete on repository")
    void deleteById_shouldInvokeDelete() {
        doNothing().when(noteRepository).deleteById(1L);

        noteRepository.deleteById(1L);

        verify(noteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById() called multiple times should reflect each invocation")
    void deleteById_calledMultipleTimes_shouldReflectEachInvocation() {
        doNothing().when(noteRepository).deleteById(anyLong());

        noteRepository.deleteById(1L);
        noteRepository.deleteById(2L);
        noteRepository.deleteById(3L);

        verify(noteRepository, times(1)).deleteById(1L);
        verify(noteRepository, times(1)).deleteById(2L);
        verify(noteRepository, times(1)).deleteById(3L);
        verify(noteRepository, times(3)).deleteById(anyLong());
    }

    // -----------------------------------------------------------------------
    // count
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("count() should return number of notes")
    void count_shouldReturnNumberOfNotes() {
        when(noteRepository.count()).thenReturn(5L);

        long count = noteRepository.count();

        assertThat(count).isEqualTo(5L);
        verify(noteRepository, times(1)).count();
    }

    @Test
    @DisplayName("count() should return zero when repository is empty")
    void count_shouldReturnZero_whenRepositoryIsEmpty() {
        when(noteRepository.count()).thenReturn(0L);

        long count = noteRepository.count();

        assertThat(count).isZero();
        verify(noteRepository, times(1)).count();
    }

    // -----------------------------------------------------------------------
    // saveAll
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("saveAll() should persist and return all provided notes")
    void saveAll_shouldReturnAllSavedNotes() {
        Note note2 = new Note();
        note2.setId(2L);
        note2.setTitle("Bulk Note");
        note2.setContent("Bulk Content");

        List<Note> toSave = List.of(sampleNote, note2);
        when(noteRepository.saveAll(toSave)).thenReturn(toSave);

        List<Note> saved = noteRepository.saveAll(toSave);

        assertThat(saved).hasSize(2);
        assertThat(saved).containsExactlyInAnyOrder(sampleNote, note2);
        verify(noteRepository, times(1)).saveAll(toSave);
    }

    @Test
    @DisplayName("saveAll() with empty list should return empty list")
    void saveAll_withEmptyList_shouldReturnEmptyList() {
        when(noteRepository.saveAll(List.of())).thenReturn(List.of());

        List<Note> saved = noteRepository.saveAll(List.of());

        assertThat(saved).isEmpty();
        verify(noteRepository, times(1)).saveAll(List.of());
    }

    // -----------------------------------------------------------------------
    // delete (entity)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("delete() should invoke delete with the given note entity")
    void delete_shouldInvokeDeleteWithNoteEntity() {
        doNothing().when(noteRepository).delete(any(Note.class));

        noteRepository.delete(sampleNote);

        verify(noteRepository, times(1)).delete(sampleNote);
    }

    // -----------------------------------------------------------------------
    // deleteAll
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("deleteAll() should invoke deleteAll on repository")
    void deleteAll_shouldInvokeDeleteAll() {
        doNothing().when(noteRepository).deleteAll();

        noteRepository.deleteAll();

        verify(noteRepository, times(1)).deleteAll();
    }
}
