CREATE TABLE available_slots
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    profissional_id  BIGINT   NOT NULL,
    data_hora        DATETIME NOT NULL,
    duracao_minutos  INT      NOT NULL DEFAULT 30,
    disponivel       BOOLEAN  NOT NULL DEFAULT TRUE,
    consulta_id      BIGINT   NULL,
    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_slots_profissional FOREIGN KEY (profissional_id) REFERENCES users (id),
    CONSTRAINT fk_slots_consulta FOREIGN KEY (consulta_id) REFERENCES consultas (id),
    CONSTRAINT uk_slots_profissional_datahora UNIQUE (profissional_id, data_hora)
) ENGINE = InnoDB;

CREATE INDEX idx_slots_profissional_data ON available_slots (profissional_id, data_hora);
CREATE INDEX idx_slots_disponivel ON available_slots (disponivel);