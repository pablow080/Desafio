import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { Endereco } from '../../../core/models/endereco.model';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-endereco-form',
  templateUrl: './endereco-form.component.html',
  imports: [
    MatFormFieldModule,
    ReactiveFormsModule,
    MatInputModule,
    MatSelectModule,
    MatOptionModule,
    MatCheckboxModule,
    MatButtonModule,
    NgIf
  ],
  styleUrls: ['./endereco-form.component.css']
})
export class EnderecoFormComponent implements OnInit {
  @Input() endereco: Endereco | null = null;
  @Input() clienteId: number | null = null;
  @Output() enderecoSalvo = new EventEmitter<Endereco>();
  @Output() cancelar = new EventEmitter<void>();

  enderecoForm!: FormGroup;

  estados = [
    'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG',
    'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'
  ];

  constructor(private fb: FormBuilder) { }

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.enderecoForm = this.fb.group({
      logradouro: [this.endereco?.logradouro || '', Validators.required],
      numero: [this.endereco?.numero || '', Validators.required],
      cep: [this.endereco?.cep || '', Validators.required],
      bairro: [this.endereco?.bairro || '', Validators.required],
      telefone: [this.endereco?.telefone || ''],
      cidade: [this.endereco?.cidade || '', Validators.required],
      estado: [this.endereco?.estado || '', Validators.required],
      principal: [this.endereco?.principal || false],
      complemento: [this.endereco?.complemento || '']
    });
  }

  onSubmit(): void {
    if (this.enderecoForm.invalid) {
      return;
    }

    const endereco: Endereco = {
      ...this.enderecoForm.value,
      id: this.endereco?.id,
      clienteId: this.clienteId
    };

    this.enderecoSalvo.emit(endereco);
  }

  onCancelar(): void {
    this.cancelar.emit();
  }
}
