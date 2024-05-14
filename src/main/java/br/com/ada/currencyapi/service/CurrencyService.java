package br.com.ada.currencyapi.service;

import java.math.BigDecimal;
import java.util.*;

import br.com.ada.currencyapi.domain.*;
import br.com.ada.currencyapi.domain.Currency;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import br.com.ada.currencyapi.exception.CoinNotFoundException;
import br.com.ada.currencyapi.exception.CurrencyException;
import br.com.ada.currencyapi.repository.CurrencyRepository;


@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    private final CurrencyClient currencyClient;


    public List<CurrencyResponse> get() {
        List<Currency> currencies = currencyRepository.findAll();
        List<CurrencyResponse> dtos = new ArrayList<>();

        currencies.forEach((currency) -> dtos.add(CurrencyResponse.builder()
                .label("%s - %s".formatted(currency.getId(), currency.getName()))
                .build()));

        return dtos;
    }

    public Long create(CurrencyRequest request) throws CurrencyException {
        Currency currency = currencyRepository.findByName(request.getName());

        if (Objects.nonNull(currency)) {
            throw new CurrencyException("Coin already exists");
        }

        Currency saved = currencyRepository.save(Currency.builder()
                .name(request.getName())
                .description(request.getDescription())
                .exchanges(request.getExchanges())
                .build());
        return saved.getId();
    }

    public void delete(Long id) {
        Currency from = currencyRepository.findById(id).orElseThrow(
                () -> new CoinNotFoundException(String.format("Coin not found: %s", id))
        );

        currencyRepository.deleteById(from.getId());
    }

    public ConvertCurrencyResponse convert(ConvertCurrencyRequest request) throws CoinNotFoundException {
        BigDecimal amount = getAmount(request);
        return ConvertCurrencyResponse.builder()
                .amount(amount)
                .build();

    }

    private BigDecimal getAmount(ConvertCurrencyRequest request) throws CoinNotFoundException {
        Currency from = currencyRepository.findByName(request.getFrom());

        if (Objects.isNull(from)) {
            throw new CoinNotFoundException(String.format("Coin not found: %s", request.getFrom()));
        }

        BigDecimal exchange = from.getExchanges().get(request.getTo());

        if (Objects.isNull(exchange)) {
            throw new CoinNotFoundException(String.format("Exchange %s not found for %s", request.getTo(), request.getFrom()));
        }

        return request.getAmount().multiply(exchange);
    }

    public ConvertCurrencyResponse convertWithAwesomeApi(ConvertCurrencyRequest request) throws CoinNotFoundException {
        BigDecimal amount = getAmountWithAwesomeApi(request);
        return ConvertCurrencyResponse.builder()
                .amount(amount)
                .build();

    }

    private BigDecimal getAmountWithAwesomeApi(ConvertCurrencyRequest request) throws CoinNotFoundException {
        Currency from = currencyRepository.findByName(request.getFrom());

        if (Objects.isNull(from)) {
            throw new CoinNotFoundException(String.format("Coin not found: %s", request.getFrom()));
        }

        String tag = request.getTo() + "-" + request.getFrom();
        BigDecimal exchange = currencyClient.getCurrency(tag).get(tag.replace("-", "")).low();

        if (Objects.isNull(exchange)) {
            throw new CoinNotFoundException(String.format("Exchange %s not found for %s", request.getTo(), request.getFrom()));
        }

        return request.getAmount().multiply(exchange);
    }
}
