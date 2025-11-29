-- ==========================================================
-- SCRIPT COMPLETO (V4) - ESTRUTURA NOVA + DADOS MIGRADOS
-- ==========================================================

USE master;
GO

IF DB_ID('interdb') IS NULL
BEGIN
    CREATE DATABASE interdb;
END
GO

USE interdb;
GO

-- 1. LIMPEZA (Ordem correta para evitar erros de FK)
IF OBJECT_ID('dbo.orcamento_produto', 'U') IS NOT NULL DROP TABLE dbo.orcamento_produto;
IF OBJECT_ID('dbo.pedido_produto', 'U') IS NOT NULL DROP TABLE dbo.pedido_produto;
IF OBJECT_ID('dbo.pedidos', 'U') IS NOT NULL DROP TABLE dbo.pedidos;
IF OBJECT_ID('dbo.orcamentos', 'U') IS NOT NULL DROP TABLE dbo.orcamentos;
IF OBJECT_ID('dbo.produtos', 'U') IS NOT NULL DROP TABLE dbo.produtos;
IF OBJECT_ID('dbo.clientes', 'U') IS NOT NULL DROP TABLE dbo.clientes;
IF OBJECT_ID('dbo.usuarios', 'U') IS NOT NULL DROP TABLE dbo.usuarios;
IF OBJECT_ID('dbo.pessoas', 'U') IS NOT NULL DROP TABLE dbo.pessoas;
GO

-- 2. CRIAÇÃO DAS TABELAS (Estrutura V3)

CREATE TABLE pessoas (
    id_pessoa INT IDENTITY(1,1) PRIMARY KEY,
    nome VARCHAR(255) NOT NULL, -- Aumentei para 255 para garantir
    data_nascimento DATE NOT NULL
);

CREATE TABLE usuarios (
    id_pessoa INT PRIMARY KEY,
    login VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    CONSTRAINT FK_Usuario_Pessoa FOREIGN KEY (id_pessoa) REFERENCES pessoas(id_pessoa)
);

CREATE TABLE clientes (
    id_pessoa INT PRIMARY KEY,
    endereco VARCHAR(500) NOT NULL,
    telefone VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    data_cadastro DATE NOT NULL,
    usuario_id INT, 
    CONSTRAINT FK_Cliente_Pessoa FOREIGN KEY (id_pessoa) REFERENCES pessoas(id_pessoa),
    CONSTRAINT FK_Cliente_Usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id_pessoa)
);

CREATE TABLE produtos (
    id_produto INT IDENTITY(1,1) PRIMARY KEY,
    descricao VARCHAR(500) NOT NULL,
    valor FLOAT NOT NULL,
    qtd INT NOT NULL
);

CREATE TABLE orcamentos (
    id_orcamento INT IDENTITY(1,1) PRIMARY KEY,
    descricao VARCHAR(1000) NOT NULL,
    data_cancel DATE,
    data_orcamento DATE NOT NULL,
    status BIT NOT NULL DEFAULT 0,
    cliente_id INT,
    usuario_id INT,
    CONSTRAINT FK_Orcamento_Cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id_pessoa),
    CONSTRAINT FK_Orcamento_Usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id_pessoa)
);

CREATE TABLE pedidos (
    id_pedido INT IDENTITY(1,1) PRIMARY KEY,
    descricao VARCHAR(1000) NOT NULL,
    data_cancel DATE,
    data_pedido DATE NOT NULL,
    id_cliente INT,
    id_usuario INT,
    id_orcamento INT UNIQUE,
    CONSTRAINT FK_Pedido_Cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id_pessoa),
    CONSTRAINT FK_Pedido_Usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_pessoa),
    CONSTRAINT FK_Pedido_Orcamento FOREIGN KEY (id_orcamento) REFERENCES orcamentos(id_orcamento)
);

