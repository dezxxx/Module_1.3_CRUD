package com.dezxxx.hometasks.crud.service;

import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.repository.WriterRepository;
import com.dezxxx.hometasks.crud.validation.NotBlankStrategy;
import com.dezxxx.hometasks.crud.validation.PositiveIdStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WriterServiceTest {

    @Mock
    private WriterRepository repository;

    private WriterService service;

    @BeforeEach
    void setUp() {
        service = new WriterService(
                repository,
                new NotBlankStrategy(),
                new PositiveIdStrategy()
        );
    }

    @Test
    void create_shouldSaveAndReturnWriter() {
        Writer saved = new Writer();
        saved.setId(1L);
        saved.setFirstName("John");
        saved.setLastName("Doe");

        when(repository.save(any(Writer.class))).thenReturn(saved);

        Writer result = service.create("John", "Doe");

        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(repository).save(any(Writer.class));
    }

    @Test
    void create_shouldThrowWhenFirstNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create("  ", "Doe"));
        verifyNoInteractions(repository);
    }

    @Test
    void create_shouldThrowWhenFirstNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(null, "Doe"));
        verifyNoInteractions(repository);
    }

    @Test
    void create_shouldThrowWhenLastNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create("John", "  "));
        verifyNoInteractions(repository);
    }

    @Test
    void create_shouldThrowWhenLastNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create("John", null));
        verifyNoInteractions(repository);
    }

    @Test
    void getAll_shouldReturnAllWriters() {
        List<Writer> writers = List.of(new Writer(), new Writer());
        when(repository.findAll()).thenReturn(writers);

        List<Writer> result = service.getAll();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void getById_shouldReturnWriter() {
        Writer writer = new Writer();
        writer.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(writer));

        Writer result = service.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> service.getById(1L));
    }

    @Test
    void getById_shouldThrowWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getById(null));
        verifyNoInteractions(repository);
    }

    @Test
    void getById_shouldThrowWhenIdIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> service.getById(-1L));
        verifyNoInteractions(repository);
    }

    @Test
    void update_shouldUpdateAndReturnWriter() {
        Writer writer = new Writer();
        writer.setId(1L);
        writer.setFirstName("Old");
        writer.setLastName("Name");

        when(repository.findById(1L)).thenReturn(Optional.of(writer));
        when(repository.update(writer)).thenReturn(writer);

        Writer result = service.update(1L, "New", "Name");

        assertEquals("New", result.getFirstName());
        verify(repository).update(writer);
    }

    @Test
    void update_shouldThrowWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.update(null, "John", "Doe"));
        verifyNoInteractions(repository);
    }

    @Test
    void update_shouldThrowWhenFirstNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> service.update(1L, "", "Doe"));
        verifyNoInteractions(repository);
    }

    @Test
    void update_shouldThrowWhenLastNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> service.update(1L, "John", "  "));
        verifyNoInteractions(repository);
    }

    @Test
    void delete_shouldCallRepository() {
        service.delete(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.delete(null));
        verifyNoInteractions(repository);
    }
}
