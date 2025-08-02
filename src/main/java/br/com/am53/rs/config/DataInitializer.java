package br.com.am53.rs.config;

import br.com.am53.rs.domain.Pessoa;
import br.com.am53.rs.repository.PessoaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PessoaRepository pessoaRepository;

    public DataInitializer(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        Pessoa p1 = new Pessoa(null, "Paulo Diniz", LocalDate.of(1980, 10, 9), LocalDate.of(2025, 8, 2));
        Pessoa p2 = new Pessoa(null, "Kelly Diniz", LocalDate.of(1979, 7, 9), LocalDate.of(2025, 8, 2));
        Pessoa p3 = new Pessoa(null, "Karen Diniz", LocalDate.of(1994, 10, 18), LocalDate.of(2025, 8, 2));

        pessoaRepository.saveAll(Arrays.asList(p1, p2, p3));

    }
}