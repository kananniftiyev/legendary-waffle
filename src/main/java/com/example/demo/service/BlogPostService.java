package com.example.demo.service;

import com.example.demo.model.BlogPost;
import com.example.demo.repo.BlogPostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;

    public List<BlogPost> getAllPosts() {
        return blogPostRepository.findAll();
    }

    public List<BlogPost> getPostsByUserId(Long userId) {
        return blogPostRepository.findByUserId(userId);
    }

    public Optional<BlogPost> getPostById(Long id) {
        return blogPostRepository.findById(id);
    }

    public BlogPost createPost(@Valid BlogPost blogPost) {
        return blogPostRepository.save(blogPost);
    }

    public BlogPost updatePost(Long id, @Valid BlogPost updatedPost) {
        return blogPostRepository.findById(id).map(post -> {
            post.setTitle(updatedPost.getTitle());
            post.setContent(updatedPost.getContent());
            return blogPostRepository.save(post);
        }).orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public void deletePost(Long id) {
        blogPostRepository.deleteById(id);
    }

    public BlogPost save(@Valid BlogPost blogPost) {
        return blogPostRepository.save(blogPost);
    }

}