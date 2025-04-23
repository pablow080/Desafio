import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import {
  MatTableDataSource
} from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Cliente } from '../../../core/models/cliente.model';
import { ClienteService } from '../../../core/services/cliente.service';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule, NgIf, NgForOf } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RouterModule } from '@angular/router';
import { ClienteFilterComponent } from '../cliente-filter/cliente-filter.component';

@Component({
  selector: 'app-cliente-lista',
  templateUrl: './cliente-lista.component.html',
  styleUrls: ['./cliente-lista.component.css'],
  imports: [
    CommonModule,
    NgIf,
    NgForOf,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatDialogModule,
    MatSnackBarModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    RouterModule,
    ClienteFilterComponent
  ]
})
export class ClienteListaComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['id', 'tipoPessoa', 'nome', 'cpfCnpj', 'email', 'ativo', 'acoes'];
  dataSource = new MatTableDataSource<Cliente>([]);
  isLoading = true;
  private destroy$ = new Subject<void>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private clienteService: ClienteService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.carregarClientes();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  carregarClientes(): void {
    this.isLoading = true;
    this.clienteService.listarTodos()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (clientes) => {
          this.dataSource.data = clientes;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Erro ao carregar clientes', error);
          this.snackBar.open('Erro ao carregar clientes', 'Fechar', { duration: 3000 });
          this.isLoading = false;
        }
      });
  }

  applyFilter(filterValue: string): void {
    this.dataSource.filter = filterValue.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  excluirCliente(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Confirmar exclusão',
        message: 'Tem certeza que deseja excluir este cliente?',
        confirmText: 'Excluir',
        cancelText: 'Cancelar'
      }
    });

    dialogRef.afterClosed()
      .pipe(takeUntil(this.destroy$))
      .subscribe(result => {
        if (result) {
          this.clienteService.excluir(id)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: () => {
                this.snackBar.open('Cliente excluído com sucesso', 'Fechar', { duration: 3000 });
                this.carregarClientes();
              },
              error: (error) => {
                console.error('Erro ao excluir cliente', error);
                this.snackBar.open('Erro ao excluir cliente', 'Fechar', { duration: 3000 });
              }
            });
        }
      });
  }

  exportarExcel(): void {
    this.clienteService.exportarExcel()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data: Blob) => {
          const url = window.URL.createObjectURL(data);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'clientes.xlsx';
          document.body.appendChild(a);
          a.click();
          window.URL.revokeObjectURL(url);
        },
        error: (error) => {
          console.error('Erro ao exportar Excel', error);
          this.snackBar.open('Erro ao exportar Excel', 'Fechar', { duration: 3000 });
        }
      });
  }

  exportarPdf(): void {
    this.clienteService.exportarPdf()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data: Blob) => {
          const url = window.URL.createObjectURL(data);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'clientes.pdf';
          document.body.appendChild(a);
          a.click();
          window.URL.revokeObjectURL(url);
        },
        error: (error) => {
          console.error('Erro ao exportar PDF', error);
          this.snackBar.open('Erro ao exportar PDF', 'Fechar', { duration: 3000 });
        }
      });
  }

  onFiltroAplicado(filtros: any): void {
    this.isLoading = true;
    this.clienteService.buscarClientes(filtros)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (clientes) => {
          this.dataSource.data = clientes;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Erro ao filtrar clientes', error);
          this.snackBar.open('Erro ao filtrar clientes', 'Fechar', { duration: 3000 });
          this.isLoading = false;
        }
      });
  }
}
