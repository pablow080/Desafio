import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Cliente } from '../../../core/models/cliente.model';
import { ClienteService } from '../../../core/services/cliente.service';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CommonModule, NgIf, NgForOf } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-cliente-detalhe',
  templateUrl: './cliente-detalhe.component.html',
  styleUrls: ['./cliente-detalhe.component.css'],
  imports: [
    CommonModule,
    NgIf,
    NgForOf,
    MatIconModule,
    MatCardModule,
    MatDividerModule,
    MatTabsModule,
    MatProgressSpinnerModule
  ]
})
export class ClienteDetalheComponent implements OnInit, OnDestroy {
  cliente: Cliente | null = null;
  isLoading = true;
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private clienteService: ClienteService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.route.params
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        if (params['id']) {
          this.carregarCliente(+params['id']);
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  carregarCliente(id: number): void {
    this.isLoading = true;
    this.clienteService.buscarPorId(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (cliente) => {
          this.cliente = cliente;
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

  editarCliente(): void {
    if (this.cliente) {
      this.router.navigate(['/clientes/editar', this.cliente.id]);
    }
  }

  excluirCliente(): void {
    if (!this.cliente) return;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Confirmar exclusão',
        message: 'Tem certeza que deseja excluir este cliente? Esta ação não pode ser desfeita.',
        confirmText: 'Excluir',
        cancelText: 'Cancelar'
      }
    });

    dialogRef.afterClosed()
      .pipe(takeUntil(this.destroy$))
      .subscribe(result => {
        if (result && this.cliente?.id) {
          this.clienteService.excluir(this.cliente.id)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: () => {
                this.snackBar.open('Cliente excluído com sucesso', 'Fechar', { duration: 3000 });
                this.router.navigate(['/clientes']);
              },
              error: (error) => {
                console.error('Erro ao excluir cliente', error);
                this.snackBar.open('Erro ao excluir cliente', 'Fechar', { duration: 3000 });
              }
            });
        }
      });
  }

  voltar(): void {
    this.router.navigate(['/clientes']);
  }

  exportarPdf(): void {
    if (!this.cliente) return;

    this.clienteService.exportarPdf()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data: Blob) => {
          const url = window.URL.createObjectURL(data);
          const a = document.createElement('a');
          a.href = url;
          a.download = `cliente_${this.cliente?.id}.pdf`;
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
}
