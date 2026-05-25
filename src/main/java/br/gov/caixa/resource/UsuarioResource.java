package br.gov.caixa.resource;

import br.gov.caixa.dto.Requests;
import br.gov.caixa.dto.Responses;
import br.gov.caixa.jwt.JwtUtil;
import br.gov.caixa.model.Usuario;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Para a entidade Usuario (requisito 2), aqui criamos os endpoints de autenticação (/auth).
 * POST /auth/register — Cadastro de usuário
 * POST /auth/login    — Login e geração de token JWT
 *
 * Criar as portas de entrada da API para o Usuario.
 * Contribuição (Requisito 2 e 3):
 * Expõe a rota /auth/register (pública) e
 *       a rota /auth/login,
 * validando o hash criptográfico e devolvendo o token Bearer gerado pelo JwtUtil.
 */

@Path("/auth") //define a rota base no servidor. Qualquer requisição que este Controller gerenciar começará obrigatoriamente com http://localhost:8080/auth.
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    @POST
    @Path("/register") //Define o subcaminho, tornando a URL completa POST /auth/register.
    @Transactional     //Crucial para o Hibernate Panache. Avisa ao Quarkus que este método abre uma transação com o banco de dados. Se algo der errado, ele faz o rollback automático.
    //O corpo da requisição JSON é convertido automaticamente pelo Quarkus para este record (DTO).
    public Response cadastra (Requests.CadastraUsuarioRequest request){

        /*
        Cria uma nova instância da entidade JPA Usuario mapeando as propriedades que vieram do DTO.
        Nota-se que a permissão (role) é envelopada em uma List<String>, pois o JPA Security trabalha com múltiplas permissões por usuário.
        Lembrete: O construtor da entidade já faz o hash Bcrypt da senha.
         */
        Usuario usuario = new Usuario(request.nome(), request.email(), request.senha(), List.of(request.role()));
        /*
        Comando do Panache (Active Record) que executa o comando SQL INSERT INTO usuarios ... no banco de dados, salvando o novo usuário.
         */
        usuario.persist();
        /*
        Response.created(...):
            Retorna o código de status HTTP 201 Created, que é a boa prática de design RESTful para criação de registros.
            Ele define no cabeçalho Location que o próximo passo lógico é o /auth/login.
        .entity(usuario): Devolve no corpo da resposta os dados do usuário cadastrado (com a senha já criptografada).
        .build(): Finaliza a construção do objeto Response.
         */
        return Response.created(URI.create("/auth/login")).entity(usuario).build();
    }

    @POST
    @Path("/login")
    /*
    Define o endpoint de autenticação mapeado na URL POST /auth/login.
    Este método não precisa de @Transactional porque faz apenas consultas (SELECT), sem alterações na base.
     */
    public Response login (Requests.RealizaLoginRequest request){
        /*
        Utiliza o método utilitário find do Panache para buscar no banco de dados um usuário
        cujo campo email seja igual ao fornecido no JSON de login.
        O método .firstResult() garante que trará apenas um registro ou null caso não encontre.
         */
        Usuario usuario = Usuario.find("email = :email", Map.of("email", request.email())).firstResult();
        /*
        Validação de segurança. Se o e-mail não existir na base de dados, a API interrompe o fluxo imediatamente
        respondendo com o status HTTP 403 Forbidden (Acesso proibido).
        Validação da senha. O método BcryptUtil.matches pega a senha em texto puro enviada no login,
        aplica o algoritmo de hash e a compara com o hash guardado no banco de dados. Se elas não baterem, retorna 403 Forbidden.
        Nota de Segurança: Ambas as falhas (e-mail inexistente ou senha errada) retornam o mesmo erro 403
        para evitar dar pistas a possíveis atacantes sobre quais e-mails existem no sistema.
         */
        if (usuario == null || !BcryptUtil.matches(request.senha(), usuario.getSenha())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        String token = JwtUtil.generateToken(usuario.id, usuario.getRole());
        /*
        Response.ok(...): Se o usuário passou por todas as validações, responde com o status HTTP 200 OK.
        new Responses.LoginResponse(...): Instancia o DTO de saída esperado pelo Requisito 3 do projeto.
            Invoca o JwtUtil.generateToken(usuario.id, usuario.getRole())
                para assinar digitalmente o token JWT contendo as permissões do usuário.
            Define o tipo do token como "Bearer".
            Extrai a primeira role encontrada (get(0)) para indicar ao cliente o nível de acesso (ADMIN ou USER).
        */
        return Response.ok(new Responses.LoginResponse(token, "Bearer", usuario.getRole().get(0))).build();
    }
}
