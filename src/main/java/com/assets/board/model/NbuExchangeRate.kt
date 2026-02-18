package com.assets.board.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Setter;

@Setter
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NbuExchangeRate {

    @JsonProperty("rate")
    String rate;


}
