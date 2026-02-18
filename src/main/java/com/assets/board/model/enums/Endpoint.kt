package com.assets.board.model.enums

enum class Endpoint(url: String) {
    //NBU API
    NBU_EXCHANGE("https://bank.gov.ua/NBUStatService/v1/statdirectory/exchangenew"),  //TWELVE DATA URLS

    TIME_SERIES("https://api.twelvedata.com/time_series"),
    TWELVE_QUOTE("https://api.twelvedata.com/quote"),
    TWELVE_PRICE("https://api.twelvedata.com/price");


    private val url: String?

    init {
        this.url = url
    }

    fun url(): String? {
        return url
    }
}