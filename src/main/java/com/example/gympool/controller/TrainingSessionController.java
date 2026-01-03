package com.example.gympool.controller;

import com.example.gympool.entity.TrainingSession;
import com.example.gympool.entity.TrainingSession;
import com.example.gympool.service.TrainingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainingsession")
@RequiredArgsConstructor
public class TrainingSessionController {
    private final TrainingSessionService trainingSessionService;
    @GetMapping()
    public List<TrainingSession> findAll() {
        return trainingSessionService.getAllTrainingSession();
    }
    @GetMapping("/{id}")
    public TrainingSession findTrainingSessionById(@PathVariable("id") Long id) {
        return trainingSessionService.getTrainingSessionById(id);
    }
    @GetMapping("/member")
    public TrainingSession findTrainingSessionByCustomerName(@RequestParam("name") String name) {
        return trainingSessionService.getTrainingSessionByCustomerName(name);
    }
    @GetMapping("/pt")
    public TrainingSession findTrainingSessionByPtName(@RequestParam("name") String name) {
        return trainingSessionService.getTrainingSessionByPtName(name);
    }
    @PostMapping()
    public void addTrainingSession(@RequestBody TrainingSession TrainingSession) {
        trainingSessionService.addTrainingSession(TrainingSession);
    }
    @PostMapping("/start")
    public TrainingSession startSession(@PathVariable("id") Long appointmentId,
                                        @RequestParam("note") String note){
        return trainingSessionService.startSession(appointmentId, note);
    }
    @PostMapping("/end")
    TrainingSession endSession(@PathVariable("id") Long appointmentId,
                               @RequestParam("note") String note){
        return trainingSessionService.endSession(appointmentId, note);
    }

}
