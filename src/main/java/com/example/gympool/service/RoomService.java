package com.example.gympool.service;

import com.example.gympool.entity.Room;
import com.example.gympool.entity.SchedulePattern;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomService {
    Room createRoom(Room room);
    List<Room> getAllRooms();
    Room getRoomById(Long id);
    Room updateRoom(Long id, Room roomDetails);
    void deleteRoom(Long id);
    List<Room> getAvailableRoomsForPattern(SchedulePattern pattern);
    List<Room> getAvailableRooms(LocalDateTime startTime, LocalDateTime endTime, Long excludeSessionId);
}