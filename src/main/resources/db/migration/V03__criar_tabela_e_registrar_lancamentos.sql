CREATE TABLE lancamento (
  id              BIGSERIAL PRIMARY KEY,
  descricao       VARCHAR(50)    NOT NULL,
  data_vencimento DATE           NOT NULL,
  data_pagamento  DATE,
  valor           DECIMAL(10, 2) NOT NULL,
  observacao      VARCHAR(100),
  tipo            VARCHAR(20)    NOT NULL,
  categoria_id    BIGINT         NOT NULL,
  pessoa_id       BIGINT         NOT NULL,
  FOREIGN KEY (categoria_id) REFERENCES categoria (id),
  FOREIGN KEY (pessoa_id) REFERENCES pessoa (id)
);

INSERT INTO lancamento (descricao, data_vencimento, data_pagamento, valor, observacao, tipo, categoria_id, pessoa_id)
VALUES
  ('Salário mensal', '2017-06-10', NULL, 6500.00, 'Distribuição de lucros', 'RECEITA', 1, 1),
  ('Bahamas', '2017-02-10', '2017-02-10', 100.32, NULL, 'DESPESA', 2, 2),
  ('Top Club', '2017-06-10', NULL, 120, NULL, 'RECEITA', 3, 3),
  ('CEMIG', '2017-02-10', '2017-02-10', 110.44, 'Geração', 'RECEITA', 3, 4),
  ('DMAE', '2017-06-10', NULL, 200.30, NULL, 'DESPESA', 3, 5),
  ('Extra', '2017-03-10', '2017-03-10', 1010.32, NULL, 'RECEITA', 4, 6),
  ('Bahamas', '2017-06-10', NULL, 500, NULL, 'RECEITA', 1, 7),
  ('Top Club', '2017-03-10', '2017-03-10', 400.32, NULL, 'DESPESA', 4, 8),
  ('Despachante', '2017-06-10', NULL, 123.64, 'Multas', 'DESPESA', 3, 9),
  ('Pneus', '2017-04-10', '2017-04-10', 665.33, NULL, 'RECEITA', 5, 10),
  ('Café', '2017-06-10', NULL, 8.32, NULL, 'DESPESA', 1, 5),
  ('Eletrônicos', '2017-04-10', '2017-04-10', 2100.32, NULL, 'DESPESA', 5, 4),
  ('Instrumentos', '2017-06-10', NULL, 1040.32, NULL, 'DESPESA', 4, 3),
  ('Café', '2017-04-10', '2017-04-10', 4.32, NULL, 'DESPESA', 4, 2),
  ('Lanche', '2017-06-10', NULL, 10.20, NULL, 'DESPESA', 4, 1);