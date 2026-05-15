CREATE TABLE mascotas (
                          id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre           VARCHAR(100) NOT NULL,
                          especie          VARCHAR(50)  NOT NULL,
                          raza             VARCHAR(100),
                          fecha_nacimiento DATE,
                          id_dueno         BIGINT       NOT NULL,
                          activo           BOOLEAN      DEFAULT TRUE,
                          created_at       DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE historial_medico (
                                  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  id_mascota  BIGINT        NOT NULL,
                                  descripcion VARCHAR(500)  NOT NULL,
                                  fecha       DATE          NOT NULL
) ENGINE=InnoDB;