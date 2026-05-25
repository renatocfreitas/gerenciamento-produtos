package br.gov.caixa.jwt;
import io.smallrye.jwt.build.Jwt;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;

/**
 * Utilitário de assinatura do token
 *
 * Requisito 3:
 * Responsável por gerar o Token assinado.
 * Ele insere o ID do usuário no subject e injeta a lista de permissões (roles)
 * dentro da claim padrão "groups" do microprofile JWT.
 *
 * O método generateToken dentro da classe JwtUtil.java é o coração
 * do mecanismo de segurança baseada em tokens (especificação MicroProfile JWT).
 * Ele pega as informações do usuário vindas do banco de dados e as empacota em uma String criptografada (o token JWT)
 * que o navegador ou aplicativo usará nas próximas requisições.
 */
public class JwtUtil {
    /*
    Define a assinatura do método.
    Ele é public (pode ser chamado de fora da classe),
          static (não precisa dar new na classe para usar) e
          retorna uma String (o próprio token bruto).
    Ele recebe o id do usuário (para sabermos quem está logado) e
               a lista de roles (permissões como ADMIN ou USER)
    vindas da entidade @UserDefinition.
     */
    public static String generateToken(Long id, List<String> roles){
        /*
        Inicia a construção do payload (conteúdo) do JWT.
        O Jwt.claims() cria uma espécie de "construtor" (Builder) do SmallRye JWT.
        A partir daqui, começamos a adicionar as alegações (claims),
        que são os dados que viajarão de forma segura dentro do token.
         */
        return Jwt.claims()
                    /*
                    Define a claim padrão sub (Subject/Assunto).
                    No padrão JWT, o subject identifica de forma única o dono daquele token.
                    Aqui, o id numérico do usuário é convertido para texto (toString()),
                    permitindo que mais tarde, no seu CompraResource,
                    você consiga validar o dono usando jwt.getSubject().
                     */
                .subject(id.toString())
                    /*
                    Define a claim padrão groups do MicroProfile JWT.
                    O ecossistema Quarkus e a especificação Jakarta Security
                    usam a claim groups dentro do token para fazer o mapeamento do Controle de Acesso Baseado em Papéis (RBAC).
                    Ao injetar a lista de privilégios dentro do token mapeada como um HashSet (um conjunto que evita duplicados),
                    o Quarkus saberá se o portador do token pode ou não acessar métodos protegidos por @RolesAllowed("ADMIN").
                    */
                .groups(new HashSet<>(roles))
                    /*
                    Define a claim de expiração exp. Ela estipula o tempo de vida útil do token
                    a partir do milissegundo em que foi gerado. No código, o token dura exatamente 1 hora.
                    Passado esse tempo, o Quarkus rejeitará o token automaticamente com o status HTTP 401 (Unauthorized),
                    obrigando o usuário a fazer login novamente por segurança.
                     */
                .expiresIn(Duration.ofHours(1))
                    /*
                    Define a claim de emissor iss (Issuer).
                    É uma string que assinala quem gerou e assinou digitalmente aquele token.
                    Isso funciona como uma assinatura de segurança: quando a API recebe o token nas rotas protegidas de produtos,
                    ela confronta o valor contido no token com a propriedade mp.jwt.verify.issuer do arquivo application.properties.
                    Se os textos não forem idênticos, o Quarkus bloqueia a requisição.
                     */
                .issuer("https://shopi.com/issuer")
                    /*
                    Realiza o fechamento e a assinatura digital do token.
                    Essa é a linha que pega todas as informações configuradas acima,
                    junta com a chave privada (configurada no arquivo de propriedades) e gera o hash criptográfico final.
                    É esse método que transforma o bloco de dados legível naquela
                    String gigante separada por três pontos (header.payload.signature) que é devolvida no endpoint /auth/login.
                     */
                .sign();
    }
}

