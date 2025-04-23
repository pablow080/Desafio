import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { ClientesRoutingModule } from './clientes-routing.module';
import { ClienteListaComponent } from './components/cliente-lista/cliente-lista.component';
import { ClienteFormComponent } from './components/cliente-form/cliente-form.component';
import { ClienteDetalheComponent } from './components/cliente-detalhe/cliente-detalhe.component';
import { EnderecoFormComponent } from './components/endereco-form/endereco-form.component';
import { EnderecoListaComponent } from './components/endereco-lista/endereco-lista.component';
import { ClienteFilterComponent } from './components/cliente-filter/cliente-filter.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    SharedModule,
    ClientesRoutingModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    ClienteListaComponent,
    ClienteFormComponent,
    ClienteDetalheComponent,
    EnderecoFormComponent,
    EnderecoListaComponent,
    ClienteFilterComponent
  ]
})
export class ClientesModule { }
