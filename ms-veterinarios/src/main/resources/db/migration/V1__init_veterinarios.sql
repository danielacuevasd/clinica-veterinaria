CREATE TABLE veterinarios (
                              id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                              nombre       VARCHAR(100) NOT NULL,
                              apellido     VARCHAR(100) NOT NULL,
                              especialidad VARCHAR(100) NOT NULL,
                              email        VARCHAR(150) NOT NULL UNIQUE,
                              telefono     VARCHAR(20),
                              disponible   BOOLEAN      DEFAULT TRUE,
                              activo       BOOLEAN      DEFAULT TRUE,
                              created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE horarios (
                          id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                          id_veterinario  BIGINT      NOT NULL,
                          dia_semana      VARCHAR(20) NOT NULL,
                          hora_inicio     TIME        NOT NULL,
                          hora_fin        TIME        NOT NULL
) ENGINE=InnoDB;