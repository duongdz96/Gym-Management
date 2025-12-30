package com.example.gympool.controller;

import com.example.gympool.entity.PT;
import com.example.gympool.service.PTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pts")
public class PTController {

    @Autowired
    private PTService ptService;

    @PostMapping
    public PT create(@RequestBody PT pt) {
        return ptService.create(pt);
    }

    @GetMapping("/{id}")
    public PT getById(@PathVariable Long id) {
        return ptService.getById(id);
    }

    @GetMapping
    public List<PT> getAll() {
        return ptService.getAll();
    }

    @PutMapping("/{id}")
    public PT update(@PathVariable Long id, @RequestBody PT pt) {
        return ptService.update(id, pt);
    }

    @PutMapping("/{id}/delete")
    public void delete(@PathVariable Long id) {
        ptService.delete(id);
    }
}
