-- Verifique se o banco de dados já existe antes de tentar criá-lo
IF DB_ID('interdb') IS NULL
BEGIN
    CREATE DATABASE interdb;
END
GO

USE interdb;
GO

-- ---------------------------------
-- CRIAÇÃO DAS TABELAS (SE NÃO EXISTIREM)
-- (Adicionando a criação para garantir que o script seja completo)
-- ---------------------------------

IF OBJECT_ID('dbo.usuarios', 'U') IS NULL
BEGIN
    CREATE TABLE usuarios (
        id_usuario INT IDENTITY(1,1) PRIMARY KEY,
        nome VARCHAR(255) NOT NULL,
        login VARCHAR(100) NOT NULL UNIQUE,
        senha VARCHAR(100) NOT NULL,
        data_nascimento DATE
    );
    PRINT 'Tabela [usuarios] criada.';
END

IF OBJECT_ID('dbo.clientes', 'U') IS NULL
BEGIN
    CREATE TABLE clientes (
        id_cliente INT IDENTITY(1,1) PRIMARY KEY,
        nome VARCHAR(255) NOT NULL,
        data_nascimento DATE,
        endereco VARCHAR(500),
        telefone VARCHAR(20),
        email VARCHAR(100) UNIQUE,
        data_cadastro DATE NOT NULL,
        usuario_id INT,
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id_usuario)
    );
    PRINT 'Tabela [clientes] criada.';
END

IF OBJECT_ID('dbo.produtos', 'U') IS NULL
BEGIN
    -- CORREÇÃO: Removidas as colunas pedido_id e orcamento_id
    CREATE TABLE produtos (
        id_produto INT IDENTITY(1,1) PRIMARY KEY,
        descricao VARCHAR(500) NOT NULL,
        valor FLOAT NOT NULL,
        qtd INT NOT NULL
    );
    PRINT 'Tabela [produtos] criada (Estrutura N:M).';
END

IF OBJECT_ID('dbo.orcamentos', 'U') IS NULL
BEGIN
    CREATE TABLE orcamentos (
        id_orcamento INT IDENTITY(1,1) PRIMARY KEY,
        descricao VARCHAR(1000) NOT NULL,
        data_cancel DATE,
        data_orcamento DATE NOT NULL,
        status BIT NOT NULL DEFAULT 0,
        cliente_id INT,
        usuario_id INT,
        -- Removida a referência direta ao pedido, se houver
        -- pedido_id_pedido INT, 
        FOREIGN KEY (cliente_id) REFERENCES clientes(id_cliente),
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id_usuario)
    );
    PRINT 'Tabela [orcamentos] criada.';
END

IF OBJECT_ID('dbo.pedidos', 'U') IS NULL
BEGIN
    CREATE TABLE pedidos (
        id_pedido INT IDENTITY(1,1) PRIMARY KEY,
        descricao VARCHAR(1000) NOT NULL,
        data_cancel DATE,
        data_pedido DATE NOT NULL,
        id_cliente INT,
        id_usuario INT,
        id_orcamento INT UNIQUE, -- Relação 1:1 com Orçamento
        FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
        FOREIGN KEY (id_orcamento) REFERENCES orcamentos(id_orcamento)
    );
    PRINT 'Tabela [pedidos] criada.';
END

-- ---------------------------------
-- CRIAÇÃO DAS TABELAS DE JUNÇÃO (N:M)
-- ---------------------------------

IF OBJECT_ID('dbo.orcamento_produto', 'U') IS NULL
BEGIN
    CREATE TABLE orcamento_produto (
        orcamento_id INT NOT NULL,
        produto_id INT NOT NULL,
        PRIMARY KEY (orcamento_id, produto_id),
        FOREIGN KEY (orcamento_id) REFERENCES orcamentos(id_orcamento) ON DELETE CASCADE,
        FOREIGN KEY (produto_id) REFERENCES produtos(id_produto) ON DELETE CASCADE
    );
    PRINT 'Tabela de junção [orcamento_produto] criada.';
END

IF OBJECT_ID('dbo.pedido_produto', 'U') IS NULL
BEGIN
    CREATE TABLE pedido_produto (
        pedido_id INT NOT NULL,
        produto_id INT NOT NULL,
        PRIMARY KEY (pedido_id, produto_id),
        FOREIGN KEY (pedido_id) REFERENCES pedidos(id_pedido) ON DELETE CASCADE,
        FOREIGN KEY (produto_id) REFERENCES produtos(id_produto) ON DELETE CASCADE
    );
    PRINT 'Tabela de junção [pedido_produto] criada.';
