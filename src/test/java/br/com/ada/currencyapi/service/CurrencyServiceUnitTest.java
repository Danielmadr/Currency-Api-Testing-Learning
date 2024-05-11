package br.com.ada.currencyapi.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.ada.currencyapi.domain.*;
import static org.junit.jupiter.api.Assertions.*;
import br.com.ada.currencyapi.exception.CoinNotFoundException;
import br.com.ada.currencyapi.exception.CurrencyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.ada.currencyapi.repository.CurrencyRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceUnitTest {

    @InjectMocks
    private CurrencyService currencyService;

    @Mock
    private CurrencyRepository currencyRepository;

    @Test
    void testGet() {
        List<Currency> list = new ArrayList<>();
        list.add(Currency.builder()
                .id(1L)
                .name("LCS")
                .description("Moeda do lucas")
                .build());
        list.add(Currency.builder()
                .id(2L)
                .name("YAS")
                .description("Moeda da yasmin")
                .build());

        when(currencyRepository.findAll()).thenReturn(list);

        List<CurrencyResponse> responses = currencyService.get();
        assertNotNull(responses);
        Assertions.assertEquals(2, responses.size());
        Assertions.assertEquals("1 - LCS", responses.get(0).getLabel());
        Assertions.assertEquals("2 - YAS", responses.get(1).getLabel());
    }

    @Test
    void testCreateCurrencySuccess() throws CurrencyException {
        CurrencyRequest request = new CurrencyRequest("INR", "Indian Rupee", new HashMap<>());

        when(currencyRepository.findByName("INR")).thenReturn(null);
        when(currencyRepository.save(any(Currency.class))).thenReturn(new Currency(1L,"INR", "Indian Rupee", new HashMap<>()));

        Long id = currencyService.create(request);
        assertNotNull(id);
    }

    @Test
    void testCreateCurrencyFailure() {
        Currency existingCurrency = new Currency(1L,"INR", "Indian Rupee", new HashMap<>());
        when(currencyRepository.findByName("INR")).thenReturn(existingCurrency);

        CurrencyRequest request = new CurrencyRequest("INR", "Indian Rupee", new HashMap<>());

        assertThrows(CurrencyException.class, () -> currencyService.create(request));
    }

    @Test
    public void testDeleteCurrency2() {
        Long id = 1L;
        CurrencyService service = Mockito.mock(CurrencyService.class);
        service.delete(id);
        Mockito.verify(service).delete(id);
    }

    @Test
    void testConvertCurrencySuccess() throws CoinNotFoundException {
        ConvertCurrencyRequest request = new ConvertCurrencyRequest("INR", "YAS", new BigDecimal("100"));
        HashMap<String, BigDecimal> exchanges = new HashMap<>();
        exchanges.put("YAS", new BigDecimal("0.85"));
        Currency fromCurrency = new Currency(1L,"INR", "Indian Rupee", exchanges);

        when(currencyRepository.findByName("INR")).thenReturn(fromCurrency);

        ConvertCurrencyResponse response = currencyService.convert(request);
        assertNotNull(response);
        assertEquals(new BigDecimal ("85.00"), response.getAmount());
    }

    @Test
    void testConvertCurrencyFailure() {
        ConvertCurrencyRequest request = new ConvertCurrencyRequest("INR", "YAS", new BigDecimal("100"));
        when(currencyRepository.findByName("INR")).thenReturn(null);

        assertThrows(CoinNotFoundException.class, () -> currencyService.convert(request));
    }
}