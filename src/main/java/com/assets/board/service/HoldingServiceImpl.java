package com.assets.board.service;

import com.assets.board.model.ai.Holding;
//import com.assets.board.repository.HoldingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HoldingServiceImpl implements HoldingService {

//    private final HoldingRepository repository;

    @Override
    public List<Holding> getAllHoldings() {
        return List.of();
    }
}
