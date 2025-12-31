package com.assets.board.model.enums;

public enum Endpoint {

    //NBU API
    NBU_EXCHANGE("https://bank.gov.ua/NBUStatService/v1/statdirectory/exchangenew"),

    //TWELVE DATA URLS

    TIME_SERIES("https://api.twelvedata.com/time_series"),
    TWELVE_QUOTE("https://api.twelvedata.com/quote"),
    TWELVE_PRICE("https://api.twelvedata.com/price");


    private String url;

    Endpoint(String url) {
        this.url = url;
    }

    public String url() {
        return url;
    }

}