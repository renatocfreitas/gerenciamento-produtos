package br.gov.caixa.dto;

/**
 *  Define a estrutura exata do JSON que o cliente receberá após autenticar com sucesso (Requisito 3).
 *  Estas são as respostas esperadas:
 *  Situação               | Status | Body
 *  Login bem-sucedido     |  200   | { "token": "<jwt>", "tipo": "Bearer", "role": "ADMIN" }
 *  Credenciais inválicas  |  401   | { "erro": "E-mail ou senha inválidos" }
 *  O token retornado deve ser utilizado no header
 *  Authorization: Bearer <token>
 *  em todas as demais requisições autenticadas.
 */
public class Responses {
    public record LoginResponse(String token, String tipo, String role){}
}
