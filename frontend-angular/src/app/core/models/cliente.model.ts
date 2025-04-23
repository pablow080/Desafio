import { Endereco } from './endereco.model';

export interface Cliente {
  id?: number;
  tipoPessoa: string;
  nome?: string;
  razaoSocial?: string;
  cpfCnpj: string;
  rg?: string;
  inscricaoEstadual?: string;
  dataNascimento?: Date;
  dataCriacao?: Date;
  email: string;
  ativo: boolean;
  enderecos: Endereco[];
}