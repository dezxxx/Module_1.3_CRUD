package com.dezxxx.hometasks.crud.service;

import com.dezxxx.hometasks.crud.config.PostStatus;
import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.repository.PostRepository;
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
class PostServiceTest {

    @Mock
    private PostRepository repository;

    private PostService service;

    @BeforeEach
    void setUp() {
        service = new PostService(
                repository,
                new NotBlankStrategy(),
                new PositiveIdStrategy()
        );
    }

    @Test
    void create_shouldSaveAndReturnPost() {
        Writer writer = new Writer();
        writer.setId(1L);

        Post saved = new Post();
        saved.setId(1L);
        saved.setTitle("Java");
        saved.setWriter(writer);

        when(repository.save(any(Post.class))).thenReturn(saved);

        Post result = service.create("Java", "Content", List.of(), writer);

        assertEquals(1L, result.getId());
        assertEquals("Java", result.getTitle());
        assertEquals(writer, result.getWriter());
        verify(repository).save(any(Post.class));
    }

    @Test
    void create_shouldThrowWhenTitleIsBlank() {
        Writer writer = new Writer();
        assertThrows(IllegalArgumentException.class,
                () -> service.create("  ", "Content", List.of(), writer));
        verifyNoInteractions(repository);
    }

    @Test
    void create_shouldThrowWhenContentIsBlank() {
        Writer writer = new Writer();
        assertThrows(IllegalArgumentException.class,
                () -> service.create("Title", "", List.of(), writer));
        verifyNoInteractions(repository);
    }

    @Test
    void getAll_shouldReturnAllPosts() {
        List<Post> posts = List.of(new Post(), new Post());
        when(repository.findAll()).thenReturn(posts);

        List<Post> result = service.getAll();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void getById_shouldReturnPost() {
        Post post = new Post();
        post.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(post));

        Post result = service.getById(1L);

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
    void update_shouldUpdateAndReturnPost() {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Old");

        when(repository.findById(1L)).thenReturn(Optional.of(post));
        when(repository.update(post)).thenReturn(post);

        Post result = service.update(1L, "New", "New content", List.of());

        assertEquals("New", result.getTitle());
        verify(repository).update(post);
    }

    @Test
    void update_shouldThrowWhenTitleIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> service.update(1L, "", "Content", List.of()));
        verifyNoInteractions(repository);
    }

    @Test
    void update_shouldThrowWhenContentIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> service.update(1L, "Title", "  ", List.of()));
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

    @Test
    void changeStatus_shouldChangeStatusAndReturnPost() {
        Post post = new Post();
        post.setId(1L);
        post.setStatus(PostStatus.ACTIVE);

        when(repository.findById(1L)).thenReturn(Optional.of(post));
        when(repository.update(post)).thenReturn(post);

        Post result = service.changeStatus(1L, PostStatus.UNDER_REVIEW);

        assertEquals(PostStatus.UNDER_REVIEW, result.getStatus());
        verify(repository).update(post);
    }

    @Test
    void changeStatus_shouldThrowWhenIdIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.changeStatus(null, PostStatus.ACTIVE));
        verifyNoInteractions(repository);
    }

    @Test
    void changeStatus_shouldThrowWhenIdIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> service.changeStatus(-5L, PostStatus.ACTIVE));
        verifyNoInteractions(repository);
    }

    @Test
    void changeStatus_shouldThrowWhenPostNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> service.changeStatus(99L, PostStatus.ACTIVE));
    }
}
