package org.desafioestagio.wicket.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClienteDTO implements Serializable {
    private Long id;
    private String cpfCnpj;
    private String email;
    private boolean ativo;
    private String tipoPessoa;

    // Pessoa Física
    private String nome;
    private String rg;
    private LocalDate dataNascimento;

    // Pessoa Jurídica
    private String razaoSocial;
    private String inscricaoEstadual;
    private LocalDate dataCriacao;

    @JsonManagedReference
    private List<EnderecoDTO> enderecos = new ArrayList<>();

    public EnderecoDTO getEndereco() {
        return enderecos != null && !enderecos.isEmpty() ? enderecos.get(0) : null;
    }
}
