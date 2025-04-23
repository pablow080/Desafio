import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { CommonModule, NgIf } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-cliente-filter',
  templateUrl: './cliente-filter.component.html',
  styleUrls: ['./cliente-filter.component.css'],
  imports: [
    CommonModule,
    NgIf,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ]
})
export class ClienteFilterComponent {
  @Output() filtroAplicado = new EventEmitter<any>();

  filtroForm: FormGroup;
  filtersExpanded = false;

  constructor(private fb: FormBuilder) {
    this.filtroForm = this.fb.group({
      nome: [''],
      razaoSocial: [''],
      cpfCnpj: [''],
      email: [''],
      rg: [''],
      inscricaoEstadual: ['']
    });
  }

  aplicarFiltro(): void {
    const filtros = this.filtroForm.value;
    this.filtroAplicado.emit(filtros);
  }

  limparFiltros(): void {
    this.filtroForm.reset();
    this.filtroAplicado.emit({});
  }

  toggleFilters(): void {
    this.filtersExpanded = !this.filtersExpanded;
  }
}
