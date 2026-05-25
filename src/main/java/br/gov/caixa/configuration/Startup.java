package br.gov.caixa.configuration;

import br.gov.caixa.model.Usuario;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Usuários padrão (data migration ou ApplicationScoped)
 * Ao iniciar a aplicação, dois usuários devem estar disponíveis
 * Nome     	    E-mail	        Senha	    Role
 * Admin Sistema	admin@loja.com	admin123	ADMIN
 * User Padrão	    user@loja.com	user123	    USER
 *
 * Dica: Você pode criá-los via import.sql na pasta resources, ou em um bean @ApplicationScoped com @Observes StartupEvent.
 *
 * O arquivo PROJETO_FINAL.md exige que os usuários admin@loja.com e user@loja.com existam assim que a aplicação iniciar.
 * Criaremos uma classe observadora do ciclo de vida do Quarkus.
 *
 * Garante que o ambiente de testes/avaliação do professor suba pronto, povoando a base em memória automaticamente se estiver vazia.
 */

@Singleton
public class Startup {
    @Transactional
    public void onStart (@Observes StartupEvent ev){
        if (Usuario.count() == 0){
            Usuario admin = new Usuario("Administrador", "admin@loja.com", "admin123", List.of("ADMIN"));
            admin.persist();
            Usuario user = new Usuario("Usuario Comum", "user@loja.com", "user123", List.of("USER"));
            user.persist();
        }
    }
}
