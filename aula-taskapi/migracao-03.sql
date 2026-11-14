USE taskdb;

CREATE TABLE usuarios (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    nome       VARCHAR(120) NOT NULL,
    email      VARCHAR(160) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    CONSTRAINT uk_usuarios_email UNIQUE (email)
);


INSERT INTO usuarios (nome, email, senha) VALUES
('Isabella Lima',   'isabella@taskapi.test',   'isa123'),
('Douglas Wallace',  'douglas@taskapi.test', 'douglas123');
ALTER TABLE tarefas ADD COLUMN usuario_id INT NULL;

UPDATE tarefas
   SET usuario_id = (SELECT id FROM usuarios WHERE email = 'isabella@taskapi.test')
 WHERE usuario_id IS NULL;

ALTER TABLE tarefas
    MODIFY concluida BOOLEAN NOT NULL DEFAULT FALSE,
    MODIFY usuario_id INT NOT NULL,
    ADD CONSTRAINT fk_tarefas_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios (id);

INSERT INTO tarefas (titulo, descricao, concluida, usuario_id)
SELECT 'Colher plantas', 'limpar o jardim', FALSE, id
  FROM usuarios WHERE email = 'isabella@taskapi.test';

INSERT INTO tarefas (titulo, descricao, concluida, usuario_id)
SELECT t.titulo, t.descricao, t.concluida, u.id
  FROM usuarios u
  JOIN (SELECT 'Comprar desodorante' AS titulo, 'repor estoque' AS descricao, TRUE AS concluida
        UNION ALL SELECT 'Revisar conteudo prova', 'prova de Redes', FALSE
        UNION ALL SELECT 'Entregar relatorio', 'tarefa Mario', FALSE) t
 WHERE u.email = 'douglas@taskapi.test';
