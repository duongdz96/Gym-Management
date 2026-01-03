package com.example.gympool.service.impl;

import com.example.gympool.dto.BlogDTO;
import com.example.gympool.entity.Blog;
import com.example.gympool.entity.User;
import com.example.gympool.repository.BlogRepository;
import com.example.gympool.repository.UserRepository;
import com.example.gympool.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private static final String BLOG_IMAGE_DIR = "image/blog/";

    @Override
    public BlogDTO createBlog(BlogDTO dto, MultipartFile coverImage, Long authorId) {
        // Tìm author
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Tạo blog entity
        Blog blog = Blog.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(author)
                .isPinned(dto.getIsPinned() != null ? dto.getIsPinned() : false)
                .publishDate(LocalDateTime.now())
                .build();

        // Xử lý upload ảnh bìa nếu có
        if (coverImage != null && !coverImage.isEmpty()) {
            String imagePath = saveImage(coverImage);
            blog.setCoverImagePath(imagePath);
        }

        Blog savedBlog = blogRepository.save(blog);
        return convertToDTO(savedBlog);
    }

    @Override
    public BlogDTO updateBlog(Long id, BlogDTO dto, MultipartFile coverImage) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy blog"));

        // Cập nhật thông tin
        blog.setTitle(dto.getTitle());
        blog.setContent(dto.getContent());
        blog.setIsPinned(dto.getIsPinned() != null ? dto.getIsPinned() : false);

        // Xử lý ảnh bìa mới nếu có
        if (coverImage != null && !coverImage.isEmpty()) {
            // Xóa ảnh cũ nếu có
            if (blog.getCoverImagePath() != null) {
                deleteImage(blog.getCoverImagePath());
            }
            // Lưu ảnh mới
            String imagePath = saveImage(coverImage);
            blog.setCoverImagePath(imagePath);
        }

        Blog updatedBlog = blogRepository.save(blog);
        return convertToDTO(updatedBlog);
    }

    @Override
    public void deleteBlog(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy blog"));

        // Xóa ảnh bìa nếu có
        if (blog.getCoverImagePath() != null) {
            deleteImage(blog.getCoverImagePath());
        }

        blogRepository.delete(blog);
    }

    @Override
    public List<BlogDTO> getAllBlogs() {
        return blogRepository.findAllByOrderByIsPinnedDescPublishDateDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BlogDTO getBlogById(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy blog"));
        return convertToDTO(blog);
    }

    @Override
    public BlogDTO togglePin(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy blog"));
        
        blog.setIsPinned(!blog.getIsPinned());
        Blog updatedBlog = blogRepository.save(blog);
        return convertToDTO(updatedBlog);
    }

    @Override
    public List<BlogDTO> getPublicBlogs() {
        return blogRepository.findTop4ByOrderByPublishDateDesc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper methods
    private String saveImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File rỗng");
        }

        try {
            // Tạo thư mục nếu chưa tồn tại
            Path folderPath = Paths.get(BLOG_IMAGE_DIR).toAbsolutePath();
            if (!folderPath.toFile().exists()) {
                folderPath.toFile().mkdirs();
            }

            // Lấy tên file và extension
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new RuntimeException("Tên file không hợp lệ");
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // Tạo tên file mới với timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = "blog_" + timestamp + extension;

            Path filePath = folderPath.resolve(fileName);

            // Lưu file
            file.transferTo(filePath.toFile());

            // Trả về đường dẫn tương đối
            return "/" + BLOG_IMAGE_DIR + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu ảnh: " + e.getMessage(), e);
        }
    }

    private void deleteImage(String imagePath) {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                // Lấy tên file từ path
                String fileName = Paths.get(imagePath).getFileName().toString();
                File file = new File(BLOG_IMAGE_DIR, fileName);
                
                if (file.exists() && file.isFile()) {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        System.err.println("Không thể xóa file: " + file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa ảnh: " + e.getMessage());
        }
    }

    private BlogDTO convertToDTO(Blog blog) {
        return BlogDTO.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .coverImagePath(blog.getCoverImagePath())
                .publishDate(blog.getPublishDate())
                .authorId(blog.getAuthor().getId())
                .authorName(blog.getAuthor().getFullName())
                .isPinned(blog.getIsPinned())
                .build();
    }
}
