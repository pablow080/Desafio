package org.desafioestagio.backend.service;

import org.desafioestagio.backend.exception.ResourceNotFoundException;
import org.desafioestagio.backend.model.Cliente;
import org.desafioestagio.backend.model.Endereco;
import org.desafioestagio.backend.repository.ClienteRepository;
import org.desafioestagio.backend.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EnderecoService {

    private final EnderecoRepository enderecoRepository;

    @Autowired
    public EnderecoService(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional
    public Endereco salvar(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    public List<Endereco> listarPorCliente(Long clienteId) {
        return enderecoRepository.findByCliente_Id(clienteId);
    }

    public Optional<Endereco> buscarPorId(Long id) {
        return enderecoRepository.findById(id);
    }

    public Endereco atualizarEndereco(Long id, Endereco enderecoAtualizado) {
        // Buscar o endereço existente
        Endereco enderecoExistente = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        // Atualizar os campos do endereço
        enderecoExistente.setLogradouro(enderecoAtualizado.getLogradouro());
        enderecoExistente.setNumero(enderecoAtualizado.getNumero());
        enderecoExistente.setCep(enderecoAtualizado.getCep());
        enderecoExistente.setBairro(enderecoAtualizado.getBairro());
        enderecoExistente.setTelefone(enderecoAtualizado.getTelefone());
        enderecoExistente.setCidade(enderecoAtualizado.getCidade());
        enderecoExistente.setEstado(enderecoAtualizado.getEstado());
        enderecoExistente.setComplemento(enderecoAtualizado.getComplemento());
        enderecoExistente.setPrincipal(enderecoAtualizado.isPrincipal());

        // Verifica e associa o cliente
        Cliente cliente = clienteRepository.findById(enderecoAtualizado.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        enderecoExistente.setCliente(cliente);

        // Salvar e retornar o endereço atualizado
        return enderecoRepository.save(enderecoExistente);
    }

    @Transactional
    public void excluir(Long id) {
        enderecoRepository.deleteById(id);
    }

    @Transactional
    public Endereco atualizar(Long id, Endereco endereco) {
        if (enderecoRepository.existsById(id)) {
            endereco.setId(id);
            return enderecoRepository.save(endereco);
        }
        throw new ResourceNotFoundException("Endereco não encontrado.");
    }
}
