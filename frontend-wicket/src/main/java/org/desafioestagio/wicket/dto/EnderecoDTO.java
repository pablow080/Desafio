package org.desafioestagio.wicket.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
public class EnderecoDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonBackReference
    private ClienteDTO clienteDTO;

    private Long id;
    private String logradouro;
    private String numero;
    private String cep;
    private String bairro;
    private String telefone;
    private String cidade;
    private String estado;
    private boolean principal;
    private String complemento;
    private boolean enderecoPrincipal;

    // Método para setar o clienteId (caso precise)
    public void setClienteId(Long clienteId) {
        if (this.clienteDTO == null) {
            this.clienteDTO = new ClienteDTO();
        }
        this.clienteDTO.setId(clienteId);
    }
}
