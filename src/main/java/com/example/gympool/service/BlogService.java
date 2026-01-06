package com.example.gympool.service;

import com.example.gympool.dto.BlogDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BlogService {
    
    // Tạo blog mới
    BlogDTO createBlog(BlogDTO dto, MultipartFile coverImage, Long authorId);
    
    // Cập nhật blog
    BlogDTO updateBlog(Long id, BlogDTO dto, MultipartFile coverImage);
    
    // Xóa blog
    void deleteBlog(Long id);
    
    // Lấy tất cả blog (cho manager)
    List<BlogDTO> getAllBlogs();
    
    // Lấy blog theo ID
    BlogDTO getBlogById(Long id);
    
    // Toggle ghim
    BlogDTO togglePin(Long id);
    
    // Lấy 4 blog mới nhất cho trang chủ
    List<BlogDTO> getPublicBlogs();

    List<BlogDTO> get4PinnedBlogs();
}