CREATE TABLE orcamento_produto (
    orcamento_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT DEFAULT 1,
    valor_unitario FLOAT, 
    PRIMARY KEY (orcamento_id, produto_id),
    FOREIGN KEY (orcamento_id) REFERENCES orcamentos(id_orcamento) ON DELETE CASCADE,
    FOREIGN KEY (produto_id) REFERENCES produtos(id_produto) ON DELETE CASCADE
);

CREATE TABLE pedido_produto (
    pedido_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT DEFAULT 1,
    valor_unitario FLOAT,
    PRIMARY KEY (pedido_id, produto_id),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
    FOREIGN KEY (produto_id) REFERENCES produtos(id_produto) ON DELETE CASCADE
);
GO

-- 3. INSERÇÃO DE DADOS MIGRADOS

-- =================================================
-- A. USUÁRIOS (IDs 1 a 14)
-- =================================================
SET IDENTITY_INSERT pessoas ON;
INSERT INTO pessoas (id_pessoa, nome, data_nascimento) VALUES
(1, 'Ana Silva', '1990-05-15'),
(2, 'Bruno Costa', '1988-11-30'),
(3, 'Carla Dias', '1995-02-10'),
(4, 'Daniel Moreira', '1982-09-25'),
(5, 'Eduarda Lima', '2000-07-07'),
(6, 'Fábio Pereira', '1993-01-12'),
(7, 'Gabriela Matos', '1999-03-05'),
(8, 'Heitor Campos', '1985-12-19'),
(9, 'Isabela Barros', '1997-08-22'),
(10, 'João Souza', '1991-04-17'),
(11, 'Victor Leal', '1991-04-17'),
(12, 'Matheus Liebana Souza', '1991-04-17'),
(13, 'Sophia Pellizon', '1991-04-17'),
(14, 'Theo Teodoro', '1991-04-17');

-- NOVOS DADOS ADICIONADOS (15 USUÁRIOS - IDs 25 a 39)
-- Pulo os IDs 15-24 pois estão reservados para Clientes no script original
INSERT INTO pessoas (id_pessoa, nome, data_nascimento) VALUES
(25, 'Lucas Mendes', '1992-06-15'),
(26, 'Fernanda Rocha', '1989-12-01'),
(27, 'Rafael Lima', '1994-03-22'),
(28, 'Beatriz Alves', '1998-09-09'),
(29, 'Sergio Moro', '1980-05-30'),
(30, 'Vanessa Camargo', '1996-07-14'),
(31, 'Igor Cavalera', '1985-02-28'),
(32, 'Camila Pitanga', '1990-11-11'),
(33, 'Renan Oliveira', '1993-08-18'),
(34, 'Leticia Spiller', '1987-01-25'),
(35, 'Gustavo Lima', '1999-04-05'),
(36, 'Monica Iozzi', '1991-10-30'),
(37, 'Felipe Neto', '1988-06-06'),
(38, 'Aline Barros', '1995-12-24'),
(39, 'Thiago Ventura', '1986-09-15');
SET IDENTITY_INSERT pessoas OFF;

INSERT INTO usuarios (id_pessoa, login, senha) VALUES
(1, 'ana.silva', 'senha123'),
(2, 'bruno.costa', 'senha123'),
(3, 'carla.dias', 'senha123'),
(4, 'daniel.moreira', 'senha123'),
(5, 'eduarda.lima', 'senha123'),
(6, 'fabio.pereira', 'senha123'),
(7, 'gabriela.matos', 'senha123'),
(8, 'heitor.campos', 'senha123'),
(9, 'isabela.barros', 'senha123'),
(10, 'joao.souza', 'senha123'),
(11, 'victor.inter', 'admin123'),
(12, 'matheus.inter', 'admin123'),
(13, 'sophia.inter', 'admin123'),
(14, 'theo.inter', 'admin123');

