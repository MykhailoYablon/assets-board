package com.assets.board.service;

import com.assets.board.model.ib.IBPosition;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PositionService {

    List<IBPosition> getAllIBPositions();

    void addPositions(MultipartFile ibPositionsFile);
}
