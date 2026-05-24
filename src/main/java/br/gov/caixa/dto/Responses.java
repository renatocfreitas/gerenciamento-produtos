package br.gov.caixa.dto;

/**
 *  Define a estrutura exata do JSON que o cliente receberá após autenticar com sucesso (Requisito 3).
 */
public class Responses {
    public record LoginResponse(String token, String tipo, String role){}
}
