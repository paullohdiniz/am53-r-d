package br.com.am53.rs.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PessoaDTO {
    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private LocalDate dataAdmissao;
}