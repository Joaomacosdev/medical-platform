CREATE TABLE consultas
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    paciente_id       BIGINT       NOT NULL,
    profissional_id   BIGINT       NOT NULL,
    data_solicitacao  DATETIME     NOT NULL,
    data_consulta     DATETIME     NOT NULL,
    tipo              VARCHAR(30)  NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    observacoes       VARCHAR(1000),
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_consultas_paciente FOREIGN KEY (paciente_id) REFERENCES users (id),
    CONSTRAINT fk_consultas_profissional FOREIGN KEY (profissional_id) REFERENCES users (id),
    CONSTRAINT ck_consultas_status CHECK (status IN ('AGENDADA', 'CONFIRMADA', 'CANCELADA', 'REALIZADA'))
) ENGINE = InnoDB;

CREATE INDEX idx_consultas_paciente_id ON consultas (paciente_id);
CREATE INDEX idx_consultas_profissional_id ON consultas (profissional_id);
CREATE INDEX idx_consultas_data_consulta ON consultas (data_consulta);
CREATE INDEX idx_consultas_status ON consultas (status);