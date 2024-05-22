package br.com.ada.currencyapi.controller;

import br.com.ada.currencyapi.domain.CurrencyRequest;
import br.com.ada.currencyapi.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
public class CurrencyViewController {

  private final CurrencyService currencyService;

  @GetMapping("/new-currency")
  public String newCurrency(Model model) {
    model.addAttribute("currencyRequest", new CurrencyRequest());
    return "new-currency";
  }

  @PostMapping("/create")
  public String create(@ModelAttribute CurrencyRequest currencyRequest, RedirectAttributes redirectAttributes) {
    Long id = this.currencyService.create(currencyRequest);
    redirectAttributes.addFlashAttribute("successMessage", "Currency created successfully with id: " + id);
    return "redirect:/currencies";
  }

  @GetMapping("/currencies")
  public String getCurrencies(Model model) {
    model.addAttribute("currencies", this.currencyService.get());
    return "list-currencies";
  }
}
