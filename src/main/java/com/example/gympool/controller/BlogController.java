package com.example.gympool.controller;

import com.example.gympool.dto.BlogDTO;
import com.example.gympool.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    // Tạo blog mới
    @PostMapping
    public ResponseEntity<BlogDTO> createBlog(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam("authorId") Long authorId,
            @RequestParam(value = "isPinned", defaultValue = "false") Boolean isPinned
    ) {
        BlogDTO dto = BlogDTO.builder()
                .title(title)
                .content(content)
                .isPinned(isPinned)
                .build();
        
        BlogDTO createdBlog = blogService.createBlog(dto, coverImage, authorId);
        return ResponseEntity.ok(createdBlog);
    }

    // Cập nhật blog
    @PutMapping("/{id}")
    public ResponseEntity<BlogDTO> updateBlog(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam(value = "isPinned", defaultValue = "false") Boolean isPinned
    ) {
        BlogDTO dto = BlogDTO.builder()
                .title(title)
                .content(content)
                .isPinned(isPinned)
                .build();
        
        BlogDTO updatedBlog = blogService.updateBlog(id, dto, coverImage);
        return ResponseEntity.ok(updatedBlog);
    }

    // Xóa blog
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBlog(@PathVariable Long id) {
        blogService.deleteBlog(id);
        return ResponseEntity.ok("Xóa blog thành công");
    }

    // Lấy tất cả blog (cho manager)
    @GetMapping
    public ResponseEntity<List<BlogDTO>> getAllBlogs() {
        List<BlogDTO> blogs = blogService.getAllBlogs();
        return ResponseEntity.ok(blogs);
    }

    // Lấy blog theo ID
    @GetMapping("/{id}")
    public ResponseEntity<BlogDTO> getBlogById(@PathVariable Long id) {
        BlogDTO blog = blogService.getBlogById(id);
        return ResponseEntity.ok(blog);
    }

    // Toggle ghim
    @PatchMapping("/{id}/pin")
    public ResponseEntity<BlogDTO> togglePin(@PathVariable Long id) {
        BlogDTO blog = blogService.togglePin(id);
        return ResponseEntity.ok(blog);
    }

    // Lấy 4 blog mới nhất cho trang chủ
    @GetMapping("/public")
    public ResponseEntity<List<BlogDTO>> getPublicBlogs() {
        List<BlogDTO> blogs = blogService.getPublicBlogs();
        return ResponseEntity.ok(blogs);
    }

    //lay 4 dua dc pin
    @GetMapping("/pinned")
    public ResponseEntity<List<BlogDTO>> getPinnnedBlogs() {
        List<BlogDTO> blogs = blogService.get4PinnedBlogs();
        return ResponseEntity.ok(blogs);
    }
}
