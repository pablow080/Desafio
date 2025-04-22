package org.desafioestagio.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.RequestCycleSettings;
import org.desafioestagio.wicket.pages.ClientePage;

public class WicketApplication extends WebApplication {

    @Override
    public Class<? extends Page> getHomePage() {
        return ClientePage.class;
    }

    @Override
    public void init() {
        super.init();

        // Estratégia para evitar reenvio de formulários em F5
        getRequestCycleSettings().setRenderStrategy(RequestCycleSettings.RenderStrategy.REDIRECT_TO_RENDER);

        // Rota amigável
        mountPage("/clientes", ClientePage.class);
    }
}
