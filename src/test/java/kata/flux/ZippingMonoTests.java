package kata.flux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;

import java.time.Duration;

public class ZippingMonoTests {

    @Test
    void zip() {
        Integer INPUT1 = 1;
        Integer INPUT2 = 2;
        Mono<Integer> mono1 = Mono.just(INPUT1);
        Mono<Integer> mono2 = Mono.just(INPUT2);
        Mono<Tuple2<Integer, Integer>> zippedMono = mono1.zipWith(mono2);

        StepVerifier.create(zippedMono)
                .expectNextMatches(t -> t.getT1() == INPUT1 && t.getT2() == INPUT2)
                .expectComplete()
                .verify();
    }

    @Test
    void zipWithDelay() {
        Integer INPUT1 = 1;
        Integer INPUT2 = 2;
        Mono<Integer> mono1 = Mono.just(INPUT1).delayElement(Duration.ofSeconds(1));
        Mono<Integer> mono2 = Mono.just(INPUT2).delayElement(Duration.ofSeconds(5));
        Mono<Tuple2<Integer, Integer>> zippedMono = mono1.zipWith(mono2);

        StepVerifier.create(zippedMono)
                .expectNextMatches(t -> t.getT1() == INPUT1 && t.getT2() == INPUT2)
                .expectComplete()
                .verify();
    }
}