-- NOVOS DADOS ADICIONADOS (15 USUÁRIOS)
INSERT INTO usuarios (id_pessoa, login, senha) VALUES
(25, 'lucas.mendes', 'user25'),
(26, 'fernanda.rocha', 'user26'),
(27, 'rafael.lima', 'user27'),
(28, 'beatriz.alves', 'user28'),
(29, 'sergio.moro', 'user29'),
(30, 'vanessa.camargo', 'user30'),
(31, 'igor.cavalera', 'user31'),
(32, 'camila.pitanga', 'user32'),
(33, 'renan.oliveira', 'user33'),
(34, 'leticia.spiller', 'user34'),
(35, 'gustavo.lima', 'user35'),
(36, 'monica.iozzi', 'user36'),
(37, 'felipe.neto', 'user37'),
(38, 'aline.barros', 'user38'),
(39, 'thiago.ventura', 'user39');
PRINT 'Usuários inseridos (IDs 1-14 e 25-39).';

-- =================================================
-- B. CLIENTES (IDs 15 a 24)
-- IDs remapeados: Antigo ID 1 -> Novo ID 15 (+14)
-- =================================================
SET IDENTITY_INSERT pessoas ON;
INSERT INTO pessoas (id_pessoa, nome, data_nascimento) VALUES
(15, 'Empresa Alpha Ltda', '2010-01-15'),
(16, 'Comércio Beta S/A', '2005-06-20'),
(17, 'Design Gama', '2018-03-10'),
(18, 'Restaurante Delta', '2012-11-05'),
(19, 'Escritório Épsilon', '2015-08-01'),
(20, 'Academia Zeta', '2020-02-28'),
(21, 'Consultoria Ômega', '2000-07-19'),
(22, 'Padaria Teta', '2017-09-12'),
(23, 'Loja Sigma', '2019-12-01'),
(24, 'Gráfica Pi', '2014-04-30');

-- NOVOS DADOS ADICIONADOS (15 CLIENTES - IDs 40 a 54)
INSERT INTO pessoas (id_pessoa, nome, data_nascimento) VALUES
(40, 'Clinica Sorriso', '2016-01-10'),
(41, 'Auto Peças Veloz', '2011-05-15'),
(42, 'Hotel Estrela do Sul', '2008-11-20'),
(43, 'Buffet Sabor Divino', '2019-02-28'),
(44, 'Escola Futuro Brilhante', '2013-07-07'),
(45, 'Pet Shop Bicho Feliz', '2021-04-12'),
(46, 'Escritório Juridico Silva', '2009-09-09'),
(47, 'Salão Beleza Pura', '2018-12-01'),
(48, 'Tech Inovação', '2022-03-15'),
(49, 'Bar do Zé', '2000-10-10'),
(50, 'Condominio Solar', '2014-06-06'),
(51, 'Igreja Vida Nova', '1995-08-25'),
(52, 'ONG Esperança', '2017-01-30'),
(53, 'Supermercado Extra Bom', '2005-11-11'),
(54, 'Academia Power Fit', '2020-05-05');
SET IDENTITY_INSERT pessoas OFF;

INSERT INTO clientes (id_pessoa, endereco, telefone, email, data_cadastro, usuario_id) VALUES
(15, 'Rua das Flores, 123', '(11) 98765-4321', 'contato@alpha.com', '2025-01-10', 1),
(16, 'Av. Principal, 456', '(21) 91234-5678', 'compras@beta.com', '2025-01-12', 2),
(17, 'Praça da Matriz, 789', '(31) 99999-8888', 'arte@gama.com', '2025-01-15', 3),
(18, 'Rua do Sabor, 10', '(41) 98888-7777', 'adm@delta.com', '2025-01-20', 4),
(19, 'Av. Central, 321', '(51) 97777-6666', 'financeiro@epsilon.com', '2025-02-01', 5),
(20, 'Rua da Saúde, 654', '(61) 96666-5555', 'contato@zeta.com', '2025-02-05', 1),
(21, 'Alameda dos Anjos, 987', '(71) 95555-4444', 'diretoria@omega.com', '2025-02-10', 2),
(22, 'Rua do Pão, 741', '(81) 94444-3333', 'pedidos@teta.com', '2025-02-15', 3),
(23, 'Travessa do Comércio, 852', '(91) 93333-2222', 'vendas@sigma.com', '2025-02-20', 4),
(24, 'Rua da Impressão, 963', '(19) 92222-1111', 'grafica@pi.com', '2025-03-01', 5);

