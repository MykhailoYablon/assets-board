package com.assets.board.controller;

import com.assets.board.model.ib.IBPosition;
import com.assets.board.service.AIService;
import com.assets.board.service.PositionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/positions")
@AllArgsConstructor
public class PositionController {

    private final PositionService positionService;
    private final AIService aiService;

    @PostMapping
    public List<IBPosition> addPositions(@RequestParam("file") MultipartFile ibPositionsFile) {
        //Save File as entity for historic comparison?
        //Show only latest positions?
        return positionService.addPositions(ibPositionsFile);
    }

    @GetMapping("/all")
    public List<IBPosition> getAllPositions() {
        return positionService.getAllIBPositionsStatistics();
    }

    @GetMapping("/ai")
    public String getAiSummary() {
        return aiService.analyzePositions();
    }

}