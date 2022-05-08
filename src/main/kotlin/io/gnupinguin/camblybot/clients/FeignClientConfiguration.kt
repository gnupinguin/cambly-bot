package io.gnupinguin.camblybot.clients

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import feign.Client
import feign.Feign
import feign.Logger
import feign.Retryer
import feign.codec.Decoder
import feign.codec.Encoder
import feign.httpclient.ApacheHttpClient
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import java.util.concurrent.TimeUnit

//TODO support 404 decoding
class GmailFeignClientConfiguration {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    @Bean
    private fun feignLoggerLevel(): Logger.Level {
        return Logger.Level.FULL
    }

    @Bean
    private fun encoder(): Encoder {
        return JacksonEncoder(objectMapper)
    }

    @Bean
    private fun decoder(): Decoder {
        return JacksonDecoder(objectMapper)
    }

    @Bean
    fun feignBuilder(clientService: OAuth2AuthorizedClientService): Feign.Builder {
        return Feign.builder()
            .retryer(Retryer.NEVER_RETRY)
            .client(feignClient())
            .logLevel(feignLoggerLevel())
    }

    private fun feignClient(): Client {
        val builder = HttpClientBuilder.create()
            .setMaxConnTotal(64)
            .setMaxConnPerRoute(64)
            .setConnectionTimeToLive(10, TimeUnit.SECONDS)
        return ApacheHttpClient(builder.build())
    }

}
