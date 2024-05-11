package br.com.ada.currencyapi.repository;

import br.com.ada.currencyapi.domain.Currency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashMap;
import static org.mockito.ArgumentMatchers.anyString;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CurrencyRepositoryTest {

  @Mock
  CurrencyRepository currencyRepository;

  @Test
  @DisplayName("Should find a currency by name successfully")
  void findByNameSuccess() {
    Currency test = new Currency();
    test.setName("DMA");
    test.setDescription("Domain Coin");
    test.setExchanges(new HashMap<>());

    Mockito.when(currencyRepository.findByName(anyString())).thenReturn(test);

    Currency currency = this.currencyRepository.findByName("DMA");

    assertEquals(currency.getName(), "DMA");
    assertEquals(currency.getDescription(), "Domain Coin");
    assertEquals(currency.getExchanges(), new HashMap<>());
  }

  @Test
  @DisplayName("Should not find a currency by name")
  void findByNameFail() {
    Mockito.when(currencyRepository.findByName("DMA")).thenReturn(null);
    Currency currency = this.currencyRepository.findByName("DMA");
    assertNull(currency);
  }

}