-- NOVOS DADOS ADICIONADOS (15 CLIENTES)
INSERT INTO clientes (id_pessoa, endereco, telefone, email, data_cadastro, usuario_id) VALUES
(40, 'Av. Brasil, 100', '(11) 91111-1111', 'contato@sorriso.com', '2025-03-02', 25),
(41, 'Rua das Peças, 50', '(21) 92222-2222', 'vendas@veloz.com', '2025-03-03', 26),
(42, 'Estrada do Mar, 900', '(31) 93333-3333', 'reservas@estrela.com', '2025-03-04', 27),
(43, 'Rua dos Doces, 44', '(41) 94444-4444', 'festa@sabor.com', '2025-03-05', 28),
(44, 'Av. Educação, 202', '(51) 95555-5555', 'secretaria@futuro.com', '2025-03-06', 29),
(45, 'Rua dos Gatos, 77', '(61) 96666-6666', 'pet@bicho.com', '2025-03-07', 30),
(46, 'Praça da Justiça, 1', '(71) 97777-7777', 'adv@silva.com', '2025-03-08', 31),
(47, 'Rua da Moda, 88', '(81) 98888-8888', 'beleza@pura.com', '2025-03-09', 32),
(48, 'Av. Digital, 1010', '(91) 99999-9999', 'dev@tech.com', '2025-03-10', 33),
(49, 'Rua do Happy Hour, 12', '(19) 91010-1010', 'ze@bar.com', '2025-03-11', 34),
(50, 'Av. dos Moradores, 500', '(22) 92020-2020', 'adm@solar.com', '2025-03-12', 35),
(51, 'Rua da Fé, 33', '(32) 93030-3030', 'contato@vida.com', '2025-03-13', 36),
(52, 'Rua Solidária, 45', '(42) 94040-4040', 'ajuda@esperanca.com', '2025-03-14', 37),
(53, 'Av. das Compras, 2000', '(52) 95050-5050', 'sac@extra.com', '2025-03-15', 38),
(54, 'Rua do Fitness, 99', '(62) 96060-6060', 'treino@power.com', '2025-03-16', 39);
PRINT 'Clientes inseridos (IDs 15-24 e 40-54).';

-- =================================================
-- C. PRODUTOS
-- =================================================
SET IDENTITY_INSERT produtos ON;
INSERT INTO produtos (id_produto, descricao, valor, qtd) VALUES
(1, 'Impressão de Banner 1x1m Lona Fosca', 75.50, 100),
(2, 'Milheiro Cartão de Visita Couchê 300g', 120.00, 50),
(3, 'Bloco de Notas A5 (100 folhas)', 15.00, 200),
(4, 'Adesivo Vinil Recorte (m²)', 45.00, 500),
(5, 'Folder A4 Couchê 150g (1000 un)', 450.00, 30),
(6, 'Crachá PVC com Cordão', 12.50, 1000),
(7, 'Criação de Logotipo (Serviço)', 800.00, 10),
(8, 'Consultoria de Design (Hora)', 150.00, 50),
(9, 'Flyer A5 Couchê 115g (2500 un)', 300.00, 40),
(10, 'Placa de PVC 3mm (m²)', 90.00, 80);

