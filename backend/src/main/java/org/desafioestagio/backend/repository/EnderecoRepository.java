package org.desafioestagio.backend.repository;

import org.desafioestagio.backend.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    // Usando o identificador do cliente
    List<Endereco> findByCliente_Id(Long clienteId);
}
