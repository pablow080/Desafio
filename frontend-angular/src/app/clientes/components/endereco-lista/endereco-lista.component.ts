import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Endereco } from '../../../core/models/endereco.model';
import { MatDialog } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { NgForOf, NgIf} from '@angular/common';
import {ConfirmDialogComponent} from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-endereco-lista',
  templateUrl: './endereco-lista.component.html',
  imports: [
    MatButtonModule,
    MatTooltipModule,
    MatIconModule,
    MatDividerModule,
    NgIf,
    NgForOf
  ],
  styleUrls: ['./endereco-lista.component.css']
})
export class EnderecoListaComponent {
  @Input() enderecos: Endereco[] = [];
  @Output() editar = new EventEmitter<Endereco>();
  @Output() excluir = new EventEmitter<number>();

  constructor(private dialog: MatDialog) { }

  onEditar(endereco: Endereco): void {
    this.editar.emit(endereco);
  }

  onExcluir(id: number | undefined): void {
    if (!id) return;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Confirmar exclusão',
        message: 'Tem certeza que deseja excluir este endereço?',
        confirmText: 'Excluir',
        cancelText: 'Cancelar'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.excluir.emit(id);
      }
    });
  }
}
