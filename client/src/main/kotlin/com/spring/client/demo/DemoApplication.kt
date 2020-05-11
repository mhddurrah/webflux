package com.spring.client.demo

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient

@SpringBootApplication
class DemoApplication {
    @Bean
    fun client(): WebClient {
        return WebClient.create()
    }

    @Bean
    fun demo(client: WebClient): CommandLineRunner {
        return CommandLineRunner {
            client().get()
                    .uri("http://localhost:8080/movies")
                    .exchange()
                    .flatMapMany { response -> response.bodyToFlux(Movie::class.java) }
                    .filter { m -> m.title.contains("Silence of the lambda") }
                    .subscribe { movie ->
                        client.get().uri("http://localhost:8080/movies/{id}/events", movie.id)
                                .exchange().flatMapMany { response -> response.bodyToFlux(MovieEvent::class.java) }
                                .subscribe(::println)
                    }


            /*client().get()
                    .uri("http://localhost:8080/movies/76adeb85-9d8c-40c2-91ed-73f17935c7b3")
                    .exchange()
                    .flatMap { response -> response.bodyToMono(Movie::class.java) }
                    .subscribe(::println)*/
        }
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
