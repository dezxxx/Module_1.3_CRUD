package com.dezxxx.hometasks.crud.repository;

import com.dezxxx.hometasks.crud.model.Post;

import java.util.List;

public interface PostRepository extends GenericRepository<Post, Long> {
    List<Post> findByWriterId(Long writerId);
}