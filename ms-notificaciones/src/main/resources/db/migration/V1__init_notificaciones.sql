CREATE TABLE notificaciones (
                                id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                                tipo            VARCHAR(50)   NOT NULL,
                                id_destinatario BIGINT        NOT NULL,
                                mensaje         VARCHAR(500)  NOT NULL,
                                enviado         BOOLEAN       DEFAULT FALSE,
                                fecha_envio     DATETIME      DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;