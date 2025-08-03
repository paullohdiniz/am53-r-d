package br.com.am53.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PessoaSalaryDTO {

    private Long id;
    private BigDecimal value;
    private String unit;
}
