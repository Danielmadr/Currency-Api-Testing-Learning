package br.com.ada.currencyapi.repository;

import br.com.ada.currencyapi.domain.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Map;

@DataJpaTest
class CurrencyRepositoryTest {

  @Autowired
  private CurrencyRepository currencyRepository;

  @BeforeEach
  void setUp(){
    currencyRepository.save(new Currency(1L, "USD", "Dollar", Map.of()));
  }

  @Test
  @DisplayName("Should find a currency by name successfully")
  void findByNameSuccess() {

    Currency response = currencyRepository.findByName("USD");
    Assertions.assertEquals(1l, response.getId());
    Assertions.assertEquals("USD", response.getName());
  }

  @Test
  @DisplayName("Should not find a currency by name")
  void findByNameFail() {
    Currency response = currencyRepository.findByName("BRL");
    Assertions.assertNull(response);
  }

}