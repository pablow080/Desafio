package org.desafioestagio.wicket.pages;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.desafioestagio.wicket.dto.ClienteDTO;
import org.desafioestagio.wicket.dto.EnderecoDTO;
import org.desafioestagio.wicket.service.ClienteRestClient;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ClientePage extends WebPage {

    private final transient ClienteRestClient clienteRestClient = new ClienteRestClient(); // Corrigido
    private final List<ClienteDTO> clientes;
    private final DataView<ClienteDTO> dataView;
    private final WebMarkupContainer tableContainer; // Novo contêiner para o DataView

    public ClientePage() {
        setVersioned(true);

        clientes = clienteRestClient.listarTodos();
        ListDataProvider<ClienteDTO> provider = new ListDataProvider<ClienteDTO>() {
            @Override
            protected List<ClienteDTO> getData() {
                return clientes;
            }
        };

        // Container para a tabela
        tableContainer = new WebMarkupContainer("tableContainer");
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);

        // DataView para exibir os clientes
        dataView = new DataView<>("clienteRow", provider) {
            @Override
            protected void populateItem(Item<ClienteDTO> item) {
                ClienteDTO cliente = item.getModelObject();

                item.add(new Label("id", Model.of(cliente.getId())));
                item.add(new Label("tipo", Model.of(obterTipoPessoa(cliente))));
                item.add(new Label("cpfCnpj", Model.of(cliente.getCpfCnpj())));
                item.add(new Label("nomeRazaoSocial", Model.of(obterNomeRazaoSocial(cliente))));
                item.add(new Label("rgInscricaoEstadual", Model.of(obterRgInscricao(cliente))));
                item.add(new Label("dataNascimentoCriacao", Model.of(obterDataNascimentoCriacao(cliente))));
                item.add(new Label("email", Model.of(cliente.getEmail())));

                EnderecoDTO enderecoPrincipal = cliente.getEnderecos().stream()
                        .filter(EnderecoDTO::isPrincipal)
                        .findFirst()
                        .orElse(null);

                item.add(new Label("telefone", Model.of(enderecoPrincipal != null ? enderecoPrincipal.getTelefone() : "-")));
                item.add(new Label("cep", Model.of(enderecoPrincipal != null ? enderecoPrincipal.getCep() : "-")));

                item.add(new Label("ativo", Model.of(cliente.isAtivo() ? "Ativo" : "Inativo")));

                // Link para editar o cliente
                PageParameters params = new PageParameters();
                params.add("id", cliente.getId());
                item.add(new BookmarkablePageLink<>("editLink", ClientePutPage.class, params));

                // Link para excluir o cliente
                item.add(new AjaxLink<Void>("deleteLink") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        clienteRestClient.excluir(cliente.getId());
                        clientes.remove(cliente);
                        dataView.modelChanged();
                        target.add(tableContainer); // Atualiza o contêiner ao invés do DataView direto
                    }
                });
            }

            private String obterTipoPessoa(ClienteDTO cliente) {
                return "Física".equalsIgnoreCase(cliente.getTipoPessoa()) ? "Física" : "Jurídica";
            }

            private String obterNomeRazaoSocial(ClienteDTO cliente) {
                return "Física".equalsIgnoreCase(cliente.getTipoPessoa()) ?
                        Optional.ofNullable(cliente.getNome()).orElse("Nome não disponível") :
                        Optional.ofNullable(cliente.getRazaoSocial()).orElse("Razão social não disponível");
            }

            private String obterRgInscricao(ClienteDTO cliente) {
                return "Física".equalsIgnoreCase(cliente.getTipoPessoa()) ?
                        Optional.ofNullable(cliente.getRg()).orElse("RG não disponível") :
                        Optional.ofNullable(cliente.getInscricaoEstadual()).orElse("Inscrição Estadual não disponível");
            }

            private String obterDataNascimentoCriacao(ClienteDTO cliente) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                return "Física".equalsIgnoreCase(cliente.getTipoPessoa()) ?
                        Optional.ofNullable(cliente.getDataNascimento()).map(formatter::format).orElse("Data de Nascimento não disponível") :
                        Optional.ofNullable(cliente.getDataCriacao()).map(formatter::format).orElse("Data de Criação não disponível");
            }
        };

        dataView.setOutputMarkupId(true);
        tableContainer.add(dataView); // DataView dentro do contêiner

        // Formulário de busca
        Form<Void> searchForm = new Form<>("searchForm");

        // Campo de filtro (DropDownChoice)
        List<String> campos = Arrays.asList("cpfCnpj", "rg", "inscricaoEstadual");
        DropDownChoice<String> campoFiltro = new DropDownChoice<>("campoFiltro", Model.of(""), campos);
        campoFiltro.setOutputMarkupId(true);  // Permite atualizar via AJAX
        searchForm.add(campoFiltro);

        // Campo de valor (TextField)
        TextField<String> valorFiltro = new TextField<>("valorFiltro", Model.of(""));
        valorFiltro.setOutputMarkupId(true); // Permite atualizar via AJAX
        searchForm.add(valorFiltro);

        // Botão de busca
        AjaxButton searchButton = new AjaxButton("searchButton", searchForm) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                String campo = campoFiltro.getModelObject();
                String valor = valorFiltro.getModelObject();

                // Verifica se o valor não está vazio
                if (valor != null && !valor.isEmpty()) {
                    List<ClienteDTO> filtrados = clienteRestClient.buscarPorCampo(campo, valor);

                    // Atualiza a lista de clientes
                    clientes.clear();
                    clientes.addAll(filtrados);

                    // Atualiza a visualização
                    target.add(tableContainer);  // tableContainer é o container que renderiza a tabela
                }
            }
        };
        searchForm.add(searchButton);

        // Adiciona o formulário à página
        add(searchForm);

        // Link para criação de um novo cliente
        add(new BookmarkablePageLink<>("createLink", ClientePostPage.class));
    }
}
