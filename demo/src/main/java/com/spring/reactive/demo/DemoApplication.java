package com.spring.reactive.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;

import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
public class DemoApplication {

    @Bean
    RouterFunction<?> router(MovieService movieService) {
        return route(GET("/movies"),
                request -> ok().body(movieService.all(), Movie.class))
                .andRoute(GET("/movies/{id}"),
                        request -> ok().body(movieService.byId(request.pathVariable("id")), Movie.class))
                .andRoute(GET("/movies/{id}/events"),
                        request -> ok()
                                .contentType(MediaType.TEXT_EVENT_STREAM)
                                .body(movieService.byId(request.pathVariable("id"))
                                        .flatMapMany(movieService::streamStreams), MovieEvent.class)
                );
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    CommandLineRunner cml(MovieRepository movieRepository) {
        return args -> movieRepository.deleteAll()
                .subscribe(null, null, () -> {
                    Stream.of("Aeon Flux", "Enter the Mono<Void", "The Fluxinator"
                            , "Silence of the lambdas", " Reactive Mongos on Pland",
                            "Y Tu Mono Tambien", "Attack of the Fluxes",
                            "Back to the future")
                            .map(name -> new Movie(UUID.randomUUID().toString(),
                                    name, randomGenre()))
                            .forEach(movie ->
                                    movieRepository.save(movie)
                                            .subscribe(System.out::println));
                });
    }

    private String randomGenre() {
        String[] genres = "horror,romcom, drama, action, documentary".split(",");
        return genres[new Random().nextInt(genres.length)];
    }

}
