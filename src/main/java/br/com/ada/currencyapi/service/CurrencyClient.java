package br.com.ada.currencyapi.service;

import br.com.ada.currencyapi.domain.CurrencyAPIResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "currency-api", url = "https://economia.awesomeapi.com.br/json")
public interface CurrencyClient {
  @GetMapping("/last/{coin}")
  Map<String, CurrencyAPIResponse> getCurrency(@PathVariable("coin") String code);
}
