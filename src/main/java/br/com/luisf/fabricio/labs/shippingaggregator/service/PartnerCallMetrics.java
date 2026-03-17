package br.com.luisf.fabricio.demos.shippingaggregator.service;

import java.util.concurrent.atomic.AtomicLong;

public class PartnerCallMetrics {
    private final AtomicLong calls = new AtomicLong();
    private final AtomicLong totalLatencyMs = new AtomicLong();

    public void register(long latencyMs) {
        calls.incrementAndGet();
        totalLatencyMs.addAndGet(latencyMs);
    }

    public long calls() {
        return calls.get();
    }

    public long averageLatencyMs() {
        long currentCalls = calls.get();
        if (currentCalls == 0) {
            return 0;
        }
        return totalLatencyMs.get() / currentCalls;
    }
}
