package com.assets.board.model.ai;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Entity
//@Table(name = "holding")
public class Holding {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    private String type;
    private String cashBalance;
    private String netWorth;
    private String timeWeightedReturn;
    private String unrealizedProfitLoss;
    private UserDefinedAttributes userDefinedAttributes;
    private List<Position> positions;
}

