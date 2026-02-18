package com.assets.board.model.ai

data class Holding (
    private var type: String? = null,
    private var cashBalance: String? = null,
    private var netWorth: String? = null,
    private var timeWeightedReturn: String? = null,
    private var unrealizedProfitLoss: String? = null,
    private var userDefinedAttributes: UserDefinedAttributes? = null,
    private var aiPositions: MutableList<AiPosition?>? = null
)

