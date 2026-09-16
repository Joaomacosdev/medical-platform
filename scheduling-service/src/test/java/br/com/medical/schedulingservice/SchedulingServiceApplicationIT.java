package br.com.medical.schedulingservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Smoke test do contexto completo da aplicacao (web + JPA/Flyway + RabbitMQ + Security) contra
 * containers reais via Testcontainers. Requer Docker; roda na fase "verify" (maven-failsafe-plugin).
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SchedulingServiceApplicationIT {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.36").withDatabaseName("scheduling_db_test");

    @Container
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3.13-management");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("security.jwt.secret", () -> "integration-test-secret");
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);
    }

    @org.springframework.beans.factory.annotation.Value("${local.server.port}")
    int port;

    private java.net.http.HttpResponse<String> request(String method, String path, String body, String token) throws Exception {
        var builder = java.net.http.HttpRequest.newBuilder(java.net.URI.create("http://localhost:" + port + path));
        if (token != null) builder.header("Authorization", "Bearer " + token);
        if (body != null) builder.header("Content-Type", "application/json");
        builder.method(method, body == null ? java.net.http.HttpRequest.BodyPublishers.noBody()
                : java.net.http.HttpRequest.BodyPublishers.ofString(body));
        return java.net.http.HttpClient.newHttpClient().send(builder.build(), java.net.http.HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void onboardingAndPatientIsolationThroughSecurityChain() throws Exception {
        var token = com.auth0.jwt.JWT.create().withIssuer("auth-service").withSubject("7007")
                .withClaim("email", "ana.it@example.com").withClaim("role", "PACIENTE")
                .withExpiresAt(java.time.Instant.now().plusSeconds(300))
                .sign(com.auth0.jwt.algorithms.Algorithm.HMAC256("integration-test-secret"));
        org.junit.jupiter.api.Assertions.assertEquals(401, request("GET", "/api/v1/consultas", null, null).statusCode());
        org.junit.jupiter.api.Assertions.assertEquals(409, request("GET", "/api/v1/consultas", null, token).statusCode());
        String body = "{\"nome\":\"Ana IT\",\"emailContato\":\"ana.contact.it@example.com\",\"authUserId\":3}";
        var created = request("PUT", "/api/v1/cadastro/me", body, token);
        org.junit.jupiter.api.Assertions.assertEquals(200, created.statusCode(), created.body());
        org.junit.jupiter.api.Assertions.assertTrue(created.body().contains("\"authUserId\":7007"));
        org.junit.jupiter.api.Assertions.assertEquals(created.body(), request("PUT", "/api/v1/cadastro/me", body, token).body());
        org.junit.jupiter.api.Assertions.assertEquals("[]", request("GET", "/api/v1/consultas", null, token).body());
        org.junit.jupiter.api.Assertions.assertEquals(403, request("POST", "/api/v1/consultas", "{\"pacienteId\":4,\"profissionalId\":1,\"dataConsulta\":\"2099-01-01T10:00:00\",\"tipo\":\"PRESENCIAL\"}", token).statusCode());
        var basic = java.net.http.HttpRequest.newBuilder(java.net.URI.create("http://localhost:" + port + "/api/v1/auth/login"))
                .header("Authorization", "Basic " + java.util.Base64.getEncoder().encodeToString("medico@hospital.com:Senha@123".getBytes()))
                .POST(java.net.http.HttpRequest.BodyPublishers.noBody()).build();
        org.junit.jupiter.api.Assertions.assertEquals(401, java.net.http.HttpClient.newHttpClient()
                .send(basic, java.net.http.HttpResponse.BodyHandlers.ofString()).statusCode());
    }

    @Test
    void contextLoads() {
    }
}