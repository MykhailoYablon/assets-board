package com.assets.board.model.ai;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiPosition {
    private String symbol;
    private String quantity;
    private String currentPrice;
    private String averagePurchasePrice;
}
