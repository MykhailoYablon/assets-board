package com.assets.board.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import lombok.Data
import lombok.Setter

@JsonIgnoreProperties(ignoreUnknown = true)
class NbuExchangeRate {
    @JsonProperty("rate")
    var rate: String? = null
}
