import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from '../models/cliente.model';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private apiUrl = '/api/clientes';

  constructor(private http: HttpClient) { }

  listarTodos(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.apiUrl);
  }

  buscarPorId(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${id}`);
  }

  buscarClientes(filtros: any): Observable<Cliente[]> {
    let params = new HttpParams();

    if (filtros.nome) params = params.set('nome', filtros.nome);
    if (filtros.razaoSocial) params = params.set('razaoSocial', filtros.razaoSocial);
    if (filtros.cpfCnpj) params = params.set('cpfCnpj', filtros.cpfCnpj);
    if (filtros.email) params = params.set('email', filtros.email);
    if (filtros.rg) params = params.set('rg', filtros.rg);
    if (filtros.inscricaoEstadual) params = params.set('inscricaoEstadual', filtros.inscricaoEstadual);

    return this.http.get<Cliente[]>(`${this.apiUrl}/buscar`, { params });
  }

  salvar(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, cliente);
  }

  atualizar(id: number, cliente: Cliente): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/${id}`, cliente);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  exportarExcel(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/exportar/excel`, {
      responseType: 'blob'
    });
  }

  exportarPdf(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/exportar/pdf`, {
      responseType: 'blob'
    });
  }

  getClienteById(id: string) {
    return this.http.get<Cliente>(`${this.apiUrl}/${id}`);
  }

  updateCliente(clienteData: any) {
    return this.http.put<Cliente>(`${this.apiUrl}/${clienteData.id}`, clienteData);
  }

  createCliente(clienteData: any) {
    return this.http.post<Cliente>(this.apiUrl, clienteData);
  }
}
