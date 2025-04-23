export interface Endereco {
  id?: number;
  logradouro: string;
  numero: string;
  cep: string;
  bairro: string;
  telefone?: string;
  cidade: string;
  estado: string;
  principal: boolean;
  complemento?: string;
  clienteId?: number;
}