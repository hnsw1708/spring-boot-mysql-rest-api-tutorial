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
class NoteControllerTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteController noteController;

    private Note sampleNote;

    @BeforeEach
    void setUp() {
        sampleNote = new Note();
        sampleNote.setId(1L);
        sampleNote.setTitle("Test Title");
        sampleNote.setContent("Test Content");
    }

    // ---------------------------------------------------------------------------
    // getAllNotes
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("getAllNotes")
    class GetAllNotes {

        @Test
        @DisplayName("returns list of notes when repository has data")
        void returnsAllNotes() {
            when(noteRepository.findAll()).thenReturn(List.of(sampleNote));

            List<Note> result = noteController.getAllNotes();

            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(sampleNote);
            verify(noteRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("returns empty list when no notes exist")
        void returnsEmptyList() {
            when(noteRepository.findAll()).thenReturn(Collections.emptyList());

            List<Note> result = noteController.getAllNotes();

            assertThat(result).isEmpty();
            verify(noteRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("returns multiple notes")
        void returnsMultipleNotes() {
            Note second = new Note();
            second.setId(2L);
            second.setTitle("Second");
            second.setContent("Content 2");

            when(noteRepository.findAll()).thenReturn(List.of(sampleNote, second));

            List<Note> result = noteController.getAllNotes();

            assertThat(result).hasSize(2);
        }
    }

    // ---------------------------------------------------------------------------
    // createNote
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("createNote")
    class CreateNote {

        @Test
        @DisplayName("saves and returns the note")
        void savesAndReturnsNote() {
            when(noteRepository.save(sampleNote)).thenReturn(sampleNote);

            Note result = noteController.createNote(sampleNote);

            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Test Title");
            assertThat(result.getContent()).isEqualTo("Test Content");
            verify(noteRepository, times(1)).save(sampleNote);
        }

        @Test
        @DisplayName("delegates persistence to the repository")
        void delegatesToRepository() {
            Note newNote = new Note();
            newNote.setTitle("New Note");
            newNote.setContent("Some content");

            when(noteRepository.save(newNote)).thenReturn(newNote);

            Note result = noteController.createNote(newNote);

            assertThat(result).isSameAs(newNote);
        }

        @Test
        @DisplayName("creates note with empty title and content")
        void createsNoteWithEmptyFields() {
            Note emptyNote = new Note();
            emptyNote.setTitle("");
            emptyNote.setContent("");

            when(noteRepository.save(emptyNote)).thenReturn(emptyNote);

            Note result = noteController.createNote(emptyNote);

            assertThat(result.getTitle()).isEmpty();
            assertThat(result.getContent()).isEmpty();
        }
    }

    // ---------------------------------------------------------------------------
    // getNoteById
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("getNoteById")
    class GetNoteById {

        @Test
        @DisplayName("returns note when found")
        void returnsNoteWhenFound() {
            when(noteRepository.findById(1L)).thenReturn(Optional.of(sampleNote));

            Note result = noteController.getNoteById(1L);

            assertThat(result).isEqualTo(sampleNote);
            verify(noteRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when note is absent")
        void throwsWhenNoteNotFound() {
            when(noteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteController.getNoteById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Note")
                    .hasMessageContaining("id")
                    .hasMessageContaining("99");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException for null-equivalent missing id")
        void throwsForMissingId() {
            when(noteRepository.findById(0L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteController.getNoteById(0L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ---------------------------------------------------------------------------
    // updateNote
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("updateNote")
    class UpdateNote {

        @Test
        @DisplayName("updates title and content, returns updated note")
        void updatesNoteSuccessfully() {
            Note details = new Note();
            details.setTitle("Updated Title");
            details.setContent("Updated Content");

            Note updatedNote = new Note();
            updatedNote.setId(1L);
            updatedNote.setTitle("Updated Title");
            updatedNote.setContent("Updated Content");

            when(noteRepository.findById(1L)).thenReturn(Optional.of(sampleNote));
            when(noteRepository.save(sampleNote)).thenReturn(updatedNote);

            Note result = noteController.updateNote(1L, details);

            assertThat(result.getTitle()).isEqualTo("Updated Title");
            assertThat(result.getContent()).isEqualTo("Updated Content");
            verify(noteRepository, times(1)).findById(1L);
            verify(noteRepository, times(1)).save(sampleNote);
        }

        @Test
        @DisplayName("mutates the existing note's fields before saving")
        void mutatesExistingNoteFields() {
            Note details = new Note();
            details.setTitle("New Title");
            details.setContent("New Content");

            when(noteRepository.findById(1L)).thenReturn(Optional.of(sampleNote));
            when(noteRepository.save(any(Note.class))).thenAnswer(inv -> inv.getArgument(0));

            Note result = noteController.updateNote(1L, details);

            assertThat(result.getTitle()).isEqualTo("New Title");
            assertThat(result.getContent()).isEqualTo("New Content");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when note to update does not exist")
        void throwsWhenNoteNotFound() {
            Note details = new Note();
            details.setTitle("Title");
            details.setContent("Content");

            when(noteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteController.updateNote(99L, details))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ---------------------------------------------------------------------------
    // deleteNote
    // ---------------------------------------------------------------------------
    @Nested
    @DisplayName("deleteNote")
    class DeleteNote {

        @Test
        @DisplayName("deletes note when found and returns ok response")
        void deletesNoteWhenFound() {
            when(noteRepository.findById(1L)).thenReturn(Optional.of(sampleNote));
            doNothing().when(noteRepository).delete(sampleNote);

            ResponseEntity<?> response = noteController.deleteNote(1L);

            assertThat(response.getStatusCode().value()).isEqualTo(200);
            verify(noteRepository, times(1)).findById(1L);
            verify(noteRepository, times(1)).delete(sampleNote);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when note to delete does not exist")
        void throwsWhenNoteNotFound() {
            when(noteRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> noteController.deleteNote(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}