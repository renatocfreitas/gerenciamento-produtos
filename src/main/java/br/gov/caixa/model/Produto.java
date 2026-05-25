package br.gov.caixa.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="produtos")
public class Produto extends PanacheEntity {
    public String nome;
    public String descricao;
    public Double preco;
    public int estoque;
}
