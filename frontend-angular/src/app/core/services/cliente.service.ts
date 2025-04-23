import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from '../models/cliente.model';
import { DatePipe } from '@angular/common';  // Importando o DatePipe para formatação de datas

@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private apiUrl = 'http://localhost:8080/api/clientes';

  constructor(private http: HttpClient, private datePipe: DatePipe) { }

  // Método para formatar a data para o formato yyyy-MM-dd
  private formatDate(date: string | null): string | null {
    if (date) {
      return this.datePipe.transform(date, 'yyyy-MM-dd');  // Formata a data no padrão yyyy-MM-dd
    }
    return null;  // Retorna null se a data for nula
  }

  // Listar todos os clientes
  listarTodos(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.apiUrl);
  }

  // Buscar cliente por ID
  buscarPorId(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${id}`);
  }

  // Salvar cliente
  salvar(cliente: Cliente): Observable<Cliente> {
    const clienteComDataFormatada = {
      ...cliente,
      dataNascimento: this.formatDate(cliente.dataNascimento),
      dataCriacao: this.formatDate(cliente.dataCriacao),
    };

    return this.http.post<Cliente>(this.apiUrl, clienteComDataFormatada);
  }

  // Atualizar cliente
  atualizar(id: number, cliente: Cliente): Observable<Cliente> {
    const clienteComDataFormatada = {
      ...cliente,
      dataNascimento: this.formatDate(cliente.dataNascimento),
      dataCriacao: this.formatDate(cliente.dataCriacao),
    };

    return this.http.put<Cliente>(`${this.apiUrl}/${id}`, clienteComDataFormatada);
  }

  // Excluir cliente
  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Exportar clientes para Excel
  exportarExcel(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/exportar/excel`, { responseType: 'blob' });
  }

  // Exportar clientes para PDF
  exportarPdf(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/exportar/pdf`, { responseType: 'blob' });
  }
}
