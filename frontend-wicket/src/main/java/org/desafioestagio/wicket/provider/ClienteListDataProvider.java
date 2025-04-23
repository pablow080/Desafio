package org.desafioestagio.wicket.provider;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Iterator;
import java.util.List;

public class ClienteListDataProvider extends SortableDataProvider<org.desafioestagio.wicket.dto.ClienteDTO, String> {

    private List<org.desafioestagio.wicket.dto.ClienteDTO> lista;

    public ClienteListDataProvider(List<org.desafioestagio.wicket.dto.ClienteDTO> lista) {
        this.lista = lista;
    }

    public void setList(List<org.desafioestagio.wicket.dto.ClienteDTO> novaLista) {
        this.lista = novaLista;
    }

    @Override
    public Iterator<? extends org.desafioestagio.wicket.dto.ClienteDTO> iterator(long first, long count) {
        return lista.subList((int) first, Math.min((int)(first + count), lista.size())).iterator();
    }

    @Override
    public long size() {
        return lista.size();
    }

    @Override
    public IModel<org.desafioestagio.wicket.dto.ClienteDTO> model(org.desafioestagio.wicket.dto.ClienteDTO object) {
        return Model.of(object);
    }
}
