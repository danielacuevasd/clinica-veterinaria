CREATE TABLE medicamentos (
                              id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                              nombre          VARCHAR(150)   NOT NULL UNIQUE,
                              stock           INT            NOT NULL DEFAULT 0,
                              unidad          VARCHAR(30)    NOT NULL,
                              precio_unitario DECIMAL(10,2)  NOT NULL,
                              activo          BOOLEAN        DEFAULT TRUE,
                              created_at      DATETIME       DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE movimientos_stock (
                                   id             BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   id_medicamento BIGINT        NOT NULL,
                                   tipo           ENUM('ENTRADA','SALIDA') NOT NULL,
                                   cantidad       INT           NOT NULL,
                                   motivo         VARCHAR(300),
                                   fecha          DATETIME      DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;