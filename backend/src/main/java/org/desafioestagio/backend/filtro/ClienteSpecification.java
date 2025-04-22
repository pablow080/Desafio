package org.desafioestagio.backend.filtro;

import org.desafioestagio.backend.model.Cliente;
import org.springframework.data.jpa.domain.Specification;

public class ClienteSpecification {

    public static Specification<Cliente> nomeLike(String nome) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("nome"), "%" + nome + "%");
    }

    public static Specification<Cliente> razaoSocialLike(String razaoSocial) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("razaoSocial"), "%" + razaoSocial + "%");
    }

    public static Specification<Cliente> cpfCnpjLike(String cpfCnpj) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("cpfCnpj"), "%" + cpfCnpj + "%");
    }

    public static Specification<Cliente> emailLike(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("email"), "%" + email + "%");
    }

    public static Specification<Cliente> rgLike(String rg) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("rg"), "%" + rg + "%");
    }

    public static Specification<Cliente> inscricaoEstadualLike(String inscricaoEstadual) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("inscricaoEstadual"), "%" + inscricaoEstadual + "%");
    }
}