-- NOVOS DADOS ADICIONADOS (15 PRODUTOS - IDs 11 a 25)
INSERT INTO produtos (id_produto, descricao, valor, qtd) VALUES
(11, 'Caneca de Cerâmica Personalizada', 35.00, 150),
(12, 'Camiseta Poliéster Sublimação', 25.00, 200),
(13, 'Agenda Diária 2025 Personalizada', 45.00, 80),
(14, 'Calendário de Mesa Triangular', 10.00, 300),
(15, 'Mousepad Ergonômico com Logo', 20.00, 100),
(16, 'Caneta Plástica Personalizada (un)', 3.50, 2000),
(17, 'Sacola Kraft G Personalizada', 5.00, 500),
(18, 'Windbanner Completo 2.5m', 250.00, 20),
(19, 'Adesivo Perfurado para Vidro (m²)', 60.00, 100),
(20, 'Lona Backlight para Luminosos (m²)', 85.00, 50),
(21, 'Letra Caixa XPS 5cm (un)', 15.00, 500),
(22, 'Totem Promocional Papelão', 120.00, 30),
(23, 'Display de Mesa em Acrílico A4', 35.00, 60),
(24, 'Cardápio Plastificado A3', 18.00, 150),
(25, 'Certificado Papel Especial (un)', 8.00, 400);
SET IDENTITY_INSERT produtos OFF;
PRINT 'Produtos inseridos.';

-- =================================================
-- D. ORÇAMENTOS
-- IDs de Clientes foram ajustados (Original + 14)
-- =================================================
SET IDENTITY_INSERT orcamentos ON;
INSERT INTO orcamentos (id_orcamento, descricao, data_cancel, data_orcamento, status, cliente_id, usuario_id) VALUES
(1, 'Orçamento para Banners e Adesivos', NULL, '2025-02-01', 0, 15, 1),
(2, 'Cartões de Visita Urgentes', NULL, '2025-02-03', 1, 16, 2),
(3, 'Material de Escritório (Blocos)', NULL, '2025-02-05', 0, 17, 3),
(4, 'Sinalização Interna Loja', NULL, '2025-02-10', 0, 23, 4),
(5, 'Renovação da Marca (Logo)', NULL, '2025-02-12', 1, 21, 5),
(6, 'Folders para Evento', NULL, '2025-02-15', 0, 19, 1),
(7, 'Crachás para funcionários', '2025-02-20', '2025-02-18', 0, 20, 2),
(8, 'Flyers promocionais Páscoa', NULL, '2025-02-22', 1, 22, 3),
(9, 'Placa para fachada', NULL, '2025-02-25', 0, 18, 4),
(10, 'Material Gráfico Completo', NULL, '2025-03-01', 0, 24, 5);

-- NOVOS DADOS ADICIONADOS (15 ORÇAMENTOS - IDs 11 a 25)
INSERT INTO orcamentos (id_orcamento, descricao, data_cancel, data_orcamento, status, cliente_id, usuario_id) VALUES
(11, 'Orçamento Brindes Corporativos', NULL, '2025-03-10', 0, 40, 25),
(12, 'Uniformes da Equipe', NULL, '2025-03-11', 1, 41, 26),
(13, 'Agendas 2025 para Clientes', NULL, '2025-03-12', 0, 42, 27),
(14, 'Calendários Promocionais', NULL, '2025-03-13', 0, 43, 28),
(15, 'Kit Boas Vindas (Mousepad/Caneta)', NULL, '2025-03-14', 1, 44, 29),
(16, 'Sacolas para Delivery', NULL, '2025-03-15', 0, 45, 30),
(17, 'Sinalização Externa Windbanner', '2025-03-20', '2025-03-16', 0, 46, 31),
(18, 'Adesivagem de Vitrine', NULL, '2025-03-17', 1, 47, 32),
(19, 'Troca de Lona Luminoso', NULL, '2025-03-18', 0, 48, 33),
(20, 'Letreiro da Recepção', NULL, '2025-03-19', 0, 49, 34),
(21, 'Totens para Entrada', NULL, '2025-03-20', 1, 50, 35),
(22, 'Displays de Mesa Restaurante', NULL, '2025-03-21', 0, 51, 36),
(23, 'Novos Cardápios', NULL, '2025-03-22', 0, 52, 37),
(24, 'Impressão Certificados Curso', NULL, '2025-03-23', 1, 53, 38),
(25, 'Material de Divulgação Geral', NULL, '2025-03-24', 0, 54, 39);
SET IDENTITY_INSERT orcamentos OFF;
PRINT 'Orçamentos inseridos.';

