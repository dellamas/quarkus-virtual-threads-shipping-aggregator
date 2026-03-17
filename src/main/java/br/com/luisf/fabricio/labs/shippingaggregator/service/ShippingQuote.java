package br.com.luisf.fabricio.demos.shippingaggregator.service;

import java.math.BigDecimal;

import br.com.luisf.fabricio.demos.shippingaggregator.model.PartnerCode;

public record ShippingQuote(PartnerCode partner, BigDecimal price, int estimatedDays, String serviceLevel, long latencyMs) {
}
