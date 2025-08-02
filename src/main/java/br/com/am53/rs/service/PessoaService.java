package br.com.am53.rs.service;

import br.com.am53.rs.domain.Pessoa;
import br.com.am53.rs.dto.PessoaDTO;
import br.com.am53.rs.mapper.PessoaMapper;
import br.com.am53.rs.repository.PessoaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final PessoaMapper pessoaMapper;

    public PessoaService(PessoaRepository pessoaRepository, PessoaMapper pessoaMapper) {
        this.pessoaRepository = pessoaRepository;
        this.pessoaMapper = pessoaMapper;
    }

    public List<PessoaDTO> findAll() {
        // A ordenação pode ser feita de forma mais flexível com Sort
        List<Pessoa> pessoas = pessoaRepository.findAll(Sort.by("nome"));
        return pessoaMapper.toDTOList(pessoas);
    }

    public Optional<PessoaDTO> findById(Long id) {
        return pessoaRepository.findById(id)
                .map(pessoaMapper::toDTO);
    }

    /**
     * Salva uma nova pessoa.
     * Impede a criação se um ID for fornecido e já existir no banco de dados.
     */
    public PessoaDTO save(PessoaDTO pessoaDTO) {
        if (pessoaDTO.getId() != null && pessoaRepository.existsById(pessoaDTO.getId())) {
            throw new IllegalArgumentException("ID já cadastrado. Para atualizar um registro, utilize o endpoint PUT ou PATCH.");
        }

        Pessoa pessoa = pessoaMapper.toEntity(pessoaDTO);
        Pessoa pessoaSalva = pessoaRepository.save(pessoa);
        return pessoaMapper.toDTO(pessoaSalva);
    }

    public PessoaDTO update(Long id, PessoaDTO pessoaAtualizadaDTO) {
        return pessoaRepository.findById(id)
                .map(pessoaExistente -> {
                    // Atualiza a entidade existente com os dados do DTO
                    pessoaExistente.setNome(pessoaAtualizadaDTO.getNome());
                    pessoaExistente.setDataNascimento(pessoaAtualizadaDTO.getDataNascimento());
                    pessoaExistente.setDataAdmissao(pessoaAtualizadaDTO.getDataAdmissao());

                    Pessoa pessoaAtualizada = pessoaRepository.save(pessoaExistente);
                    return pessoaMapper.toDTO(pessoaAtualizada);
                }).orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada com id: " + id));
    }

    public PessoaDTO patch(Long id, Map<String, Object> campos) {
        Pessoa pessoaExistente = pessoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada com id: " + id));

        campos.forEach((chave, valor) -> {
            Field campo = ReflectionUtils.findField(Pessoa.class, chave);
            if (campo != null) {
                campo.setAccessible(true);
                // Converte o valor se for uma data
                if (campo.getType().equals(LocalDate.class) && valor instanceof String) {
                    ReflectionUtils.setField(campo, pessoaExistente, LocalDate.parse((String) valor));
                } else {
                    ReflectionUtils.setField(campo, pessoaExistente, valor);
                }
            }
        });
        Pessoa pessoaAtualizada = pessoaRepository.save(pessoaExistente);
        return pessoaMapper.toDTO(pessoaAtualizada);
    }

    public void deleteById(Long id) {
        if (!pessoaRepository.existsById(id)) {
            throw new EntityNotFoundException("Pessoa não encontrada com id: " + id);
        }
        pessoaRepository.deleteById(id);
    }
}