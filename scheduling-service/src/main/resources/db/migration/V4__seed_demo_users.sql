-- Usuarios de demonstracao para testes locais e para a collection Postman.
-- Senha em texto plano para todos: Senha@123 (hash BCrypt abaixo).
INSERT INTO users (name, email, password, role) VALUES
    ('Dr. Ricardo Silva', 'medico@hospital.com', '$2a$10$QgYMwoYeZfwzF2fso8wuOuihEicuvZVuRtJb9oZQd5lvwt55iwL3i', 'MEDICO'),
    ('Enf. Ana Souza', 'enfermeiro@hospital.com', '$2a$10$N8BrFIJ7DObJAWSL1Hh72eL3fckFEq9hJ3KNr9XgxzhPOzsuPk6Qi', 'ENFERMEIRO'),
    ('Joao Lima', 'paciente@hospital.com', '$2a$10$T220NdG97FtF8o1j29Eil.Rp1TO9iunD2QiXa3W2DOwTm2OX5XwsS', 'PACIENTE');