-- =================================================
-- E. PEDIDOS
-- IDs de Clientes foram ajustados (Original + 14)
-- =================================================
SET IDENTITY_INSERT pedidos ON;
INSERT INTO pedidos (id_pedido, descricao, data_cancel, data_pedido, id_cliente, id_usuario, id_orcamento) VALUES
(1, 'Pedido Cartões de Visita (Aprovado)', NULL, '2025-02-04', 16, 2, 2),
(2, 'Pedido Criação de Logo (Aprovado)', NULL, '2025-02-13', 21, 5, 5),
(3, 'Pedido Flyers Páscoa (Aprovado)', NULL, '2025-02-23', 22, 3, 8),
(4, 'Pedido direto - Blocos de Nota', NULL, '2025-03-02', 15, 1, NULL),
(5, 'Pedido direto - Crachás', NULL, '2025-03-05', 20, 2, NULL),
(6, 'Ajuste Sinalização (Pedido Urgente)', NULL, '2025-03-10', 23, 4, NULL),
(7, 'Pedido Banners Evento', NULL, '2025-03-11', 15, 1, 1),
(8, 'Pedido Placa Fachada (Pendente)', NULL, '2025-03-12', 18, 4, 9),
(9, 'Consultoria de Design (Fechado)', NULL, '2025-03-15', 19, 5, NULL),
(10, 'Impressão Material Gráfico', '2025-03-20', '2025-03-18', 24, 3, 10);

-- NOVOS DADOS ADICIONADOS (15 PEDIDOS - IDs 11 a 25)
INSERT INTO pedidos (id_pedido, descricao, data_cancel, data_pedido, id_cliente, id_usuario, id_orcamento) VALUES
(11, 'Pedido Uniformes Aprovado', NULL, '2025-03-12', 41, 26, 12),
(12, 'Pedido Kit Boas Vindas', NULL, '2025-03-15', 44, 29, 15),
(13, 'Pedido Vitrine Aprovado', NULL, '2025-03-18', 47, 32, 18),
(14, 'Pedido Totens Entrada', NULL, '2025-03-21', 50, 35, 21),
(15, 'Pedido Certificados', NULL, '2025-03-24', 53, 38, 24),
(16, 'Pedido direto - Canecas Extras', NULL, '2025-03-25', 40, 25, NULL),
(17, 'Pedido direto - Mais Agendas', NULL, '2025-03-26', 42, 27, NULL),
(18, 'Pedido direto - Reposição Calendários', NULL, '2025-03-27', 43, 28, NULL),
(19, 'Pedido direto - Sacolas Emergência', NULL, '2025-03-28', 45, 30, NULL),
(20, 'Pedido direto - Windbanner Promo', NULL, '2025-03-29', 46, 31, NULL),
(21, 'Pedido direto - Lona Luminoso', NULL, '2025-03-30', 48, 33, NULL),
(22, 'Pedido direto - Letreiro', NULL, '2025-03-31', 49, 34, NULL),
(23, 'Pedido direto - Displays', NULL, '2025-04-01', 51, 36, NULL),
(24, 'Pedido direto - Cardápios', NULL, '2025-04-02', 52, 37, NULL),
(25, 'Pedido direto - Divulgação', NULL, '2025-04-03', 54, 39, NULL);
SET IDENTITY_INSERT pedidos OFF;
PRINT 'Pedidos inseridos.';

