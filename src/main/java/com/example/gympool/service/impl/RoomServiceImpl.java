package com.example.gympool.service.impl;

import com.example.gympool.entity.ClassSchedule;
import com.example.gympool.entity.Room;
import com.example.gympool.entity.SchedulePattern;
import com.example.gympool.repository.ClassScheduleRepository;
import com.example.gympool.repository.RoomRepository;
import com.example.gympool.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Override
    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room Not Found"));
    }

    @Override
    public Room updateRoom(Long id, Room roomDetails) {
        Room existingRoom = getRoomById(id);

        existingRoom.setName(roomDetails.getName());
        existingRoom.setDescription(roomDetails.getDescription());
        existingRoom.setCapacity(roomDetails.getCapacity());

        return roomRepository.save(existingRoom);
    }

    @Override
    public void deleteRoom(Long id) {
        Room roomToDelete = getRoomById(id);
        roomRepository.delete(roomToDelete);
    }

    @Override
    public List<Room> getAvailableRoomsForPattern(SchedulePattern pattern) {
        List<Room> allRooms = roomRepository.findAll();

        if (allRooms.isEmpty()) return new ArrayList<>();

        Set<Long> busyRoomIds = new HashSet<>();

        LocalDate currentDate = pattern.getClassStartDate();
        List<DayOfWeek> days = Arrays.stream(pattern.getDaysOfWeek().split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(DayOfWeek::valueOf)
                .toList();

        while (!currentDate.isAfter(pattern.getClassEndDate())) {
            if (days.contains(currentDate.getDayOfWeek())) {
                LocalDateTime start = LocalDateTime.of(currentDate, pattern.getTimeStart());
                LocalDateTime end = LocalDateTime.of(currentDate, pattern.getTimeEnd());

                List<ClassSchedule> conflicts = classScheduleRepository.findOverlappingSchedules(start, end);

                for (ClassSchedule s : conflicts) {
                    if (s.getRoom() != null) {
                        busyRoomIds.add(s.getRoom().getId());
                    }
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        return allRooms.stream()
                .filter(room -> !busyRoomIds.contains(room.getId()))
                .collect(Collectors.toList());
    }
}