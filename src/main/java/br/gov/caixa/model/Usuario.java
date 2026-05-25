package br.gov.caixa.model;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.security.jpa.Password;
import io.quarkus.security.jpa.Roles;
import io.quarkus.security.jpa.UserDefinition;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;
import io.quarkus.security.jpa.Username;

/**
 * Mapeia a tabela.
 * A especificação JPA permite ao Quarkus gerenciar a autenticação direto pelas tabelas do banco de dados.
 *
 * Atende aos requisitos 2 e 3 criando a persistência de usuário com senhas criptografadas
 * via BcryptUtil no construtor.
 *
 */

@Entity
@Table(name = "usuarios")
@UserDefinition
public class Usuario extends PanacheEntity {
    private String nome;

    @Username
    private String email;

    @Password
    private String senha;

    @Roles
    private List<String> role;

    protected Usuario(){}

    public Usuario(String nome, String email, String senha, List<String> role){
        this.nome = nome;
        this.email = email;
        this.senha = BcryptUtil.bcryptHash(senha);
        this.role = role;
    }

    public String getNome(){ return nome; }
    public String getEmail(){ return email; }
    public String getSenha(){ return senha; }
    public List<String> getRole(){ return role; }
}
