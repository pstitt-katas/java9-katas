package kata.reactor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.function.BiFunction;

import static java.lang.String.format;

@Slf4j
public class Kata5_Threading {

    @Test
    void defaultThreads() {
        Mono<Threads> threadsMono = Flux.range(1, 5)
                .map(i -> Thread.currentThread().getName())
                .reduce(new Threads(), addThread());

        StepVerifier.create(threadsMono)
                .expectNextMatches(threads -> threads.size() == 1 && threadsEachMatch(threads, "main"))
                .expectComplete()
                .verify();
    }

    @Test
    void subscribeOnParallel() {
        Mono<Threads> threadsMono = Flux.range(1, 5)
                .map(i -> Thread.currentThread().getName())
                .reduce(new Threads(), addThread())
                .subscribeOn(Schedulers.parallel());

        StepVerifier.create(threadsMono)
                .expectNextMatches(threads -> threads.size() == 1 && threadsEachMatch(threads, "parallel-[0-9]+"))
                .expectComplete()
                .verify();
    }

    @Test
    void publishOnParallel() {
        Mono<Threads> threadsMono = Flux.range(1, 5)
                .publishOn(Schedulers.parallel())
                .map(i -> Thread.currentThread().getName())
                .reduce(new Threads(), addThread());

        StepVerifier.create(threadsMono)
                .expectNextMatches(threads -> threads.size() == 1 && threadsEachMatch(threads, "parallel-[0-9]+"))
                .expectComplete()
                .verify();
    }

    @Test
    void parallelFlux() {
        ParallelFlux<String> threadsFlux = Flux.range(1, 5)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(i -> Thread.currentThread().getName());
        String EXPECTED_THREAD_REGEX = "parallel-[0-9]+";

        StepVerifier.create(threadsFlux)
                .expectNextMatches(thread -> threadMatches(thread, EXPECTED_THREAD_REGEX))
                .expectNextMatches(thread -> threadMatches(thread, EXPECTED_THREAD_REGEX))
                .expectNextMatches(thread -> threadMatches(thread, EXPECTED_THREAD_REGEX))
                .expectNextMatches(thread -> threadMatches(thread, EXPECTED_THREAD_REGEX))
                .expectNextMatches(thread -> threadMatches(thread, EXPECTED_THREAD_REGEX))
                .expectComplete()
                .verify();
    }

    private boolean threadMatches(String thread, String regex) {
        log.info("Thread: {}", thread);
        return thread.matches(regex);
    }

    class Threads extends HashSet<String> {
    }

    private BiFunction<Threads, String, Threads> addThread() {
        return (threads, thread) -> {
            threads.add(thread);
            return threads;
        };
    }

    private boolean threadsEachMatch(Threads threads, String regex) {
        try {
            threads.stream().forEach(thread -> {
                log.info("Thread: {}", thread);
                if (!thread.matches(regex)) {
                    String error = format("Found thread %s, but expected it to contain %s", thread, regex);
                    throw new RuntimeException(error);
                }
            });
        } catch (RuntimeException x) {
            log.error(x.getMessage());
            return false;
        }
        return true;
    }
}
