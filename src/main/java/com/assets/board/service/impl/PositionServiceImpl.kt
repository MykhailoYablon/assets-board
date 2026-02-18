package com.assets.board.service.impl

import com.assets.board.mapper.PositionMapper
import com.assets.board.model.ib.IBPosition
import com.assets.board.repository.PositionRepository
import com.assets.board.service.PositionService
import com.assets.board.service.parser.IBFilesParser
import lombok.AllArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Slf4j
@Service
@AllArgsConstructor
class PositionServiceImpl(
    val ibFilesParser: IBFilesParser,
    val positionRepository: PositionRepository,
    val positionMapper: PositionMapper
) : PositionService {


    override fun allIBPositionsStatistics(): MutableList<IBPosition?>? {
        val positions = positionRepository.findAll()
        return positionMapper.toDtoList(positions)
    }

    override fun addPositions(ibPositionsFile: MultipartFile): MutableList<IBPosition?>? {
        val dtos = ibFilesParser.parseIBPositions(ibPositionsFile)
        val entities = positionMapper.toEntityList(dtos)
        val savedEntities = positionRepository.saveAll(entities)
        return positionMapper.toDtoList(savedEntities)
    }
}