END
GO

-- ---------------------------------
-- INSERÇÃO DE DADOS (Usando SET IDENTITY_INSERT)
-- ---------------------------------

-- 1. Inserção de Usuários
SET IDENTITY_INSERT usuarios ON;
IF NOT EXISTS (SELECT 1 FROM usuarios WHERE id_usuario = 1)
INSERT INTO usuarios (id_usuario, nome, login, senha, data_nascimento) VALUES
(1, 'Ana Silva', 'ana.silva', 'senha123', '1990-05-15'),
(2, 'Bruno Costa', 'bruno.costa', 'senha123', '1988-11-30'),
(3, 'Carla Dias', 'carla.dias', 'senha123', '1995-02-10'),
(4, 'Daniel Moreira', 'daniel.moreira', 'senha123', '1982-09-25'),
(5, 'Eduarda Lima', 'eduarda.lima', 'senha123', '2000-07-07'),
(6, 'Fábio Pereira', 'fabio.pereira', 'senha123', '1993-01-12'),
(7, 'Gabriela Matos', 'gabriela.matos', 'senha123', '1999-03-05'),
(8, 'Heitor Campos', 'heitor.campos', 'senha123', '1985-12-19'),
(9, 'Isabela Barros', 'isabela.barros', 'senha123', '1997-08-22'),
(10, 'João Souza', 'joao.souza', 'senha123', '1991-04-17'),
(11, 'Victor Leal', 'victor.inter', 'admin123', '1991-04-17'),
(12, 'Matheus Liebana Souza', 'matheus.inter', 'admin123', '1991-04-17'),
(13, 'Sophia Pellizon', 'sophia.inter', 'admin123', '1991-04-17'),
(14, 'Theo Teodoro', 'theo.inter', 'admin123', '1991-04-17');
SET IDENTITY_INSERT usuarios OFF;
PRINT 'Usuários inseridos.';

-- 2. Inserção de Clientes
SET IDENTITY_INSERT clientes ON;
IF NOT EXISTS (SELECT 1 FROM clientes WHERE id_cliente = 1)
INSERT INTO clientes (id_cliente, nome, data_nascimento, endereco, telefone, email, data_cadastro, usuario_id) VALUES
(1, 'Empresa Alpha Ltda', '2010-01-15', 'Rua das Flores, 123', '(11) 98765-4321', 'contato@alpha.com', '2025-01-10', 1),
(2, 'Comércio Beta S/A', '2005-06-20', 'Av. Principal, 456', '(21) 91234-5678', 'compras@beta.com', '2025-01-12', 2),
(3, 'Design Gama', '2018-03-10', 'Praça da Matriz, 789', '(31) 99999-8888', 'arte@gama.com', '2025-01-15', 3),
(4, 'Restaurante Delta', '2012-11-05', 'Rua do Sabor, 10', '(41) 98888-7777', 'adm@delta.com', '2025-01-20', 4),
(5, 'Escritório Épsilon', '2015-08-01', 'Av. Central, 321', '(51) 97777-6666', 'financeiro@epsilon.com', '2025-02-01', 5),
(6, 'Academia Zeta', '2020-02-28', 'Rua da Saúde, 654', '(61) 96666-5555', 'contato@zeta.com', '2025-02-05', 1),
(7, 'Consultoria Ômega', '2000-07-19', 'Alameda dos Anjos, 987', '(71) 95555-4444', 'diretoria@omega.com', '2025-02-10', 2),
(8, 'Padaria Teta', '2017-09-12', 'Rua do Pão, 741', '(81) 94444-3333', 'pedidos@teta.com', '2025-02-15', 3),
(9, 'Loja Sigma', '2019-12-01', 'Travessa do Comércio, 852', '(91) 93333-2222', 'vendas@sigma.com', '2025-02-20', 4),
(10, 'Gráfica Pi', '2014-04-30', 'Rua da Impressão, 963', '(19) 92222-1111', 'grafica@pi.com', '2025-03-01', 5);
SET IDENTITY_INSERT clientes OFF;
PRINT 'Clientes inseridos.';

-- 3. Inserção de Produtos
-- CORREÇÃO: Removidas as colunas pedido_id e orcamento_id
SET IDENTITY_INSERT produtos ON;
IF NOT EXISTS (SELECT 1 FROM produtos WHERE id_produto = 1)
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

