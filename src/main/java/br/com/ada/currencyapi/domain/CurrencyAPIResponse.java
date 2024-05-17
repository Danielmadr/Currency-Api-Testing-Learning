package br.com.ada.currencyapi.domain;


import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CurrencyAPIResponse(
        String code,
        String codein,
        String name,
        BigDecimal high,
        BigDecimal low,
        String varBid,
        String pctChange,
        BigDecimal bid,
        BigDecimal ask,
        String timestamp,
        String create_date
) {
}
