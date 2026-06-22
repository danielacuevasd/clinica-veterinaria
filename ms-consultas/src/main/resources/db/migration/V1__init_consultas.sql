CREATE TABLE consultas (
                           id             BIGINT AUTO_INCREMENT PRIMARY KEY,
                           id_cita        BIGINT        NOT NULL,
                           id_mascota     BIGINT        NOT NULL,
                           id_veterinario BIGINT        NOT NULL,
                           diagnostico    VARCHAR(500)  NOT NULL,
                           peso           DECIMAL(5,2),
                           temperatura    DECIMAL(4,1),
                           observaciones  TEXT,
                           fecha          DATETIME      DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;