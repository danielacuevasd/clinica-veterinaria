CREATE TABLE tratamientos (
                              id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                              id_consulta  BIGINT        NOT NULL,
                              id_mascota   BIGINT        NOT NULL,
                              medicamento  VARCHAR(200)  NOT NULL,
                              dosis        VARCHAR(100)  NOT NULL,
                              frecuencia   VARCHAR(100)  NOT NULL,
                              duracion_dias INT          NOT NULL,
                              indicaciones TEXT,
                              created_at   DATETIME      DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;