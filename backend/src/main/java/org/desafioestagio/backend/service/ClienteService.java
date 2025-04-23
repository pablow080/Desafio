package org.desafioestagio.backend.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.desafioestagio.backend.exception.BusinessException;
import org.desafioestagio.backend.exception.ResourceNotFoundException;
import org.desafioestagio.backend.filtro.ClienteSpecification;
import org.desafioestagio.backend.model.Cliente;
import org.desafioestagio.backend.model.Endereco; // Importando o modelo Endereco
import org.desafioestagio.backend.repository.ClienteRepository;
import org.desafioestagio.backend.repository.EnderecoRepository; // Importando o repositório Endereco
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EnderecoRepository enderecoRepository; // Adicionando o repositório de Endereços

    public ClienteService(ClienteRepository clienteRepository, EnderecoRepository enderecoRepository) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        if (cliente.getEnderecos() == null || cliente.getEnderecos().isEmpty()) {
            throw new BusinessException("O cliente deve ter ao menos um endereço");
        }
        for (Endereco endereco : cliente.getEnderecos()) {
            endereco.setCliente(cliente);
        }

        // Salvar cliente primeiro
        Cliente clienteSalvo = clienteRepository.save(cliente);

        // Agora, associar o cliente_id aos endereços e salvar os endereços
        for (Endereco endereco : cliente.getEnderecos()) {
            endereco.setCliente(clienteSalvo); // Associar cliente ao endereço
            enderecoRepository.save(endereco); // Salvar endereço
        }

        return clienteSalvo; // Retornar o cliente salvo
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public List<Cliente> buscarClientes(String nome, String razaoSocial, String cpfCnpj, String email, String rg, String inscricaoEstadual) {
        Specification<Cliente> spec = Specification.where(null);  // Inicializa com uma Specification vazia

        // Verifica se cada parâmetro é não nulo e não vazio antes de adicionar a Specification correspondente
        if (nome != null && !nome.isEmpty()) {
            spec = spec.and(ClienteSpecification.nomeLike(nome));
        }
        if (razaoSocial != null && !razaoSocial.isEmpty()) {
            spec = spec.and(ClienteSpecification.razaoSocialLike(razaoSocial));
        }
        if (cpfCnpj != null && !cpfCnpj.isEmpty()) {
            spec = spec.and(ClienteSpecification.cpfCnpjLike(cpfCnpj));
        }
        if (email != null && !email.isEmpty()) {
            spec = spec.and(ClienteSpecification.emailLike(email));
        }
        if (rg != null && !rg.isEmpty()) {
            spec = spec.and(ClienteSpecification.rgLike(rg));
        }
        if (inscricaoEstadual != null && !inscricaoEstadual.isEmpty()) {
            spec = spec.and(ClienteSpecification.inscricaoEstadualLike(inscricaoEstadual));
        }

        // Executa a consulta com a Specification construída
        return clienteRepository.findAll(spec);
    }


    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    // Atualizar os campos do cliente
                    cliente.setTipoPessoa(clienteAtualizado.getTipoPessoa());
                    cliente.setCpfCnpj(clienteAtualizado.getCpfCnpj());
                    cliente.setNome(clienteAtualizado.getNome());
                    cliente.setRazaoSocial(clienteAtualizado.getRazaoSocial());
                    cliente.setRg(clienteAtualizado.getRg());
                    cliente.setInscricaoEstadual(clienteAtualizado.getInscricaoEstadual());
                    cliente.setDataNascimento(clienteAtualizado.getDataNascimento());
                    cliente.setDataCriacao(clienteAtualizado.getDataCriacao());
                    cliente.setEmail(clienteAtualizado.getEmail());
                    cliente.setAtivo(clienteAtualizado.isAtivo());

                    // Atualizar endereços, se necessário
                    if (clienteAtualizado.getEnderecos() != null) {
                        for (Endereco endereco : clienteAtualizado.getEnderecos()) {
                            if (endereco.getId() != null && enderecoRepository.existsById(endereco.getId())) {
                                // Atualizar endereço existente
                                endereco.setCliente(cliente);
                                enderecoRepository.save(endereco);
                            } else {
                                // Adicionar novo endereço
                                endereco.setCliente(cliente);
                                enderecoRepository.save(endereco);
                            }
                        }
                    }

                    return clienteRepository.save(cliente);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
    }

    @Transactional
    public void excluir(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
    }

    public Cliente buscarPorId(long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));
    }

    public byte[] exportarClientesParaExcel() {
        List<Cliente> clientes = clienteRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clientes");

            // Criar cabeçalho
            Row header = sheet.createRow(0);
            String[] colunas = {"ID", "Tipo Pessoa", "Nome/Razão Social", "CPF/CNPJ", "Email", "RG/Inscrição Estadual", "Ativo"};
            for (int i = 0; i < colunas.length; i++) {
                header.createCell(i).setCellValue(colunas[i]);
            }

            // Preencher dados
            int rowIdx = 1;
            for (Cliente cliente : clientes) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(cliente.getId());
                row.createCell(1).setCellValue(cliente.getTipoPessoa());
                row.createCell(2).setCellValue(cliente.getNome() != null && !cliente.getNome().isEmpty()
                        ? cliente.getNome()
                        : cliente.getRazaoSocial());
                row.createCell(3).setCellValue(cliente.getCpfCnpj());
                row.createCell(4).setCellValue(cliente.getEmail());
                row.createCell(5).setCellValue(cliente.getRg() != null && !cliente.getRg().isEmpty()
                        ? cliente.getRg()
                        : cliente.getInscricaoEstadual());
                row.createCell(6).setCellValue(cliente.isAtivo() ? "Sim" : "Não");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new BusinessException("Erro ao exportar relatório Excel: " + e.getMessage());
        }
    }
}
