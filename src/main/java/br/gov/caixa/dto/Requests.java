package br.gov.caixa.dto;

/**
 * Criamos os DTOs primeiro para que os nossos Resources/Controllers tenham os objetos de transporte definidos.
 * Agrupamos as estruturas em registros (records).
 * Isola os dados que vêm da requisição HTTP (evitando expor diretamente as entidades JPA).
 * *****************************************************************************************
 * POST /auth/register - Cadastro de usuário
 * Request Body:
 * {
 *  "nome":"João Silva",
 *  "email":"joao@email.com",
 *  "senha":"minhaSenha123",
 *  "role":"USER"
 * }
 *
 * Respostas esperadas:
 * Situação                     Status  Body
 * Cadastro realizado	        201    { "id": 1, "nome": "João Silva", "email": "joao@email.com", "role": "USER" }
 * E-mail já cadastrado	        409    { "erro": "E-mail já cadastrado" }
 * Campos obrigatórios ausentes	400    { "erro": "Campos obrigatórios não informados" }
 * !!! A senha não deve ser retornada em nenhuma resposta !!!
 * *****************************************************************************************
 *
 */
public class Requests {
    public record CadastraUsuarioRequest(String nome, String email, String senha, String role){}
    public record    RealizaLoginRequest(             String email, String senha             ){}
}
