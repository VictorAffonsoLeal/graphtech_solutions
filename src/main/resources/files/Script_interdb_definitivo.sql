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
    status INT NOT NULL DEFAULT 0
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
PRINT 'Usuários inseridos (IDs 1-14).';

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
PRINT 'Clientes inseridos (IDs 15-24).';

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

-- Vinculando produtos a PEDIDOS
INSERT INTO pedido_produto (pedido_id, produto_id, qtd, valor_unitario) VALUES
(1, 2, 1, 120.00), -- Cartão de Visita (Pedido 1)
(2, 7, 1, 800.00), -- Logo (Pedido 2)
(3, 9, 1, 300.00), -- Flyer (Pedido 3)
(5, 6, 1, 12.50),  -- Crachá (Pedido 5)
(7, 1, 1, 75.50),  -- Banner (Pedido 7)
(8, 10, 1, 90.00), -- Placa PVC (Pedido 8)
(9, 8, 1, 150.00); -- Consultoria (Pedido 9)

PRINT 'Banco de dados recriado e populado com sucesso!';
GO