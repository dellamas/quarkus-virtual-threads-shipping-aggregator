package br.com.luisf.fabricio.labs.shippingaggregator.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.enterprise.context.ApplicationScoped;

import br.com.luisf.fabricio.labs.shippingaggregator.api.PartnerDiagnosticsResponse;
import br.com.luisf.fabricio.labs.shippingaggregator.api.ShippingDiagnosticsResponse;
import br.com.luisf.fabricio.labs.shippingaggregator.model.PartnerCode;

@ApplicationScoped
public class ShippingDiagnosticsService {
    private final AtomicLong totalQuotes = new AtomicLong();
    private final Map<PartnerCode, PartnerCallMetrics> partnerMetrics = new ConcurrentHashMap<>();

    public ShippingDiagnosticsService() {
        Arrays.stream(PartnerCode.values()).forEach(partner -> partnerMetrics.put(partner, new PartnerCallMetrics()));
    }

    public void registerQuote() {
        totalQuotes.incrementAndGet();
    }

    public void registerPartnerCall(PartnerCode partner, long latencyMs) {
        partnerMetrics.get(partner).register(latencyMs);
    }

    public ShippingDiagnosticsResponse snapshot() {
        List<PartnerDiagnosticsResponse> partners = Arrays.stream(PartnerCode.values())
                .map(partner -> new PartnerDiagnosticsResponse(
                        partner,
                        partnerMetrics.get(partner).calls(),
                        partnerMetrics.get(partner).averageLatencyMs()))
                .toList();
        return new ShippingDiagnosticsResponse(totalQuotes.get(), partners);
    }
}
