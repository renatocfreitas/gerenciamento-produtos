package br.gov.caixa.dto;

/**
 * Criamos os DTOs primeiro para que os nossos Resources/Controllers tenham os objetos de transporte definidos.
 * Agrupamos as estruturas em registros (records).
 * Isola os dados que vêm da requisição HTTP (evitando expor diretamente as entidades JPA).
 */
public class Requests {
    public record CadastraUsuarioRequest(String nome, String email, String senha, String role){}
    public record    RealizaLoginRequest(             String email, String senha){}
}
