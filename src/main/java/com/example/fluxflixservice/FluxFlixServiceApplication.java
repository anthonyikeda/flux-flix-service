package com.example.fluxflixservice;

import lombok.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

@SpringBootApplication
public class FluxFlixServiceApplication {

    @Bean
    CommandLineRunner demoData(MovieRepository movieRepository) {

        return args -> {
            movieRepository.deleteAll().thenMany(
            Flux.just("The Silence of the Lamdas", "Æon Flux", "Meet the Fluxers",
                    "The Publishers", "Enter the Mono<Void>", "Y Tu Mono Tambien",
                    "Blade Reactor", "Back to the Future<>")
                    .map(Movie::new)
                    .flatMap(movieRepository::save))
                    .subscribe(System.out::println);
        };
    }

    @Bean
    RouterFunction<ServerResponse> routerFunction(MovieHandler handler) {
        return route(GET("/movies"), handler::all)
                .andRoute(GET("/movies/{id}"), handler::byId)
                .andRoute(GET("/movies/{id}/events"), handler::events);
    }

	public static void main(String[] args) {
		SpringApplication.run(FluxFlixServiceApplication.class, args);
	}
}

@Component
class MovieHandler {
    private final MovieService movieService;

    MovieHandler(MovieService movieService) {
        this.movieService = movieService;
    }

    public Mono<ServerResponse> all(ServerRequest request) {
        return ServerResponse.ok()
                .body(this.movieService.getAllMovies(), Movie.class);

    }

    public Mono<ServerResponse> byId(ServerRequest request) {
        return ServerResponse.ok()
                .body(this.movieService.getMovieById(request.pathVariable("id")), Movie.class);
    }

    public Mono<ServerResponse> events(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(this.movieService.getEvents(request.pathVariable("id")), MovieEvent.class);
    }
}

/*
@RestController
@RequestMapping("/movies")
class MovieController {
    private final MovieService movieService;

    MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    Flux<Movie> all() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    Mono<Movie> getMovieById(@PathVariable String id) {
        return movieService.getMovieById(id);
    }

    @GetMapping(value = "/{id}/events", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    Flux<MovieEvent> events(@PathVariable String id) {
        return movieService.getEvents(id);
    }

}
*/

@Service
class MovieService {
    private MovieRepository movieRepository;

    MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    Flux<Movie> getAllMovies() {
        return this.movieRepository.findAll();
    }

    Mono<Movie> getMovieById(String id) {
        return this.movieRepository.findById(id);
    }

    Flux<MovieEvent> getEvents(String movieId) {
        return Flux.<MovieEvent>generate(sink -> sink.next(new MovieEvent(movieId, new Date())))
                .delayElements(Duration.ofSeconds(1));
    }
}

@Repository
interface MovieRepository extends ReactiveMongoRepository<Movie, String> {}

@Data
@NoArgsConstructor
@AllArgsConstructor
class MovieEvent {
    String movieId;
    private Date when;
}

@Document
@Data
@NoArgsConstructor
@RequiredArgsConstructor
class Movie {
    @Id
	private String id;

    @NonNull
	private String title;

}
