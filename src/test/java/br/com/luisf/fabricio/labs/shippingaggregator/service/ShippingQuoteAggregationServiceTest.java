package br.com.luisf.fabricio.demos.shippingaggregator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.luisf.fabricio.demos.shippingaggregator.api.ShippingQuoteRequest;
import br.com.luisf.fabricio.demos.shippingaggregator.model.PartnerCode;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ShippingQuoteAggregationServiceTest {

    @Test
    void shouldKeepHealthyQuotesWhenOnePartnerFails() {
        ShippingQuoteAggregationService service = new ShippingQuoteAggregationService();
        service.shippingPartnerSimulator = new FailingShippingPartnerSimulator();
        service.shippingDiagnosticsService = new ShippingDiagnosticsService();

        var response = service.aggregate(new ShippingQuoteRequest(
                "sao-paulo-sp",
                "belo-horizonte-mg",
                "SKU-9001",
                3,
                new BigDecimal("799.90")));

        assertEquals(2, response.options().size());
        assertEquals("ECONOSHIP", response.options().get(0).partner().name());
        assertEquals("PICKNPACK", response.options().get(1).partner().name());
    }

    static class FailingShippingPartnerSimulator extends ShippingPartnerSimulator {
        @Override
        public ShippingQuote fetchQuote(PartnerCode partner, ShippingQuoteRequest request) {
            if (partner == PartnerCode.FASTBOX) {
                throw new IllegalStateException("partner timeout");
            }
            return super.fetchQuote(partner, request);
        }
    }

    @Test
    void shouldRejectInvalidQuantityBeforeStartingPartnerCalls() {
        ShippingQuoteAggregationService service = new ShippingQuoteAggregationService();
        service.shippingPartnerSimulator = new ShippingPartnerSimulator();
        service.shippingDiagnosticsService = new ShippingDiagnosticsService();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.aggregate(new ShippingQuoteRequest(
                        "sao-paulo-sp",
                        "belo-horizonte-mg",
                        "SKU-9001",
                        0,
                        new BigDecimal("799.90"))));

        assertEquals("quantity must be greater than zero", exception.getMessage());
    }

}
