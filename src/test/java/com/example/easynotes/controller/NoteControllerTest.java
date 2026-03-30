package com.example.easynotes.controller;

import com.example.easynotes.exception.ResourceNotFoundException;
import com.example.easynotes.model.Note;
import com.example.easynotes.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NoteController Unit Tests")
class NoteControllerTest {

    @Mock
    NoteRepository noteRepository;

    @InjectMocks
    NoteController noteController;

    private Note sampleNote;

    @BeforeEach
    void setUp() {
        sampleNote = new Note();
        sampleNote.setId(1L);
        sampleNote.setTitle("Test Title");
        sampleNote.setContent("Test Content");
    }

    // -------------------------------------------------------------------------
    // Helper to build a Note with fluent style using Java 21 clarity
    // -------------------------------------------------------------------------
    private Note buildNote(Long id, String title, String content) {
        Note note = new Note();
        note.setId(id);
        note.setTitle(title);
        note.setContent(content);
        return note;
    }

    // =========================================================================
    // getAllNotes
    // =========================================================================
    @Nested
    @DisplayName("getAllNotes()")
    class GetAllNotes {

        @Test
        @DisplayName("should return all notes from repository")
        void shouldReturnAllNotes() {
            List<Note> notes = List.of(
                    buildNote(1L, "Title 1", "Content 1"),
                    buildNote(2L, "Title 2", "Content 2")
            );
            when(noteRepository.findAll()).thenReturn(notes);

            List<Note> result = noteController.getAllNotes();

            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyElementsOf(notes);
            verify(noteRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("should return empty list when no notes exist")
        void shouldReturnEmptyListWhenNoNotes() {
            when(noteRepository.findAll()).thenReturn(Collections.emptyList());

            List<Note> result = noteController.getAllNotes();

            assertThat(result).isEmpty();
            verify(noteRepository, times(1)).findAll();
        }
    }

    // =========================================================================
    // createNote
    // =========================================================================
    @Nested
    @DisplayName("createNote()")
    class CreateNote {

        @Test
        @DisplayName("should persist and return the created note")
        void shouldCreateAndReturnNote() {
            Note inputNote = buildNote(null, "New Title", "New Content");
            Note savedNote = buildNote(10L, "New Title", "New Content");

            when(noteRepository.save(inputNote)).thenReturn(savedNote);

            Note result = noteController.createNote(inputNote);

            assertThat(result.getId()).isEqualTo(10L);
            assertThat(result.getTitle()).isEqualTo("New Title");
            assertThat(result.getContent()).isEqualTo("New Content");
            verify(noteRepository, times(1)).save(inputNote);
        }

        @Test
        @DisplayName("should delegate save to repository exactly once")
        void shouldCallRepositorySaveOnce() {
            when(noteRepository.save(any(Note.class))).thenReturn(sampleNote);

            noteController.createNote(sampleNote);

            verify(noteRepository, times(1)).save(sampleNote);
        }
    }

    // =========================================================================
    // getNoteById
    // =========================================================================
    @Nested
    @DisplayName("getNoteById()")
    class GetNoteById {

        @Test
        @DisplayName("should return note when found")
        void shouldReturnNoteWhenFound() {
            when(noteRepository.findById(1L)).thenReturn(Optional.of(sampleNote));

            Note result = noteController.getNoteById(1L);

            assertThat(result).isEqualTo(sampleNote);
            verify(noteRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when note not found")
        void shouldThrowResourceNotFoundExceptionWhenNoteNotFound() {
            when(noteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteController.getNoteById(99L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(noteRepository, times(1)).findById(99L);
        }

        @Test
        @DisplayName("should throw exception with correct fields embedded in message")
        void shouldThrowExceptionWithCorrectMessageFields() {
            long missingId = 42L;
            when(noteRepository.findById(missingId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteController.getNoteById(missingId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Note")
                    .hasMessageContaining("id")
                    .hasMessageContaining(String.valueOf(missingId));
        }
    }

    // =========================================================================
    // updateNote
    // =========================================================================
    @Nested
    @DisplayName("updateNote()")
    class UpdateNote {

        @Test
        @DisplayName("should update title and content, then return updated note")
        void shouldUpdateAndReturnNote() {
            Note existingNote = buildNote(1L, "Old Title", "Old Content");
            Note noteDetails  = buildNote(null, "New Title", "New Content");
            Note savedNote    = buildNote(1L, "New Title", "New Content");

            when(noteRepository.findById(1L)).thenReturn(Optional.of(existingNote));
            when(noteRepository.save(existingNote)).thenReturn(savedNote);

            Note result = noteController.updateNote(1L, noteDetails);

            assertThat(result.getTitle()).isEqualTo("New Title");
            assertThat(result.getContent()).isEqualTo("New Content");
            verify(noteRepository, times(1)).findById(1L);
            verify(noteRepository, times(1)).save(existingNote);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when note to update not found")
        void shouldThrowWhenNoteToUpdateNotFound() {
            Note noteDetails = buildNote(null, "X", "Y");
            when(noteRepository.findById(55L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteController.updateNote(55L, noteDetails))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(noteRepository, never()).save(any());
        }

        @Test
        @DisplayName("should apply noteDetails fields onto existing note before saving")
        void shouldMutateExistingNoteBeforeSave() {
            Note existingNote = buildNote(1L, "Original", "Original Content");
            Note noteDetails  = buildNote(null, "Updated Title", "Updated Content");

            when(noteRepository.findById(1L)).thenReturn(Optional.of(existingNote));
            when(noteRepository.save(any(Note.class))).thenAnswer(inv -> inv.getArgument(0));

            Note result = noteController.updateNote(1L, noteDetails);

            assertThat(result.getTitle()).isEqualTo("Updated Title");
            assertThat(result.getContent()).isEqualTo("Updated Content");
        }
    }

    // =========================================================================
    // deleteNote
    // =========================================================================
    @Nested
    @DisplayName("deleteNote()")
    class DeleteNote {

        @Test
        @DisplayName("should delete note and return 200 OK")
        void shouldDeleteNoteAndReturn200() {
            when(noteRepository.findById(1L)).thenReturn(Optional.of(sampleNote));
            doNothing().when(noteRepository).delete(sampleNote);

            ResponseEntity<?> response = noteController.deleteNote(1L);

            assertThat(response.getStatusCode().value()).isEqualTo(200);
            verify(noteRepository, times(1)).findById(1L);
            verify(noteRepository, times(1)).delete(sampleNote);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when note to delete not found")
        void shouldThrowWhenNoteToDeleteNotFound() {
            when(noteRepository.findById(77L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteController.deleteNote(77L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(noteRepository, never()).delete(any());
        }

        @Test
        @DisplayName("should call delete on repository with the correct note object")
        void shouldPassCorrectNoteToDelete() {
            Note noteToDelete = buildNote(3L, "To Delete", "Content");
            when(noteRepository.findById(3L)).thenReturn(Optional.of(noteToDelete));

            noteController.deleteNote(3L);

            verify(noteRepository).delete(noteToDelete);
        }

        @Test
        @DisplayName("should return non-null response entity body or empty ok")
        void shouldReturnOkResponseEntity() {
            when(noteRepository.findById(1L)).thenReturn(Optional.of(sampleNote));

            ResponseEntity<?> response = noteController.deleteNote(1L);

            assertThat(response).isNotNull();
            assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        }
    }
}
