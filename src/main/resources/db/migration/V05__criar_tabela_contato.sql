CREATE TABLE contato (
  id        BIGSERIAL PRIMARY KEY,
  pessoa_id BIGINT       NOT NULL,
  nome      VARCHAR(50)  NOT NULL,
  email     VARCHAR(100) NOT NULL,
  telefone  VARCHAR(20)  NOT NULL,
  FOREIGN KEY (pessoa_id) REFERENCES pessoa (id)
);

INSERT INTO contato (id, pessoa_id, nome, email, telefone)
VALUES (1, 1, 'Marcos Henrique', 'marcos@algamoney.com', '00 0000-0000');
