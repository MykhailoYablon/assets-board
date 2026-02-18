package com.assets.board.service

import com.assets.board.model.ib.IBPosition
import org.springframework.web.multipart.MultipartFile

interface PositionService {
    fun allIBPositionsStatistics(): MutableList<IBPosition?>?

    fun addPositions(ibPositionsFile: MultipartFile): MutableList<IBPosition?>?
}
