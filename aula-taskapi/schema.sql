CREATE DATABASE IF NOT EXISTS taskdb;
USE taskdb;

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    CONSTRAINT uk_usuarios_email UNIQUE (email)
);


CREATE TABLE IF NOT EXISTS tarefas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(120) NOT NULL,
    descricao VARCHAR(500),
    concluida BOOLEAN DEFAULT FALSE,
    usuario_id INT NOT NULL,
    CONSTRAINT fk_tarefas_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

INSERT INTO usuarios (id, nome, email, senha) VALUES
(1, 'Isabella Lima',  'isabella@taskapi.test',   'isa123'),
(2, 'Douglas Wallace', 'douglas@taskapi.test', 'douglas123');



-- Alguns registros para testar o GET
INSERT INTO tarefas (titulo, descricao, concluida) VALUES
('Estudar filtros','Ler o material da aula', FALSE, 1),
('Configurar o banco', 'MySQL + driver', TRUE, 1),
('Colher plantas', 'limpar o jardim', FALSE, 1),
('Comprar desodorante','repor estoque', TRUE, 2),
('Revisar conteudo prova','prova de Redes',   FALSE, 2),
('Entregar relatorio', 'tarefa Mario', FALSE, 2);