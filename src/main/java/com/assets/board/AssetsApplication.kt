package com.assets.board

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.util.*

@SpringBootApplication
open class AssetsApplication

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kyiv"))
    SpringApplication.run(AssetsApplication::class.java, *args)
}