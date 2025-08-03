package br.com.am53.rs.controller;

import br.com.am53.rs.dto.PessoaAgeDTO;
import br.com.am53.rs.dto.PessoaDTO;
import br.com.am53.rs.dto.PessoaSalaryDTO;
import br.com.am53.rs.enum_new.AgeOutputFormat;
import br.com.am53.rs.enum_new.SalaryOutputFormat;
import br.com.am53.rs.service.PessoaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/persons")
public class PessoaController {

    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
    }

    @GetMapping("/{id}/age")
    public ResponseEntity<PessoaAgeDTO> getAge(
            @PathVariable Long id,
            @RequestParam(name = "output", defaultValue = "YEARS") AgeOutputFormat output) {

        PessoaAgeDTO ageDto = pessoaService.calculateAge(id, output);
        return ResponseEntity.ok(ageDto);
    }

    @PostMapping
    public ResponseEntity<PessoaDTO> create(@RequestBody PessoaDTO pessoa) {
        PessoaDTO novaPessoa = pessoaService.save(pessoa);
        return new ResponseEntity<>(novaPessoa, HttpStatus.CREATED);
    }

    @GetMapping
    public List<PessoaDTO> getAll() {
        return pessoaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaDTO> getById(@PathVariable Long id) {
        return pessoaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaDTO> update(@PathVariable Long id, @RequestBody PessoaDTO pessoa) {
        return ResponseEntity.ok(pessoaService.update(id, pessoa));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PessoaDTO> patch(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
        return ResponseEntity.ok(pessoaService.patch(id, campos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pessoaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/salary")
    public ResponseEntity<PessoaSalaryDTO> getSalary(
            @PathVariable Long id,
            @RequestParam(name = "output", defaultValue = "FULL") SalaryOutputFormat output) {

        PessoaSalaryDTO salaryDto = pessoaService.calculateSalary(id, output);
        return ResponseEntity.ok(salaryDto);
    }
}