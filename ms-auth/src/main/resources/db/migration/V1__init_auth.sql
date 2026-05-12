CREATE TABLE usuarios (
                          id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                          username   VARCHAR(100)  NOT NULL UNIQUE,
                          password   VARCHAR(255)  NOT NULL,
                          rol        ENUM('ADMIN','VETERINARIO','DUENO') NOT NULL,
                          activo     BOOLEAN       DEFAULT TRUE,
                          created_at DATETIME      DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO usuarios (username, password, rol)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');