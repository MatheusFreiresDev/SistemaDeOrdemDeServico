CREATE TABLE ordens_servico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data_criacao DATETIME NOT NULL DEFAULT NOW(),
    status VARCHAR(200) NOT NULL,
    prioridade VARCHAR(200) NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    categoria VARCHAR(200) NOT NULL,
    criador_id INT NOT NULL,
    executor_id INT DEFAULT NULL,
    FOREIGN KEY (criador_id) REFERENCES usuarios(id),
    FOREIGN KEY (executor_id) REFERENCES usuarios(id)
);