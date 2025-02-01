package com.example.demo.controller;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.model.BlogPost;
import com.example.demo.model.User;
import com.example.demo.service.BlogPostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BlogPost> createPost(
            @Valid @RequestBody BlogPost blogPost,
            @AuthenticationPrincipal User user) {
        blogPost.setUserId(user.getId());
        return ResponseEntity.ok(blogPostService.save(blogPost));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BlogPost> getPost(@PathVariable Long id, @AuthenticationPrincipal User user) {
        BlogPost post = blogPostService.getPostById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to view this post");
        }

        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BlogPost> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody BlogPost blogPost,
            @AuthenticationPrincipal User user) {

        BlogPost existingPost = blogPostService.getPostById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!existingPost.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to update this post");
        }

        blogPost.setId(id);
        blogPost.setUserId(user.getId());
        return ResponseEntity.ok(blogPostService.save(blogPost));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletePost(@PathVariable Long id, @AuthenticationPrincipal User user) {
        BlogPost post = blogPostService.getPostById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this post");
        }

        blogPostService.deletePost(id);
        return ResponseEntity.ok().build();
    }
}