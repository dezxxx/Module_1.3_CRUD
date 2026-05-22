package com.dezxxx.hometasks.crud.service;

import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.repository.LabelRepository;
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
class LabelServiceTest {

    @Mock
    private LabelRepository repository;

    private LabelService service;

    @BeforeEach
    void setUp() {
        service = new LabelService(
                repository,
                new NotBlankStrategy(),
                new PositiveIdStrategy()
        );
    }

    @Test
    void create_shouldSaveAndReturnLabel() {
        Label saved = new Label();
        saved.setId(1L);
        saved.setName("Java");

        when(repository.save(any(Label.class))).thenReturn(saved);

        Label result = service.create("Java");

        assertEquals(1L, result.getId());
        assertEquals("Java", result.getName());
        verify(repository).save(any(Label.class));
    }

    @Test
    void create_shouldThrowWhenNameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create("  "));
        verifyNoInteractions(repository);
    }

    @Test
    void create_shouldThrowWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(null));
        verifyNoInteractions(repository);
    }

    @Test
    void getAll_shouldReturnAllLabels() {
        List<Label> labels = List.of(new Label(), new Label());
        when(repository.findAll()).thenReturn(labels);

        List<Label> result = service.getAll();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void getById_shouldReturnLabel() {
        Label label = new Label();
        label.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(label));

        Label result = service.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> service.getById(99L));
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
    void update_shouldUpdateAndReturnLabel() {
        Label label = new Label();
        label.setId(1L);
        label.setName("Old");

        when(repository.findById(1L)).thenReturn(Optional.of(label));
        when(repository.update(label)).thenReturn(label);

        Label result = service.update(1L, "New");

        assertEquals("New", result.getName());
        verify(repository).update(label);
    }

    @Test
    void update_shouldThrowWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.update(null, "New"));
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
