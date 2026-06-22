CREATE TABLE facturas (
                          id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                          id_consulta   BIGINT        NOT NULL,
                          id_mascota    BIGINT        NOT NULL,
                          id_dueno      BIGINT        NOT NULL,
                          total         DECIMAL(10,2) NOT NULL DEFAULT 0,
                          estado        ENUM('PENDIENTE','PAGADA','ANULADA') DEFAULT 'PENDIENTE',
                          observaciones TEXT,
                          created_at    DATETIME      DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;