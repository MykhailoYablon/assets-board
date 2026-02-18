package com.assets.board.model.ai

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@NoArgsConstructor
@AllArgsConstructor
data class AiPosition (
    private var symbol: String? = null,
    private var quantity: String? = null,
    private var currentPrice: String? = null,
    private var averagePurchasePrice: String? = null
)