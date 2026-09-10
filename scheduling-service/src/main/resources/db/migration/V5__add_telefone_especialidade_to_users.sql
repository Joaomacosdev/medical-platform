ALTER TABLE users
    ADD COLUMN telefone      VARCHAR(20)  NULL,
    ADD COLUMN especialidade VARCHAR(100) NULL;

UPDATE users SET telefone = '4002-8922', especialidade = 'Cardiologia' WHERE email = 'medico@hospital.com';
UPDATE users SET telefone = '4002-8922' WHERE email = 'enfermeiro@hospital.com';
UPDATE users SET telefone = '4002-8922' WHERE email = 'paciente@hospital.com';