package io.gnupinguin.camblybot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties
class CamblyBotApplication

fun main(args: Array<String>) {
    runApplication<CamblyBotApplication>(*args)
}
