package com.assets.board.controller

import com.assets.board.model.ib.IBPosition
import com.assets.board.service.PositionService
import com.assets.board.service.impl.AIService
import lombok.AllArgsConstructor
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@CrossOrigin(origins = ["*"])
@RestController
@RequestMapping("/positions")
@AllArgsConstructor
class PositionController(
    val positionService: PositionService,
    val aiService: AIService
) {
    @PostMapping
    fun addPositions(@RequestParam("file") ibPositionsFile: MultipartFile): MutableList<IBPosition?>? {
        //Save File as entity for historic comparison?
        //Show only latest positions?
        return positionService.addPositions(ibPositionsFile)
    }

    @get:GetMapping("/all")
    val allPositions: MutableList<IBPosition?>?
        get() = positionService.allIBPositionsStatistics()

    @get:GetMapping("/ai")
    val aiSummary: String?
        get() = aiService.analyzePositions()
}