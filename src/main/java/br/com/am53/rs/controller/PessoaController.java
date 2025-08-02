package br.com.am53.rs.controller;

import br.com.am53.rs.domain.Pessoa;
import br.com.am53.rs.dto.PessoaDTO;
import br.com.am53.rs.service.PessoaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/persons")
//@Api(value = "API Pessoas", tags = {"pessoas"})
public class PessoaController {

    private final PessoaService pessoaService;

    public PessoaController(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
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
        try {
            return ResponseEntity.ok(pessoaService.update(id, pessoa));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PessoaDTO> patch(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
        try {
            return ResponseEntity.ok(pessoaService.patch(id, campos));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            pessoaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
