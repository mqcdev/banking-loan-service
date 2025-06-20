package com.nttdata.banking.loan.infrastructure;

import com.nttdata.banking.loan.config.WebClientConfig;
import com.nttdata.banking.loan.model.Credit;
import com.nttdata.banking.loan.util.Constants;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class CreditRepository {

    @Value("${local.property.host.ms-credits}")
    private String propertyHostMsCredits;

    @Autowired
    ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    @CircuitBreaker(name = Constants.CREDIT_CB, fallbackMethod = "getDefaultCreditsByDocumentNumber")
    public Flux<Credit> findCreditsByDocumentNumber(String documentNumber) {

        log.info("Inicio----findCreditsByDocumentNumber-------: ");
        WebClientConfig webconfig = new WebClientConfig();
        Flux<Credit> alerts = webconfig.setUriData("http://" + propertyHostMsCredits + ":8084")
                .flatMap(d -> webconfig.getWebclient().get()
                        .uri("/api/credits/creditsDetails/" + documentNumber).retrieve()
                        .onStatus(HttpStatus::is4xxClientError, clientResponse -> Mono.error(new Exception("Error 400")))
                        .onStatus(HttpStatus::is5xxServerError, clientResponse -> Mono.error(new Exception("Error 500")))
                        .bodyToFlux(Credit.class)
                        // .transform(it -> reactiveCircuitBreakerFactory.create("parameter-service").run(it, throwable -> Flux.just(new Credit())))
                        .collectList()
                )
                .flatMapMany(iterable -> Flux.fromIterable(iterable));
        return alerts;
    }

    public Flux<Credit> getDefaultCreditsByDocumentNumber(String documentNumber, Exception e) {
        log.info("Inicio----getDefaultCreditsByDocumentNumber-------: ");
        return Flux.empty();
    }
}
