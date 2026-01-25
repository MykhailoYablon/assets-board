package com.assets.board.service;

import com.assets.board.model.ib.IBPosition;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class PositionServiceImpl implements PositionService {

    private IBFilesParser ibFilesParser;

    @Override
    public List<IBPosition> getAllIBPositions() {
        return List.of();
    }

    @Override
    public void addPositions(MultipartFile ibPositionsFile) {
        List<IBPosition> ibPositions = ibFilesParser.parseIBPositions(ibPositionsFile);


    }

}
