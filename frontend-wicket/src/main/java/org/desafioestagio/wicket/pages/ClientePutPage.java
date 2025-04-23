package org.desafioestagio.wicket.pages;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.util.convert.IConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.desafioestagio.wicket.dto.ClienteDTO;
import org.desafioestagio.wicket.dto.EnderecoDTO;
import org.desafioestagio.wicket.service.ClienteRestClient;

import java.util.Arrays;

public class ClientePutPage extends WebPage {

    public ClientePutPage(PageParameters parameters) {
        Long clienteId = parameters.get("id").toOptionalLong();
        ClienteRestClient clienteRestClient = new ClienteRestClient();

        if (clienteId == null) {
            error("Cliente não encontrado.");
            setResponsePage(ClientePage.class);
            return;
        }

        ClienteDTO cliente = clienteRestClient.buscarPorId(clienteId);
        if (cliente == null) {
            error("Cliente não encontrado.");
            setResponsePage(ClientePage.class);
            return;
        }

        // Lista mutável de endereços
        List<EnderecoDTO> enderecos = new ArrayList<>(cliente.getEnderecos());
        cliente.setEnderecos(enderecos);

        add(new FeedbackPanel("feedback"));
        Form<Void> form = new Form<>("form");
        add(form);

        DropDownChoice<String> tipoPessoaChoice = new DropDownChoice<>("tipoPessoa",
                new PropertyModel<>(cliente, "tipoPessoa"),
                Arrays.asList("Física", "Jurídica"));
        tipoPessoaChoice.setRequired(true);
        form.add(tipoPessoaChoice);

        WebMarkupContainer fisicaContainer = new WebMarkupContainer("fisicaContainer");
        WebMarkupContainer juridicaContainer = new WebMarkupContainer("juridicaContainer");
        fisicaContainer.setOutputMarkupPlaceholderTag(true);
        juridicaContainer.setOutputMarkupPlaceholderTag(true);
        form.add(fisicaContainer, juridicaContainer);

        form.add(new TextField<>("cpfCnpj", new PropertyModel<>(cliente, "cpfCnpj")).setRequired(true));
        form.add(new TextField<>("email", new PropertyModel<>(cliente, "email")).setRequired(true));
        form.add(new CheckBox("ativo", new PropertyModel<>(cliente, "ativo")));

        fisicaContainer.add(new TextField<>("nome", new PropertyModel<>(cliente, "nome")).setRequired(true));
        fisicaContainer.add(new TextField<>("rg", new PropertyModel<>(cliente, "rg")).setRequired(true));
        fisicaContainer.add(new DateTextField("dataNascimento", new PropertyModel<>(cliente, "dataNascimento"), "yyyy-MM-dd").setRequired(true));

        juridicaContainer.add(new TextField<>("razaoSocial", new PropertyModel<>(cliente, "razaoSocial")).setRequired(true));
        juridicaContainer.add(new TextField<>("inscricaoEstadual", new PropertyModel<>(cliente, "inscricaoEstadual")).setRequired(true));
        juridicaContainer.add(new DateTextField("dataCriacao", new PropertyModel<>(cliente, "dataCriacao"), "yyyy-MM-dd").setRequired(true));

        tipoPessoaChoice.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                String tipo = tipoPessoaChoice.getModelObject();
                fisicaContainer.setVisible("Física".equals(tipo));
                juridicaContainer.setVisible("Jurídica".equals(tipo));
                target.add(fisicaContainer, juridicaContainer);
            }
        });
        fisicaContainer.setVisible("Física".equals(cliente.getTipoPessoa()));
        juridicaContainer.setVisible("Jurídica".equals(cliente.getTipoPessoa()));

        // Container para a lista de endereços
        WebMarkupContainer enderecoContainer = new WebMarkupContainer("enderecoContainer");
        enderecoContainer.setOutputMarkupId(true);
        form.add(enderecoContainer);

        ListView<EnderecoDTO> enderecoListView = new ListView<>("enderecos", cliente.getEnderecos()) {
            @Override
            protected void populateItem(ListItem<EnderecoDTO> item) {
                EnderecoDTO endereco = item.getModelObject();
                item.add(new TextField<>("logradouro", new PropertyModel<>(endereco, "logradouro")).setRequired(true));
                item.add(new TextField<>("numero", new PropertyModel<>(endereco, "numero")).setRequired(true));
                item.add(new TextField<>("cep", new PropertyModel<>(endereco, "cep")).setRequired(true));
                item.add(new TextField<>("bairro", new PropertyModel<>(endereco, "bairro")).setRequired(true));
                item.add(new TextField<>("telefone", new PropertyModel<>(endereco, "telefone")));
                item.add(new TextField<>("cidade", new PropertyModel<>(endereco, "cidade")).setRequired(true));
                item.add(new TextField<>("estado", new PropertyModel<>(endereco, "estado")).setRequired(true));
                item.add(new TextField<>("complemento", new PropertyModel<>(endereco, "complemento")));
                item.add(new CheckBox("principal", new PropertyModel<>(endereco, "principal")));
            }
        };
        enderecoListView.setReuseItems(true);
        enderecoContainer.add(enderecoListView);

        // Botão AJAX para adicionar novo endereço
        AjaxButton addEnderecoButton = new AjaxButton("addEndereco") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                cliente.getEnderecos().add(new EnderecoDTO());
                target.add(enderecoContainer);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(getPage().get("feedback"));
            }
        };
        addEnderecoButton.setDefaultFormProcessing(false);
        form.add(addEnderecoButton);

        form.add(new Button("salvar") {
            @Override
            public void onSubmit() {
                super.onSubmit();
                cliente.setTipoPessoa(tipoPessoaChoice.getModelObject());

                if ("Física".equals(cliente.getTipoPessoa())) {
                    cliente.setRazaoSocial(null);
                    cliente.setInscricaoEstadual(null);
                    cliente.setDataCriacao(null);
                } else {
                    cliente.setNome(null);
                    cliente.setRg(null);
                    cliente.setDataNascimento(null);
                }

                clienteRestClient.atualizar(cliente.getId(), cliente);
                setResponsePage(ClientePage.class);
            }
        });
    }
}
