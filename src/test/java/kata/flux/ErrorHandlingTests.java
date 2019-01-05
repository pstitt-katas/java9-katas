package kata.flux;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ErrorHandlingTests {

    @Test
    void errorEmittedByMono() {
        String ERROR = "error";
        Mono mono = Mono.error(new RuntimeException(ERROR));

        StepVerifier.create(mono)
                .expectErrorMatches(x -> x instanceof RuntimeException && x.getMessage().equals(ERROR))
                .verify();
    }

    @Test
    void errorEmittedByMonoAndHandledWithDefaultValue() {
        String ERROR = "error";
        Integer DEFAULT_VALUE = 99;
        Mono mono = Mono.error(new RuntimeException(ERROR))
                .onErrorReturn(DEFAULT_VALUE);

        StepVerifier.create(mono)
                .expectNext(DEFAULT_VALUE)
                .expectComplete()
                .verify();
    }

    @Test
    void errorEmittedByNestedMono() {
        Integer INPUT = 1;
        String ERROR = "error";
        Mono mono = Mono.just(INPUT)
                .flatMap(i -> Mono.error(new RuntimeException(ERROR)));

        StepVerifier.create(mono)
                .expectErrorMatches(x -> x instanceof RuntimeException && x.getMessage().equals(ERROR))
                .verify();
    }

    @Test
    void errorEmittedAndHandledByNestedMono() {
        Integer INPUT = 1;
        String DEFAULT_VALUE = "99";
        String ERROR = "error";
        Mono mono = Mono.just(INPUT)
                .flatMap(i ->
                        Mono.error(new RuntimeException(ERROR))
                                .onErrorReturn(DEFAULT_VALUE));

        StepVerifier.create(mono)
                .expectNext(DEFAULT_VALUE)
                .expectComplete()
                .verify();
    }

    @Test
    void errorEmittedByNestedMonoAndHandledAtOuterMono() {
        Integer INPUT = 1;
        String DEFAULT_VALUE = "99";
        String ERROR = "error";
        Mono mono = Mono.just(INPUT)
                .flatMap(i -> Mono.error(new RuntimeException(ERROR)))
                .onErrorReturn(DEFAULT_VALUE);

        StepVerifier.create(mono)
                .expectNext(DEFAULT_VALUE)
                .expectComplete()
                .verify();
    }
}
