package org.desafioestagio.wicket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.desafioestagio.wicket.dto.ClienteDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteRestClient {

    private static final String BASE_URL = "http://localhost:8080/api/clientes"; // URL da API
    private final RestTemplate restTemplate;
    private static final Logger LOGGER = Logger.getLogger(ClienteRestClient.class.getName());

    public ClienteRestClient() {
        // Configuração do ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // <-- ESSENCIAL!

        // Configuração do conversor JSON
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        // Instanciação do RestTemplate com o conversor configurado
        this.restTemplate = new RestTemplate();
        this.restTemplate.getMessageConverters().removeIf(c -> c instanceof MappingJackson2HttpMessageConverter);
        this.restTemplate.getMessageConverters().add(converter);
    }

    // Método para listar todos os clientes
    public List<ClienteDTO> listarTodos() {
        try {
            ResponseEntity<List<ClienteDTO>> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ClienteDTO>>() {}
            );
            List<ClienteDTO> lista = response.getBody();
            return (lista != null) ? lista : Collections.emptyList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar clientes", e);
            return Collections.emptyList();
        }
    }

    // Método para salvar um novo cliente
    public void salvar(ClienteDTO clienteDTO) {
        prepararEnderecos(clienteDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClienteDTO> request = new HttpEntity<>(clienteDTO, headers);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(BASE_URL, request, Void.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Cliente salvo com sucesso.");
            } else {
                System.out.println("Falha ao salvar cliente. Código de status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao salvar cliente", e);
        }
    }

    // Método auxiliar para vincular cliente aos endereços
    private void prepararEnderecos(ClienteDTO clienteDTO) {
        if (clienteDTO.getEnderecos() != null) {
            clienteDTO.getEnderecos().forEach(endereco -> {
                endereco.setClienteDTO(clienteDTO);
                endereco.setClienteId(clienteDTO.getId());
            });
        }
    }

    // Método para atualizar um cliente existente
    public void atualizar(Long id, ClienteDTO clienteDTO) {
        prepararEnderecos(clienteDTO);
        enviarRequisicao(clienteDTO, id);
    }

    // Método genérico para envio de requisições POST e PUT
    private void enviarRequisicao(ClienteDTO cliente, Long id) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ClienteDTO> entity = new HttpEntity<>(cliente, headers);
            String url = (id != null) ? BASE_URL + "/" + id : BASE_URL;

            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Cliente atualizado com sucesso.");
            } else {
                System.out.println("Falha ao atualizar cliente. Código de status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao enviar requisição PUT para o cliente", e);
        }
    }

    // Método para excluir um cliente
    public void excluir(Long id) {
        try {
            String url = BASE_URL + "/" + id;
            restTemplate.delete(url);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao excluir cliente com ID " + id, e);
        }
    }

    public List<ClienteDTO> buscarPorCampo(String campo, String valor) {
        try {
            String url = "http://localhost:8080/api/clientes/buscar?";

            // Condicional para adicionar parâmetros corretamente à URL
            if (!valor.isEmpty()) {
                url += URLEncoder.encode(campo, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(valor, StandardCharsets.UTF_8);
            } else {
                throw new IllegalArgumentException("Valor não pode ser vazio");
            }

            System.out.println("URL da requisição: " + url); // Verifique a URL gerada no log

            ClienteDTO[] response = restTemplate.getForObject(url, ClienteDTO[].class);
            return Arrays.asList(response);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ClienteDTO buscarPorId(Long clienteId) {
        try {
            String url = BASE_URL + "/" + clienteId;
            ResponseEntity<ClienteDTO> response = restTemplate.getForEntity(url, ClienteDTO.class);
            return response.getBody();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar cliente com ID " + clienteId, e);
            return null;
        }
    }
}
