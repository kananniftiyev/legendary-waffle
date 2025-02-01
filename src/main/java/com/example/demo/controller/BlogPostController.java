package com.example.demo.controller;

import com.example.demo.dtos.PostInDTO;
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
import org.springframework.http.HttpStatus;


import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/private/posts")
public class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> createPost(
            @Valid @RequestBody PostInDTO postInput,
            @AuthenticationPrincipal(errorOnInvalidType=true) User user) {

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "User not authenticated"));
        }

        BlogPost blogPost = BlogPost.builder()
                .userId(user.getId())
                .title(postInput.getTitle())
                .content(postInput.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        blogPost = blogPostService.save(blogPost);

        System.out.println(blogPost);
        return ResponseEntity.ok()
                .body(Map.of(
                        "message", "Blog post created successfully",
                        "blogPost", blogPost));
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
    public ResponseEntity<Object> updatePost(
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
        BlogPost updatedPost = blogPostService.save(blogPost);

        return ResponseEntity.ok(Map.of(
                "message", "Post updated successfully",
                "updatedPost", updatedPost
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> deletePost(@PathVariable Long id, @AuthenticationPrincipal User user) {
        BlogPost post = blogPostService.getPostById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUserId().equals(user.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this post");
        }

        blogPostService.deletePost(id);

        return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
    }
}