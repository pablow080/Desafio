import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit } from '@angular/core';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatNoDataRow, MatRow, MatRowDef,
  MatTable,
  MatTableDataSource
} from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Cliente } from '../../../core/models/cliente.model';
import { ClienteService } from '../../../core/services/cliente.service';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import {RouterLink} from '@angular/router';
import {MatButton, MatIconButton, MatMiniFabButton} from '@angular/material/button';
import {MatCard, MatCardContent} from '@angular/material/card';
import {MatFormField, MatInput, MatInputModule, MatSuffix} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';
import {MatTooltip} from '@angular/material/tooltip';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {NgClass, NgIf} from '@angular/common';
import {MatFormFieldModule} from '@angular/material/form-field';

@Component({
  selector: 'app-cliente-lista',
  templateUrl: './cliente-lista.component.html',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatButton,
    RouterLink,
    MatCard,
    MatCardContent,
    MatFormField,
    MatInput,
    MatSuffix,
    MatIcon,
    MatMiniFabButton,
    MatTooltip,
    MatProgressSpinner,
    MatTable,
    MatSort,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCell,
    MatCellDef,
    NgClass,
    MatIconButton,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatNoDataRow,
    MatPaginator,
    NgIf
  ],
  styleUrls: ['./cliente-lista.component.css']
})
export class ClienteListaComponent implements OnInit, OnDestroy, AfterViewInit {
  displayedColumns: string[] = ['id', 'tipoPessoa', 'nome', 'cpfCnpj', 'email', 'ativo', 'acoes'];
  dataSource = new MatTableDataSource<Cliente>([]);
  isLoading = true;

  private destroy$ = new Subject<void>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private clienteService: ClienteService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.carregarClientes();
  }

  ngAfterViewInit(): void {
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
          console.log('Clientes carregados:', clientes); // Verifique os dados retornados
          this.dataSource.data = clientes;  // Atualizando a tabela com os dados
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
          a.click();
          window.URL.revokeObjectURL(url);
        },
        error: (error) => {
          console.error('Erro ao exportar PDF', error);
          this.snackBar.open('Erro ao exportar PDF', 'Fechar', { duration: 3000 });
        }
      });
  }
}
