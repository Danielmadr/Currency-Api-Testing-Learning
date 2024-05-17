package br.com.ada.currencyapi.service;

import br.com.ada.currencyapi.domain.*;
import br.com.ada.currencyapi.domain.Currency;
import br.com.ada.currencyapi.exception.CoinNotFoundException;
import br.com.ada.currencyapi.exception.CurrencyException;
import br.com.ada.currencyapi.repository.CurrencyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;


@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

  @Mock
  private CurrencyRepository currencyRepository;

  @Mock
  private CurrencyClient currencyClient;

  @InjectMocks
  private CurrencyService currencyService;

  private final List<Currency> coins = new ArrayList<>();

  @BeforeEach
  void setUp() {
    coins.add(Currency.builder().id(1L).name("DMA").description("Domain Coin").exchanges(new HashMap<>()).build());
    coins.add(Currency.builder().id(2L).name("BTC").description("Bitcoin").exchanges(new HashMap<>()).build());
  }

  @Test
  @DisplayName("Get Success")
  void getSuccess() {
    Mockito.when(currencyRepository.findAll()).thenReturn(coins);

    List<CurrencyResponse> responses = currencyService.get();

    Assertions.assertEquals(2, responses.size());
    Assertions.assertEquals("1 - DMA", responses.get(0).getLabel());
    Assertions.assertEquals("2 - BTC", responses.get(1).getLabel());
  }

  @Test
  @DisplayName("Get Is Empty")
  void getIsEmpty() {
    Mockito.when(currencyRepository.findAll()).thenReturn(new ArrayList<>());

    List<CurrencyResponse> responses = currencyService.get();
    Assertions.assertEquals(0, responses.size());
  }

  @Test
  @DisplayName("Create Currency that already exists")
  void createAlreadyExists() {
    Mockito.when(currencyRepository.findByName(Mockito.anyString())).thenReturn(coins.get(0));

    CurrencyRequest request = CurrencyRequest.builder().name("DMA").description("Domain Coin").exchanges(new HashMap<>()).build();

    Assertions.assertThrows(CurrencyException.class, () -> currencyService.create(request));
  }

  @Test
  @DisplayName("Create Currency Success")
  void createSuccess() {
    Mockito.when(currencyRepository.findByName(Mockito.anyString())).thenReturn(null);
    Mockito.when(currencyRepository.save(Mockito.any(Currency.class))).thenReturn(coins.get(0));

    CurrencyRequest request = CurrencyRequest.builder().name("DMA").description("Domain Coin").exchanges(new HashMap<>()).build();

    Assertions.assertEquals(1L, currencyService.create(request));
  }

  @Test
  @DisplayName("Delete Currency Success")
  void deleteSuccess() {
    Mockito.when(currencyRepository.findById(1L)).thenReturn(Optional.ofNullable(coins.get(0)));
    currencyService.delete(1L);
    Mockito.verify(currencyRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
  }

  @Test
  @DisplayName("Try Delete Currency that does not exist")
  void deleteNotExists() {
    Mockito.when(currencyRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

    CoinNotFoundException exception = Assertions.assertThrows(CoinNotFoundException.class, () -> currencyService.delete(1L));
    Assertions.assertEquals("Coin not found: 1", exception.getMessage());
  }

  @Test
  @DisplayName("Convert Success")
  void convert() {
    coins.get(0).getExchanges().put("BTC", BigDecimal.TEN);

    ConvertCurrencyRequest request = ConvertCurrencyRequest.builder().to("BTC").from("DMA").amount(BigDecimal.ONE).build();

    Mockito.when(currencyRepository.findByName("DMA")).thenReturn(coins.get(0));

    Assertions.assertEquals(BigDecimal.TEN, currencyService.convert(request).getAmount());

  }

  @Test
  @DisplayName("Exchange not found for coin")
  void convertExchangeNotFound() {
    ConvertCurrencyRequest request = ConvertCurrencyRequest.builder().to("BTC").from("DMA").amount(BigDecimal.ONE).build();

    Mockito.when(currencyRepository.findByName("DMA")).thenReturn(coins.get(0));

    CoinNotFoundException exception = Assertions.assertThrows(CoinNotFoundException.class, () -> currencyService.convert(request));
    Assertions.assertEquals("Exchange BTC not found for DMA", exception.getMessage());

  }

  @Test
  @DisplayName("Convert coin not found")
  void convertCoinNotFound() {

    ConvertCurrencyRequest request = ConvertCurrencyRequest.builder().to("BTC").from("DMA").amount(BigDecimal.ONE).build();

    CoinNotFoundException exception = Assertions.assertThrows(CoinNotFoundException.class, () -> currencyService.convert(request));
    Assertions.assertEquals("Coin not found: DMA", exception.getMessage());
  }

  @Test
  @DisplayName("Convert Success")
  void convertExchangeComesFromAPI() {
    ConvertCurrencyRequest request = ConvertCurrencyRequest.builder().to("USD").from("BRL").amount(BigDecimal.ONE).build();
    Map<String, CurrencyAPIResponse> response =
            Map.of(
                    "USDBRL",
                    CurrencyAPIResponse.builder().bid(BigDecimal.TEN).build()
            );


    Mockito.when(currencyRepository.findByName(Mockito.anyString())).thenReturn(coins.get(0));
    Mockito.when(currencyClient.getCurrency(Mockito.anyString())).thenReturn(response);

    Assertions.assertEquals(BigDecimal.TEN, currencyService.convertWithAwesomeApi(request).getAmount());
    Mockito.verify(currencyRepository, Mockito.times(1)).findByName(Mockito.anyString());
  }
  @Test
  @DisplayName("Exchange not found for coin")
  void convertExchangeNotFoundWithAPI() {
    ConvertCurrencyRequest request = ConvertCurrencyRequest.builder().to("BTC").from("DMA").amount(BigDecimal.ONE).build();

    Mockito.when(currencyRepository.findByName("DMA")).thenReturn(coins.get(0));

    CoinNotFoundException exception = Assertions.assertThrows(CoinNotFoundException.class, () -> currencyService.convertWithAwesomeApi(request));
    Assertions.assertEquals("Exchange BTC not found for DMA", exception.getMessage());

  }

  @Test
  @DisplayName("Convert coin not found")
  void convertCoinNotFoundWithAPI() {

    ConvertCurrencyRequest request = ConvertCurrencyRequest.builder().to("BTC").from("DMA").amount(BigDecimal.ONE).build();

    CoinNotFoundException exception = Assertions.assertThrows(CoinNotFoundException.class, () -> currencyService.convertWithAwesomeApi(request));
    Assertions.assertEquals("Coin not found: DMA", exception.getMessage());
  }
}