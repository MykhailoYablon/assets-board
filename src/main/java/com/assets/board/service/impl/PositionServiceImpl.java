package com.assets.board.service.impl;

import com.assets.board.entity.Position;
import com.assets.board.mapper.PositionMapper;
import com.assets.board.model.ib.IBPosition;
import com.assets.board.repository.PositionRepository;
import com.assets.board.service.IBFilesParser;
import com.assets.board.service.PositionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final IBFilesParser ibFilesParser;
    private final PositionRepository positionRepository;
    private final PositionMapper positionMapper;


    @Override
    public List<IBPosition> getAllIBPositionsStatistics() {
        List<Position> positions = positionRepository.findAll();
        return positionMapper.toDtoList(positions);
    }

    @Override
    public List<IBPosition> addPositions(MultipartFile ibPositionsFile) {
        List<IBPosition> dtos = ibFilesParser.parseIBPositions(ibPositionsFile);
        List<Position> entities = positionMapper.toEntityList(dtos);
        List<Position> savedEntities = positionRepository.saveAll(entities);
        return positionMapper.toDtoList(savedEntities);
    }
}
