package org.desafioestagio.backend.controller;

import org.desafioestagio.backend.exception.ResourceNotFoundException;
import org.desafioestagio.backend.model.Cliente;
import org.desafioestagio.backend.repository.ClienteRepository;
import org.desafioestagio.backend.service.ClienteService;
import org.desafioestagio.backend.service.RelatorioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final RelatorioService relatorioService;

    public ClienteController(ClienteService clienteService, RelatorioService relatorioService) {
        this.clienteService = clienteService;
        this.relatorioService = relatorioService;
    }

    @PostMapping
    public ResponseEntity<Cliente> criar(@RequestBody Cliente cliente) {
        // Certifique-se de que os endereços são enviados corretamente
        if (cliente.getEnderecos() != null && !cliente.getEnderecos().isEmpty()) {
            Cliente novoCliente = clienteService.salvar(cliente);
            return ResponseEntity.created(URI.create("/api/clientes/" + novoCliente.getId())).body(novoCliente);
        } else {
            return ResponseEntity.badRequest().build();  // Se não houver endereços, retornamos erro de requisição
        }
    }

    @GetMapping
    public List<Cliente> listar() {
        return clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        if (cliente != null) {
            return ResponseEntity.ok(cliente);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    public List<Cliente> buscarClientes(@RequestParam(required = false) String nome,
                                        @RequestParam(required = false) String razaoSocial,
                                        @RequestParam(required = false) String cpfCnpj,
                                        @RequestParam(required = false) String email,
                                        @RequestParam(required = false) String rg,
                                        @RequestParam(required = false) String inscricaoEstadual) {
        return clienteService.buscarClientes(nome, razaoSocial, cpfCnpj, email, rg, inscricaoEstadual);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        try {
            // Atualiza cliente e endereços
            return ResponseEntity.ok(clienteService.atualizar(id, cliente));
        } catch (ResourceNotFoundException e) {
            System.out.println("Cliente não encontrado com id: " + id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            clienteService.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel() {
        byte[] relatorio = clienteService.exportarClientesParaExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "clientes.xlsx");
        return new ResponseEntity<>(relatorio, headers, HttpStatus.OK);
    }
}
