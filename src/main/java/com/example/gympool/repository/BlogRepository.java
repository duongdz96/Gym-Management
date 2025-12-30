package com.example.gympool.repository;

import com.example.gympool.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    
    // Lấy tất cả blog, sắp xếp theo ghim trước, sau đó mới nhất
    List<Blog> findAllByOrderByIsPinnedDescPublishDateDesc();
    
    // Lấy các blog được ghim
    List<Blog> findByIsPinnedTrueOrderByPublishDateDesc();
    
    // Lấy 4 blog mới nhất cho trang chủ
    List<Blog> findTop4ByOrderByPublishDateDesc();
}
