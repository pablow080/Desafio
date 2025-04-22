package org.desafioestagio.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@Entity
public class Cliente {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipoPessoa;

    @Column(nullable = false, unique = true)
    private String cpfCnpj;

    // Pessoa Física
    private String nome;
    private String rg;
    private LocalDate dataNascimento;

    // Pessoa Jurídica
    private String razaoSocial;
    private String inscricaoEstadual;
    private LocalDate dataCriacao;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean ativo;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Endereco> enderecos = new ArrayList<>();
}
