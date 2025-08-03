package br.com.am53.rs.service;

import br.com.am53.rs.domain.Pessoa;
import br.com.am53.rs.dto.PessoaAgeDTO;
import br.com.am53.rs.dto.PessoaDTO;
import br.com.am53.rs.dto.PessoaSalaryDTO;
import br.com.am53.rs.enum_new.AgeOutputFormat;
import br.com.am53.rs.enum_new.SalaryOutputFormat;
import br.com.am53.rs.mapper.PessoaMapper;
import br.com.am53.rs.repository.PessoaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final PessoaMapper pessoaMapper;

    @Value("${salary.minimum-wage}")
    private BigDecimal minimumWage;

    public PessoaService(PessoaRepository pessoaRepository, PessoaMapper pessoaMapper) {
        this.pessoaRepository = pessoaRepository;
        this.pessoaMapper = pessoaMapper;
    }

    public List<PessoaDTO> findAll() {
        List<Pessoa> pessoas = pessoaRepository.findAll(Sort.by("nome"));
        return pessoaMapper.toDTOList(pessoas);
    }

    public Optional<PessoaDTO> findById(Long id) {
        return pessoaRepository.findById(id)
                .map(pessoaMapper::toDTO);
    }

    public PessoaDTO save(PessoaDTO pessoaDTO) {
        if (pessoaDTO.getId() != null && pessoaRepository.existsById(pessoaDTO.getId())) {
            throw new IllegalArgumentException("ID já cadastrado. Para atualizar um registro, utilize o endpoint PUT ou PATCH.");
        }

        Pessoa pessoa = pessoaMapper.toEntity(pessoaDTO);
        Pessoa pessoaSalva = pessoaRepository.save(pessoa);
        return pessoaMapper.toDTO(pessoaSalva);
    }

    public PessoaSalaryDTO calculateSalary(Long id, SalaryOutputFormat format) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada com id: " + id));

        if (pessoa.getDataAdmissao() == null) {
            throw new IllegalArgumentException("A pessoa com id " + id + " não possui data de admissão cadastrada.");
        }

        int anosDeServico = Period.between(pessoa.getDataAdmissao(), LocalDate.now()).getYears();

        BigDecimal salarioBase = new BigDecimal("1558.00");
        BigDecimal fatorAumento = new BigDecimal("1.18"); // 18%
        BigDecimal bonusFixo = new BigDecimal("500.00");

        BigDecimal salarioAtual = salarioBase;
        for (int i = 0; i < anosDeServico; i++) {
            salarioAtual = salarioAtual.multiply(fatorAumento).add(bonusFixo);
        }

        BigDecimal valorFinal;
        String unidade;

        if (format == SalaryOutputFormat.MIN) {
            valorFinal = salarioAtual.divide(minimumWage, 2, RoundingMode.CEILING);
            unidade = "salários mínimos";
        } else {
            valorFinal = salarioAtual.setScale(2, RoundingMode.HALF_UP);
            unidade = "R$";
        }

        return new PessoaSalaryDTO(id, valorFinal, unidade);
    }

    public PessoaDTO update(Long id, PessoaDTO pessoaAtualizadaDTO) {
        return pessoaRepository.findById(id)
                .map(pessoaExistente -> {
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

    public PessoaAgeDTO calculateAge(Long id, AgeOutputFormat format) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pessoa não encontrada com id: " + id));

        if (pessoa.getDataNascimento() == null) {
            throw new IllegalArgumentException("A pessoa com id " + id + " não possui data de nascimento cadastrada.");
        }

        LocalDate dataNascimento = pessoa.getDataNascimento();
        LocalDate hoje = LocalDate.now();
        long idadeCalculada;

        switch (format) {
            case DAYS:
                idadeCalculada = ChronoUnit.DAYS.between(dataNascimento, hoje);
                break;
            case MONTHS:
                idadeCalculada = ChronoUnit.MONTHS.between(dataNascimento, hoje);
                break;
            case YEARS:
            default:
                idadeCalculada = Period.between(dataNascimento, hoje).getYears();
                break;
        }

        return new PessoaAgeDTO(id, idadeCalculada, format.name().toLowerCase());
    }
}