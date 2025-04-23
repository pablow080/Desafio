import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ClienteListaComponent } from './components/cliente-lista/cliente-lista.component';
import { ClienteFormComponent } from './components/cliente-form/cliente-form.component';
import { ClienteDetalheComponent } from './components/cliente-detalhe/cliente-detalhe.component';

const routes: Routes = [
  { path: '', component: ClienteListaComponent },
  { path: 'novo', component: ClienteFormComponent },
  { path: 'editar/:id', component: ClienteFormComponent },
  { path: 'detalhe/:id', component: ClienteDetalheComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ClientesRoutingModule { }