-- 4. Inserção de Orçamentos
-- CORREÇÃO: Removida a coluna pedido_id_pedido
SET IDENTITY_INSERT orcamentos ON;
IF NOT EXISTS (SELECT 1 FROM orcamentos WHERE id_orcamento = 1)
INSERT INTO orcamentos (id_orcamento, descricao, data_cancel, data_orcamento, status, cliente_id, usuario_id) VALUES
(1, 'Orçamento para Banners e Adesivos', NULL, '2025-02-01', 0, 1, 1),
(2, 'Cartões de Visita Urgentes', NULL, '2025-02-03', 1, 2, 2),
(3, 'Material de Escritório (Blocos)', NULL, '2025-02-05', 0, 3, 3),
(4, 'Sinalização Interna Loja', NULL, '2025-02-10', 0, 9, 4),
(5, 'Renovação da Marca (Logo)', NULL, '2025-02-12', 1, 7, 5),
(6, 'Folders para Evento', NULL, '2025-02-15', 0, 5, 1),
(7, 'Crachás para funcionários', '2025-02-20', '2025-02-18', 0, 6, 2),
(8, 'Flyers promocionais Páscoa', NULL, '2025-02-22', 1, 8, 3),
(9, 'Placa para fachada', NULL, '2025-02-25', 0, 4, 4),
(10, 'Material Gráfico Completo', NULL, '2025-03-01', 0, 10, 5);
SET IDENTITY_INSERT orcamentos OFF;
PRINT 'Orçamentos inseridos.';

-- 5. Inserção de Pedidos
SET IDENTITY_INSERT pedidos ON;
IF NOT EXISTS (SELECT 1 FROM pedidos WHERE id_pedido = 1)
INSERT INTO pedidos (id_pedido, descricao, data_cancel, data_pedido, id_cliente, id_usuario, id_orcamento) VALUES
(1, 'Pedido Cartões de Visita (Aprovado)', NULL, '2025-02-04', 2, 2, 2),
(2, 'Pedido Criação de Logo (Aprovado)', NULL, '2025-02-13', 7, 5, 5),
(3, 'Pedido Flyers Páscoa (Aprovado)', NULL, '2025-02-23', 8, 3, 8),
(4, 'Pedido direto - Blocos de Nota', NULL, '2025-03-02', 1, 1, NULL),
(5, 'Pedido direto - Crachás', NULL, '2025-03-05', 6, 2, NULL),
(6, 'Ajuste Sinalização (Pedido Urgente)', NULL, '2025-03-10', 9, 4, NULL),
(7, 'Pedido Banners Evento', NULL, '2025-03-11', 1, 1, 1),
(8, 'Pedido Placa Fachada (Pendente)', NULL, '2025-03-12', 4, 4, 9),
(9, 'Consultoria de Design (Fechado)', NULL, '2025-03-15', 5, 5, NULL),
(10, 'Impressão Material Gráfico', '2025-03-20', '2025-03-18', 10, 3, 10);
SET IDENTITY_INSERT pedidos OFF;
PRINT 'Pedidos inseridos.';

-- 6. INSERÇÃO NAS TABELAS DE JUNÇÃO (Substitui o UPDATE em produtos)

-- Vinculando produtos a ORÇAMENTOS
IF NOT EXISTS (SELECT 1 FROM orcamento_produto WHERE orcamento_id = 3 AND produto_id = 3)
INSERT INTO orcamento_produto (orcamento_id, produto_id) VALUES
(3, 3), -- Bloco de Notas A5 (100 folhas) -> Orçamento 3
(4, 4), -- Adesivo Vinil Recorte (m²) -> Orçamento 4
(6, 5); -- Folder A4 Couchê 150g (1000 un) -> Orçamento 6
PRINT 'Produtos vinculados a Orçamentos.';

-- Vinculando produtos a PEDIDOS
IF NOT EXISTS (SELECT 1 FROM pedido_produto WHERE pedido_id = 1 AND produto_id = 2)
INSERT INTO pedido_produto (pedido_id, produto_id) VALUES
(1, 2), -- Milheiro Cartão de Visita -> Pedido 1
(2, 7), -- Criação de Logotipo (Serviço) -> Pedido 2
(3, 9), -- Flyer A5 Couchê 115g (2500 un) -> Pedido 3
(5, 6), -- Crachá PVC com Cordão -> Pedido 5
(7, 1), -- Impressão de Banner 1x1m -> Pedido 7
(8, 10),-- Placa de PVC 3mm (m²) -> Pedido 8
(9, 8); -- Consultoria de Design (Hora) -> Pedido 9
PRINT 'Produtos vinculados a Pedidos.';
GO

PRINT 'Script concluído com sucesso.';
GO