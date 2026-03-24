package br.com.luisf.fabricio.demos.shippingaggregator.service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.math.BigDecimal;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import br.com.luisf.fabricio.demos.shippingaggregator.api.ShippingQuoteOptionResponse;
import br.com.luisf.fabricio.demos.shippingaggregator.api.ShippingQuoteRequest;
import br.com.luisf.fabricio.demos.shippingaggregator.api.ShippingQuoteResponse;
import br.com.luisf.fabricio.demos.shippingaggregator.model.PartnerCode;

@ApplicationScoped
public class ShippingQuoteAggregationService {
    @Inject
    ShippingPartnerSimulator shippingPartnerSimulator;

    @Inject
    ShippingDiagnosticsService shippingDiagnosticsService;

    public ShippingQuoteResponse aggregate(ShippingQuoteRequest request) {
        validateRequest(request);
        long startedAt = System.nanoTime();
        shippingDiagnosticsService.registerQuote();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<ShippingQuote>> tasks = Arrays.stream(PartnerCode.values())
                    .map(partner -> (Callable<ShippingQuote>) () -> collectQuote(partner, request))
                    .toList();
            List<ShippingQuoteOptionResponse> options = executor.invokeAll(tasks).stream()
                    .map(this::awaitQuote)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(ShippingQuoteOptionResponse::price))
                    .toList();
            if (options.isEmpty()) {
                throw new IllegalStateException("Unable to aggregate shipping quotes from available partners");
            }
            long totalAggregationTimeMs = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
            return new ShippingQuoteResponse(
                    request.origin() + " -> " + request.destination(),
                    request.sku(),
                    request.quantity(),
                    request.orderValue(),
                    options,
                    totalAggregationTimeMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Virtual thread aggregation interrupted", e);
        }
    }

    private ShippingQuote collectQuote(PartnerCode partner, ShippingQuoteRequest request) {
        ShippingQuote quote = shippingPartnerSimulator.fetchQuote(partner, request);
        shippingDiagnosticsService.registerPartnerCall(partner, quote.latencyMs());
        return quote;
    }

    private ShippingQuoteOptionResponse awaitQuote(Future<ShippingQuote> future) {
        try {
            return getQuote(future.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Virtual thread result interrupted", e);
        } catch (ExecutionException e) {
            return null;
        }
    }

    private void validateRequest(ShippingQuoteRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (request.origin() == null || request.origin().strip().isEmpty()) {
            throw new IllegalArgumentException("origin must not be blank");
        }
        if (request.destination() == null || request.destination().strip().isEmpty()) {
            throw new IllegalArgumentException("destination must not be blank");
        }
        if (request.sku() == null || request.sku().strip().isEmpty()) {
            throw new IllegalArgumentException("sku must not be blank");
        }
        if (request.quantity() < 1) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
        if (request.orderValue() == null) {
            throw new IllegalArgumentException("orderValue must not be null");
        }
        if (request.orderValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("orderValue must be greater than zero");
        }
    }

    private ShippingQuoteOptionResponse getQuote(ShippingQuote quote) {
        return new ShippingQuoteOptionResponse(
                quote.partner(),
                quote.price(),
                quote.estimatedDays(),
                quote.serviceLevel(),
                quote.latencyMs());
    }
}