-- =================================================
-- F. VINCULAR PRODUTOS (JUNÇÃO N:M)
-- Convertendo a lógica antiga de UPDATE para INSERT nas novas tabelas
-- =================================================

-- Vinculando produtos a ORÇAMENTOS
INSERT INTO orcamento_produto (orcamento_id, produto_id, qtd, valor_unitario) VALUES
(3, 3, 1, 15.00),  -- Bloco de Notas (Orçamento 3)
(4, 4, 1, 45.00),  -- Adesivo Vinil (Orçamento 4)
(6, 5, 1, 450.00); -- Folder A4 (Orçamento 6)

-- NOVOS DADOS ADICIONADOS (15 VÍNCULOS ORÇAMENTO)
INSERT INTO orcamento_produto (orcamento_id, produto_id, qtd, valor_unitario) VALUES
(11, 11, 50, 35.00), -- Canecas no Orc 11
(12, 12, 20, 25.00), -- Camisetas no Orc 12
(13, 13, 100, 45.00), -- Agendas no Orc 13
(14, 14, 200, 10.00), -- Calendarios no Orc 14
(15, 15, 30, 20.00), -- Mousepads no Orc 15
(16, 17, 500, 5.00), -- Sacolas no Orc 16
(17, 18, 2, 250.00), -- Windbanner no Orc 17
(18, 19, 10, 60.00), -- Adesivo Vidro no Orc 18
(19, 20, 5, 85.00), -- Lona Backlight no Orc 19
(20, 21, 50, 15.00), -- Letra Caixa no Orc 20
(21, 22, 4, 120.00), -- Totem no Orc 21
(22, 23, 15, 35.00), -- Display no Orc 22
(23, 24, 30, 18.00), -- Cardapio no Orc 23
(24, 25, 100, 8.00), -- Certificado no Orc 24
(25, 9, 1000, 300.00); -- Flyer (prod antigo) no Orc 25

-- Vinculando produtos a PEDIDOS
INSERT INTO pedido_produto (pedido_id, produto_id, qtd, valor_unitario) VALUES
(1, 2, 1, 120.00), -- Cartão de Visita (Pedido 1)
(2, 7, 1, 800.00), -- Logo (Pedido 2)
(3, 9, 1, 300.00), -- Flyer (Pedido 3)
(5, 6, 1, 12.50),  -- Crachá (Pedido 5)
(7, 1, 1, 75.50),  -- Banner (Pedido 7)
(8, 10, 1, 90.00), -- Placa PVC (Pedido 8)
(9, 8, 1, 150.00); -- Consultoria (Pedido 9)

-- NOVOS DADOS ADICIONADOS (15 VÍNCULOS PEDIDOS)
INSERT INTO pedido_produto (pedido_id, produto_id, qtd, valor_unitario) VALUES
(11, 12, 20, 25.00), -- Camisetas no Ped 11
(12, 15, 30, 20.00), -- Mousepads no Ped 12
(13, 19, 10, 60.00), -- Adesivo no Ped 13
(14, 22, 4, 120.00), -- Totem no Ped 14
(15, 25, 100, 8.00), -- Certificado no Ped 15
(16, 11, 10, 35.00), -- Canecas no Ped 16
(17, 13, 50, 45.00), -- Agendas no Ped 17
(18, 14, 50, 10.00), -- Calendarios no Ped 18
(19, 17, 200, 5.00), -- Sacolas no Ped 19
(20, 18, 1, 250.00), -- Windbanner no Ped 20
(21, 20, 2, 85.00), -- Lona no Ped 21
(22, 21, 20, 15.00), -- Letras no Ped 22
(23, 23, 5, 35.00), -- Displays no Ped 23
(24, 24, 10, 18.00), -- Cardapios no Ped 24
(25, 9, 500, 300.00); -- Flyer no Ped 25

PRINT 'Banco de dados recriado e populado com sucesso!';
GO