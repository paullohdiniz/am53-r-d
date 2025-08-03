package br.com.am53.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PessoaAgeDTO {

    private Long id;
    private long age;
    private String unit;

}
