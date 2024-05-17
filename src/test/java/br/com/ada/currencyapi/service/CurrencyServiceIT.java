package br.com.ada.currencyapi.service;

import br.com.ada.currencyapi.domain.*;
import br.com.ada.currencyapi.exception.CoinNotFoundException;
import br.com.ada.currencyapi.exception.CurrencyException;
import br.com.ada.currencyapi.repository.CurrencyRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
@ActiveProfiles("test")
class CurrencyServiceIT {

  @Autowired
  private CurrencyRepository currencyRepository;

  @Autowired
  private CurrencyService currencyService;

  @BeforeEach
  public void setUp() {
    List<Currency> currencies = new ArrayList<>();
    currencies.add(Currency.builder()
            .name("BTC")
            .description("Bitcoin")
            .exchanges(Map.of("USD", BigDecimal.valueOf(0.1)))
            .build());
    currencies.add(Currency.builder()
            .name("BRL")
            .description("Real")
            .exchanges(new HashMap<>())
            .build());
    currencies.add(Currency.builder()
            .name("USD")
            .description("Dollar")
            .exchanges(new HashMap<>())
            .build());
    currencyRepository.saveAll(currencies);
  }

  @AfterEach
  public void tearDown() {
    currencyRepository.deleteAll();
  }

  @Test
  @DisplayName("Get all currencies")
  void getSuccess() {
    List<CurrencyResponse> currencies = currencyService.get();

    Assertions.assertEquals(3, currencies.size());
    Assertions.assertEquals("1 - BTC", currencies.get(0).getLabel());
    Assertions.assertEquals("2 - BRL", currencies.get(1).getLabel());
  }

  @Test
  @DisplayName("Create currency Success")
  void createSuccess() {
    CurrencyRequest request = CurrencyRequest.builder().name("DAM").description("Domain Currency").exchanges(new HashMap<>()).build();

    Long id = currencyService.create(request);

    List<Currency> currencies = currencyRepository.findAll();

    Assertions.assertEquals(4, currencies.size());
    Assertions.assertEquals(id, currencies.get(3).getId());
    Assertions.assertEquals("DAM", currencies.get(3).getName());
    Assertions.assertEquals("Domain Currency", currencies.get(3).getDescription());

  }

  @Test
  @DisplayName("Create currency that Already Exists")
  void createAlreadyExists() {
    CurrencyRequest request = CurrencyRequest.builder().name("BTC").description("Bitcoin").exchanges(new HashMap<>()).build();

    CurrencyException exception = Assertions.assertThrows(CurrencyException.class, () -> currencyService.create(request));

    Assertions.assertEquals("Coin already exists", exception.getMessage());

    List<Currency> currencies = currencyRepository.findAll();
    Assertions.assertEquals(3, currencies.size());
    Assertions.assertEquals("BTC", currencies.get(0).getName());
    Assertions.assertEquals("Bitcoin", currencies.get(0).getDescription());
    Assertions.assertEquals("BRL", currencies.get(1).getName());
    Assertions.assertEquals("Real", currencies.get(1).getDescription());
  }

  @Test
  @DisplayName("Delete currency Success")
  void deleteSuccess() {
    Long idToDelete = 1L;

    currencyService.delete(idToDelete);

    List<Currency> currencies = currencyRepository.findAll();
    Assertions.assertEquals(2, currencies.size());
    Assertions.assertEquals("BRL", currencies.get(0).getName());
    Assertions.assertEquals("Real", currencies.get(0).getDescription());
  }

  @Test
  @DisplayName("Delete a Currency that does not exist")
  void deleteNotExists() {
    Long idToDelete = 4L;

    CoinNotFoundException exception = Assertions.assertThrows(CoinNotFoundException.class,
            () -> currencyService.delete(idToDelete));

    Assertions.assertEquals("Coin not found: 4", exception.getMessage());

    List<Currency> currencies = currencyRepository.findAll();
    Assertions.assertEquals(3, currencies.size());
    Assertions.assertEquals("BTC", currencies.get(0).getName());
    Assertions.assertEquals("Bitcoin", currencies.get(0).getDescription());
    Assertions.assertEquals("BRL", currencies.get(1).getName());
    Assertions.assertEquals("Real", currencies.get(1).getDescription());
  }

  @Test
  @DisplayName("Convert currency Success")
  void convertSuccess() {
    ConvertCurrencyRequest request = ConvertCurrencyRequest.builder()
            .to("USD")
            .from("BTC")
            .amount(BigDecimal.TEN).build();

    ConvertCurrencyResponse response = currencyService.convert(request);

    Assertions.assertEquals(BigDecimal.valueOf(1.0), response.getAmount());
  }

  @Test
  @DisplayName("Convert currency That Does Not Exists")
  void convertNotExists() {
    ConvertCurrencyRequest request = ConvertCurrencyRequest.builder()
            .to("ETM")
            .from("DMA")//not exist
            .amount(BigDecimal.TEN).build();

    CoinNotFoundException exception = Assertions.assertThrows(CoinNotFoundException.class,
            () -> currencyService.convert(request));

    Assertions.assertEquals("Coin not found: DMA", exception.getMessage());
  }

  @Test
  @DisplayName("Convert From Exist currency to Not Exist currency")
  void convertFromExistToNotExist() {
    ConvertCurrencyRequest request = ConvertCurrencyRequest.builder()
            .to("DMA") //not exist
            .from("USD")
            .amount(BigDecimal.TEN).build();

    CoinNotFoundException exception = Assertions.assertThrows(CoinNotFoundException.class,
            () -> currencyService.convert(request));

    Assertions.assertEquals("Exchange DMA not found for USD", exception.getMessage());
  }

  @Test
  @DisplayName("Convert currency by API Success")
  void convertWithAPISuccess() {
    ConvertCurrencyRequest request = ConvertCurrencyRequest.builder()
            .to("BRL")
            .from("USD")
            .amount(BigDecimal.TEN).build();

    ConvertCurrencyResponse response = currencyService.convertWithAwesomeApi(request);

    Assertions.assertNotNull(response);
  }
  @Test
  @DisplayName("Convert currency That Does Not Exists")
  void convertNotExistsWithAPI() {
    ConvertCurrencyRequest request = ConvertCurrencyRequest.builder()
            .to("USD")
            .from("DMA")//not exist
            .amount(BigDecimal.TEN).build();

    CoinNotFoundException exception = Assertions.assertThrows(CoinNotFoundException.class,
            () -> currencyService.convertWithAwesomeApi(request));

    Assertions.assertEquals("Coin not found: DMA", exception.getMessage());
  }

  @Test
  @DisplayName("Convert From Exist currency to Not Exist currency")
  void convertFromExistToNotExistWithAPI() {
    ConvertCurrencyRequest request = ConvertCurrencyRequest.builder()
            .to("DMA") //not exist
            .from("USD")
            .amount(BigDecimal.TEN).build();

    CoinNotFoundException exception = Assertions.assertThrows(CoinNotFoundException.class,
            () -> currencyService.convertWithAwesomeApi(request));

    Assertions.assertEquals("Exchange DMA not found for USD", exception.getMessage());
  }

}