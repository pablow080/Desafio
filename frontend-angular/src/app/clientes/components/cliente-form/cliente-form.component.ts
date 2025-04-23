import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ClienteService } from '../../../core/services/cliente.service';
import { Cliente } from '../../../core/models/cliente.model';
import { Endereco } from '../../../core/models/endereco.model';
import { CommonModule, NgIf, NgForOf, DatePipe } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-cliente-form',
  templateUrl: './cliente-form.component.html',
  styleUrls: ['./cliente-form.component.css'],
  providers: [DatePipe],
  imports: [
    CommonModule,
    NgIf,
    NgForOf,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatOptionModule,
    MatCardModule,
    MatDividerModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatCheckboxModule,
    MatDatepickerModule,
    MatNativeDateModule,
    RouterModule
  ]
})
export class ClienteFormComponent implements OnInit, OnDestroy {
  clienteForm!: FormGroup;
  isEditMode = false;
  clienteId: number | null = null;
  isLoading = false;
  estados = [
    'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG',
    'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'
  ];
  private routeSub: any;

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private route: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.initForm();

    this.routeSub = this.route.params.subscribe(params => {
      if (params['id']) {
        this.clienteId = +params['id'];
        this.isEditMode = true;
        this.carregarCliente(this.clienteId);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.routeSub) {
      this.routeSub.unsubscribe();
    }
  }

  initForm(): void {
    this.clienteForm = this.fb.group({
      tipoPessoa: ['Física', Validators.required],
      nome: ['', Validators.required],
      razaoSocial: [''],
      cpfCnpj: ['', Validators.required],
      rg: [''],
      inscricaoEstadual: [''],
      dataNascimento: [null],
      dataCriacao: [null],
      email: ['', [Validators.required, Validators.email]],
      ativo: [true],
      enderecos: this.fb.array([this.createEnderecoFormGroup()])
    });

    // Handle tipo pessoa changes
    this.clienteForm.get('tipoPessoa')?.valueChanges.subscribe(tipo => {
      if (tipo === 'Física') {
        this.clienteForm.get('nome')?.setValidators(Validators.required);
        this.clienteForm.get('razaoSocial')?.clearValidators();
        this.clienteForm.get('rg')?.setValidators(Validators.required);
        this.clienteForm.get('inscricaoEstadual')?.clearValidators();
      } else {
        this.clienteForm.get('nome')?.clearValidators();
        this.clienteForm.get('razaoSocial')?.setValidators(Validators.required);
        this.clienteForm.get('rg')?.clearValidators();
        this.clienteForm.get('inscricaoEstadual')?.setValidators(Validators.required);
      }
      this.clienteForm.get('nome')?.updateValueAndValidity();
      this.clienteForm.get('razaoSocial')?.updateValueAndValidity();
      this.clienteForm.get('rg')?.updateValueAndValidity();
      this.clienteForm.get('inscricaoEstadual')?.updateValueAndValidity();
    });
  }

  createEnderecoFormGroup(): FormGroup {
    return this.fb.group({
      id: [null],
      logradouro: ['', Validators.required],
      numero: ['', Validators.required],
      cep: ['', Validators.required],
      bairro: ['', Validators.required],
      telefone: [''],
      cidade: ['', Validators.required],
      estado: ['', Validators.required],
      principal: [false],
      complemento: ['']
    });
  }

  get enderecos(): FormArray {
    return this.clienteForm.get('enderecos') as FormArray;
  }

  get nomeControl() {
    return this.clienteForm.get('nome');
  }

  carregarCliente(id: number): void {
    this.isLoading = true;
    this.clienteService.buscarPorId(id).subscribe({
      next: (cliente) => {
        this.preencherFormulario(cliente);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Erro ao carregar cliente', error);
        this.snackBar.open('Erro ao carregar dados do cliente', 'Fechar', { duration: 3000 });
        this.isLoading = false;
        this.router.navigate(['/clientes']);
      }
    });
  }

  preencherFormulario(cliente: Cliente): void {
    // Clear current enderecos
    while (this.enderecos.length) {
      this.enderecos.removeAt(0);
    }

    // Add each endereco
    cliente.enderecos.forEach(endereco => {
      this.enderecos.push(this.fb.group({
        id: [endereco.id],
        logradouro: [endereco.logradouro, Validators.required],
        numero: [endereco.numero, Validators.required],
        cep: [endereco.cep, Validators.required],
        bairro: [endereco.bairro, Validators.required],
        telefone: [endereco.telefone],
        cidade: [endereco.cidade, Validators.required],
        estado: [endereco.estado, Validators.required],
        principal: [endereco.principal],
        complemento: [endereco.complemento]
      }));
    });

    // Set cliente data
    this.clienteForm.patchValue({
      tipoPessoa: cliente.tipoPessoa,
      nome: cliente.nome,
      razaoSocial: cliente.razaoSocial,
      cpfCnpj: cliente.cpfCnpj,
      rg: cliente.rg,
      inscricaoEstadual: cliente.inscricaoEstadual,
      dataNascimento: cliente.dataNascimento,
      email: cliente.email,
      ativo: cliente.ativo
    });
  }

  adicionarEndereco(): void {
    this.enderecos.push(this.createEnderecoFormGroup());
  }

  removerEndereco(index: number): void {
    if (this.enderecos.length > 1) {
      this.enderecos.removeAt(index);
    } else {
      this.snackBar.open('O cliente deve ter pelo menos um endereço', 'Fechar', { duration: 3000 });
    }
  }

  marcarComoPrincipal(index: number): void {
    for (let i = 0; i < this.enderecos.length; i++) {
      this.enderecos.at(i).get('principal')?.setValue(i === index);
    }
  }

  onSubmit(): void {
    if (this.clienteForm.invalid) {
      this.snackBar.open('Por favor, preencha todos os campos obrigatórios', 'Fechar', { duration: 3000 });
      return;
    }

    // Set pelo menos um endereço como principal
    let temPrincipal = false;
    for (let i = 0; i < this.enderecos.length; i++) {
      if (this.enderecos.at(i).get('principal')?.value) {
        temPrincipal = true;
        break;
      }
    }

    if (!temPrincipal && this.enderecos.length > 0) {
      this.enderecos.at(0).get('principal')?.setValue(true);
    }

    const cliente: Cliente = this.prepararDados();

    this.isLoading = true;
    if (this.isEditMode && this.clienteId) {
      this.clienteService.atualizar(this.clienteId, cliente).subscribe({
        next: () => {
          this.snackBar.open('Cliente atualizado com sucesso', 'Fechar', { duration: 3000 });
          this.isLoading = false;
          this.router.navigate(['/clientes']);
        },
        error: (error) => {
          console.error('Erro ao atualizar cliente', error);
          this.snackBar.open('Erro ao atualizar cliente', 'Fechar', { duration: 3000 });
          this.isLoading = false;
        }
      });
    } else {
      this.clienteService.salvar(cliente).subscribe({
        next: () => {
          this.snackBar.open('Cliente criado com sucesso', 'Fechar', { duration: 3000 });
          this.isLoading = false;
          this.router.navigate(['/clientes']);
        },
        error: (error) => {
          console.error('Erro ao criar cliente', error);
          this.snackBar.open('Erro ao criar cliente', 'Fechar', { duration: 3000 });
          this.isLoading = false;
        }
      });
    }
  }

  prepararDados(): Cliente {
    const formValue = this.clienteForm.value;
    const cliente: Cliente = {
      tipoPessoa: formValue.tipoPessoa,
      nome: formValue.nome,
      razaoSocial: formValue.razaoSocial,
      cpfCnpj: formValue.cpfCnpj,
      rg: formValue.rg,
      inscricaoEstadual: formValue.inscricaoEstadual,
      dataNascimento: formValue.dataNascimento,
      dataCriacao: formValue.dataCriacao,
      email: formValue.email,
      ativo: formValue.ativo,
      enderecos: formValue.enderecos.map((endereco: any) => {
        const enderecoObj: Endereco = {
          id: endereco.id || undefined,
          logradouro: endereco.logradouro,
          numero: endereco.numero,
          cep: endereco.cep,
          bairro: endereco.bairro,
          telefone: endereco.telefone,
          cidade: endereco.cidade,
          estado: endereco.estado,
          principal: endereco.principal,
          complemento: endereco.complemento,
          clienteId: this.clienteId || undefined
        };
        return enderecoObj;
      })
    };

    if (this.isEditMode && this.clienteId) {
      cliente.id = this.clienteId;
    }

    return cliente;
  }

  cancelar(): void {
    this.router.navigate(['/clientes']);
  }
}
