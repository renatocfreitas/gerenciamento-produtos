package br.gov.caixa.resource;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import br.gov.caixa.model.Produto;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Atende aos requisitos 1, 4 e 5: reúne as regras do RBAC (autorização por roles) e a otimizaçào de cache.
 * - @Authenticated:         Garante que qualquer usuário logado (USER ou ADMIN) possa listar produtos (Requisito 5).
 * - @CacheResult:           Salva em memória o resultado da consulta para evitar hits desnecessários ao banco (Requisito 4).
 * - @RolesAllowed("ADMIN"): Bloqueia métodos de modificação (POST, PUT, DELETE) para usuários que não sejam administradores.
 * - @CacheInvalidateAll:    Limpa o cache se o catálogo mudar, evitando "dados sujos".
 */
@Path("/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoResource {
    @GET
    @Authenticated // Qualquer usuário autenticado acessa
    @CacheResult(cacheName = "produtos-cache") // Requisito 4: Adiciona Cache
    public List<Produto> getProdutos(){
        return Produto.listAll();
    }

    @POST
    @RolesAllowed("ADMIN") // Requisito 5: Restrito ao Administrador
    @Transactional
    @CacheInvalidateAll(cacheName = "produtos-cache") // Invalida cache ao alterar dados
    public Response criarProduto(Produto produto){
        produto.persist();
        return Response.status(Response.Status.CREATED).entity(produto).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Transactional
    @CacheInvalidateAll(cacheName = "produtos-cache")
    public Response atualizarProduto(@PathParam("id") Long id, Produto alteracoes){
        Produto produto = Produto.findById(id);
        if (produto == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        produto.nome = alteracoes.nome;
        produto.descricao = alteracoes.descricao;
        produto.preco = alteracoes.preco;
        produto.estoque = alteracoes.estoque;
        return Response.ok(produto).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Transactional
    @CacheInvalidateAll(cacheName = "produtos-cache")
    public Response deleteProduto(@PathParam("id") Long id){
        boolean deletado = Produto.deleteById(id);
        return deletado ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

}
