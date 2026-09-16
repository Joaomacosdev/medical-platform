package br.com.medical.authservice.infra.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Authentication Service API")
                        .description("""
                            Serviço responsável pela autenticação e gerenciamento
                            de usuários da aplicação Tech Challenge – Fase 3.

                            Esta aplicação foi desenvolvida como um serviço
                            independente, seguindo uma arquitetura baseada em
                            microsserviços e boas práticas de desenvolvimento de APIs.

                            Responsabilidades principais:
                            • Gerenciamento de usuários
                            • Autenticação
                            • Autorização
                            • Controle de acesso baseado em roles
                            • Validação de dados
                            • Tratamento padronizado de exceções
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("João Marcos")
                                .url("https://www.linkedin.com/in/jo%C3%A3o-marcos-aragao/")
                        )
                );
    }

}
