package br.com.am53.rs.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) para a entidade Pessoa.
 * Usado para transferir dados entre o controller e o service,
 * desacoplando a camada de API do modelo de domínio.
 */
@Data
public class PessoaDTO {
    private Long id;
    private String nome;
    private LocalDate dataNascimento;
    private LocalDate dataAdmissao;
}