package org.desafioestagio.backend.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.desafioestagio.backend.model.Cliente;
import org.desafioestagio.backend.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

@Service
public class RelatorioService {

    private final ClienteRepository clienteRepository;

    public RelatorioService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public byte[] gerarRelatorioClientesPDF() {
        try {
            // Recupera a lista de clientes do banco de dados
            List<Cliente> clientes = clienteRepository.findAll();

            // Carrega o arquivo .jrxml a partir do diretório correto
            InputStream inputStream = getClass().getResourceAsStream("/reports/relatorio_clientes.jrxml");

            // Verifica se o arquivo foi encontrado
            if (inputStream == null) {
                throw new RuntimeException("Arquivo .jrxml não encontrado no classpath.");
            }

            // Compila o arquivo .jrxml em um JasperReport
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

            // Cria a fonte de dados com a lista de clientes
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(clientes);

            // Parâmetros do relatório (se necessário)
            HashMap<String, Object> params = new HashMap<>();

            // Preenche o relatório com os dados e os parâmetros
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);

            // Exporta o relatório para PDF (em memória)
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (Exception e) {
            // Caso ocorra algum erro, lançar uma exceção com a mensagem do erro
            throw new RuntimeException("Erro ao gerar relatório PDF: " + e.getMessage(), e);
        }
    }
}
