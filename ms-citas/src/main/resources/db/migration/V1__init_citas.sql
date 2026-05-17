CREATE TABLE citas (
                       id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                       id_mascota      BIGINT       NOT NULL,
                       id_veterinario  BIGINT       NOT NULL,
                       id_dueno        BIGINT       NOT NULL,
                       fecha_hora      DATETIME     NOT NULL,
                       motivo          VARCHAR(300),
                       estado          ENUM('PENDIENTE','CONFIRMADA','CANCELADA') DEFAULT 'PENDIENTE',
                       created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;