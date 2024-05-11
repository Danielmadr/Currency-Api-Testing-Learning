package br.com.ada.currencyapi.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Currency implements Serializable {

    // id -> 1
    // LCS
    // Moeda do Lucas
    // {USD: 5, BRL: 10, EUR: 15}


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;

    @ElementCollection
    @CollectionTable(name = "exchanges",
            joinColumns = {@JoinColumn(name = "currency_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "currency_name")
    private Map<String, BigDecimal> exchanges;

}