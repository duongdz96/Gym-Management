package com.example.gympool.repository;

import com.example.gympool.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE r.id NOT IN (" +
            "  SELECT s.room.id FROM ClassSchedule s " +
            "  WHERE s.status != 'CANCELLED' " +
            "  AND (" +
            "       (s.startTime < :endTime AND s.endTime > :startTime)" + // Logic check chồng lấn thời gian
            "  )" +
            "  AND (:excludeSessionId IS NULL OR s.id != :excludeSessionId)" + // Bỏ qua session hiện tại nếu đang update
            ")")
    List<Room> findAvailableRooms(@Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime,
                                  @Param("excludeSessionId") Long excludeSessionId);
}