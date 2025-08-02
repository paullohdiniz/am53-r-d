package br.com.am53.rs.mapper;

import br.com.am53.rs.domain.Pessoa;
import br.com.am53.rs.dto.PessoaDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PessoaMapper {

    /**
     * Converte uma entidade Pessoa para um PessoaDTO.
     */
    public PessoaDTO toDTO(Pessoa pessoa) {
        if (pessoa == null) {
            return null;
        }
        PessoaDTO dto = new PessoaDTO();
        dto.setId(pessoa.getId());
        dto.setNome(pessoa.getNome());
        dto.setDataNascimento(pessoa.getDataNascimento());
        dto.setDataAdmissao(pessoa.getDataAdmissao());
        return dto;
    }

    /**
     * Converte um PessoaDTO para uma entidade Pessoa.
     */
    public Pessoa toEntity(PessoaDTO dto) {
        if (dto == null) {
            return null;
        }
        Pessoa pessoa = new Pessoa();
        pessoa.setId(dto.getId());
        pessoa.setNome(dto.getNome());
        pessoa.setDataNascimento(dto.getDataNascimento());
        pessoa.setDataAdmissao(dto.getDataAdmissao());
        return pessoa;
    }

    /**
     * Converte uma lista de entidades Pessoa para uma lista de PessoaDTO.
     */
    public List<PessoaDTO> toDTOList(List<Pessoa> pessoas) {
        return pessoas.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}