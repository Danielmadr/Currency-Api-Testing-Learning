package br.com.ada.currencyapi.controller;


import br.com.ada.currencyapi.domain.ConvertCurrencyRequest;
import br.com.ada.currencyapi.domain.ConvertCurrencyResponse;
import br.com.ada.currencyapi.domain.CurrencyRequest;
import br.com.ada.currencyapi.domain.CurrencyResponse;
import br.com.ada.currencyapi.service.CurrencyService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class CurrencyControllerTest {

  @Mock
  private CurrencyService currencyService;

  @InjectMocks
  private CurrencyController currencyController;

  private MockMvc mockMvc;
  private final List<CurrencyResponse> coins = new ArrayList<>();

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(currencyController).build();

    coins.add(CurrencyResponse.builder().label("1 - DMA").build());
    coins.add(CurrencyResponse  .builder().label("2 - BTC").build());
  }

  @Test
  @DisplayName("Get Success")
  void getSuccess() throws Exception {
    Mockito.when(currencyService.get()).thenReturn(coins);

    MvcResult result = mockMvc.perform(get("/currency"))
            .andExpect(status().isOk())
            .andReturn();

    String content = result.getResponse().getContentAsString();
    List<CurrencyResponse> response = new ObjectMapper().readValue(content, new TypeReference<>() {

    });

    Assertions.assertEquals(2, response.size());
    Assertions.assertEquals("1 - DMA", response.get(0).getLabel());
    Assertions.assertEquals("2 - BTC", response.get(1).getLabel());
  }

  @Test
  @DisplayName("Convert Success")
  void convertSuccess() throws Exception {
    ConvertCurrencyResponse response = new ConvertCurrencyResponse(BigDecimal.TEN);

    Mockito.when(currencyService.convert(Mockito.any(ConvertCurrencyRequest.class))).thenReturn(response);

    MvcResult result = mockMvc.perform(get("/currency/convert", Mockito.any(ConvertCurrencyRequest.class)))
            .andExpect(status().isOk())
            .andReturn();

    ConvertCurrencyResponse resultResponse = new ObjectMapper()
            .readValue(result.getResponse().getContentAsString(), ConvertCurrencyResponse.class);

    Assertions.assertEquals(BigDecimal.TEN, resultResponse.getAmount());
}

  @Test
  @DisplayName("Create Success")
  void createSuccess() throws Exception {
    Mockito.when(currencyService.create(Mockito.any(CurrencyRequest.class))).thenReturn(1L);

    CurrencyRequest request = CurrencyRequest.builder().build();
    String jsonRequest = new ObjectMapper().writeValueAsString(request);

    MvcResult result = mockMvc.perform(post("/currency")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest))
            .andReturn();

    Long response = new ObjectMapper()
            .readValue(result.getResponse().getContentAsString(), Long.class);

    Assertions.assertEquals(1L, response);
  }

  @Test
  @DisplayName("Delete Success")
  void deleteSuccess() throws Exception {
    Mockito.doNothing().when(currencyService).delete(Mockito.anyLong());

   mockMvc.perform(delete("/currency/{id}", Mockito.anyLong())).andExpect(status().isOk()).andReturn();


    Mockito.verify(currencyService, Mockito.times(1)).delete(Mockito.anyLong());
  }
}