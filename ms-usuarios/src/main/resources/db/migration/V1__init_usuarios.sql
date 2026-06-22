CREATE TABLE duenos (
                        id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                        nombre           VARCHAR(100) NOT NULL,
                        apellido         VARCHAR(100) NOT NULL,
                        email            VARCHAR(150) NOT NULL UNIQUE,
                        telefono         VARCHAR(20),
                        rut              VARCHAR(20)  NOT NULL UNIQUE,
                        activo           BOOLEAN      DEFAULT TRUE,
                        created_at       DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;