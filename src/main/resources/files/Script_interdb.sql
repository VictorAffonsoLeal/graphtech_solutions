create database interdb
go

use interdb
go

-- 1. Inserção de Usuários
insert into usuarios (nome, login, senha, data_nascimento) values
('Ana Silva', 'ana.silva', 'senha123', '1990-05-15'),
('Bruno Costa', 'bruno.costa', 'senha123', '1988-11-30'),
('Carla Dias', 'carla.dias', 'senha123', '1995-02-10'),
('Daniel Moreira', 'daniel.moreira', 'senha123', '1982-09-25'),
('Eduarda Lima', 'eduarda.lima', 'senha123', '2000-07-07'),
('Fábio Pereira', 'fabio.pereira', 'senha123', '1993-01-12'),
('Gabriela Matos', 'gabriela.matos', 'senha123', '1999-03-05'),
('Heitor Campos', 'heitor.campos', 'senha123', '1985-12-19'),
('Isabela Barros', 'isabela.barros', 'senha123', '1997-08-22'),
('João Souza', 'joao.souza', 'senha123', '1991-04-17'),
('Victor Leal', 'victor.inter', 'admin123', '1991-04-17'),
('Matheus Liebana Souza', 'matheus.inter', 'admin123', '1991-04-17'),
('Sophia Pellizon', 'sophia.inter', 'admin123', '1991-04-17'),
('Theo Teodoro', 'theo.inter', 'admin123', '1991-04-17');

-- 2. Inserção de Clientes
insert into clientes (nome, data_nascimento, endereco, telefone, email, data_cadastro, usuario_id) values
('Empresa Alpha Ltda', '2010-01-15', 'Rua das Flores, 123', '(11) 98765-4321', 'contato@alpha.com', '2025-01-10', 1),
('Comércio Beta S/A', '2005-06-20', 'Av. Principal, 456', '(21) 91234-5678', 'compras@beta.com', '2025-01-12', 2),
('Design Gama', '2018-03-10', 'Praça da Matriz, 789', '(31) 99999-8888', 'arte@gama.com', '2025-01-15', 3),
('Restaurante Delta', '2012-11-05', 'Rua do Sabor, 10', '(41) 98888-7777', 'adm@delta.com', '2025-01-20', 4),
('Escritório Épsilon', '2015-08-01', 'Av. Central, 321', '(51) 97777-6666', 'financeiro@epsilon.com', '2025-02-01', 5),
('Academia Zeta', '2020-02-28', 'Rua da Saúde, 654', '(61) 96666-5555', 'contato@zeta.com', '2025-02-05', 1),
('Consultoria Ômega', '2000-07-19', 'Alameda dos Anjos, 987', '(71) 95555-4444', 'diretoria@omega.com', '2025-02-10', 2),
('Padaria Teta', '2017-09-12', 'Rua do Pão, 741', '(81) 94444-3333', 'pedidos@teta.com', '2025-02-15', 3),
('Loja Sigma', '2019-12-01', 'Travessa do Comércio, 852', '(91) 93333-2222', 'vendas@sigma.com', '2025-02-20', 4),
('Gráfica Pi', '2014-04-30', 'Rua da Impressão, 963', '(19) 92222-1111', 'grafica@pi.com', '2025-03-01', 5);

-- 3. Inserção de Produtos
insert into produtos (descricao, valor, qtd, pedido_id, orcamento_id) values
('Impressão de Banner 1x1m Lona Fosca', 75.50, 100, NULL, NULL),
('Milheiro Cartão de Visita Couchê 300g', 120.00, 50, NULL, NULL),
('Bloco de Notas A5 (100 folhas)', 15.00, 200, NULL, NULL),
('Adesivo Vinil Recorte (m²)', 45.00, 500, NULL, NULL),
('Folder A4 Couchê 150g (1000 un)', 450.00, 30, NULL, NULL),
('Crachá PVC com Cordão', 12.50, 1000, NULL, NULL),
('Criação de Logotipo (Serviço)', 800.00, 10, NULL, NULL),
('Consultoria de Design (Hora)', 150.00, 50, NULL, NULL),
('Flyer A5 Couchê 115g (2500 un)', 300.00, 40, NULL, NULL),
('Placa de PVC 3mm (m²)', 90.00, 80, NULL, NULL);

-- 4. Inserção de Orçamentos
insert into orcamentos (descricao, data_cancel, data_orcamento, status, cliente_id, usuario_id, pedido_id_pedido) values
('Orçamento para Banners e Adesivos', NULL, '2025-02-01', 0, 1, 1, NULL),
('Cartões de Visita Urgentes', NULL, '2025-02-03', 1, 2, 2, NULL),
('Material de Escritório (Blocos)', NULL, '2025-02-05', 0, 3, 3, NULL),
('Sinalização Interna Loja', NULL, '2025-02-10', 0, 9, 4, NULL),
('Renovação da Marca (Logo)', NULL, '2025-02-12', 1, 7, 5, NULL),
('Folders para Evento', NULL, '2025-02-15', 0, 5, 1, NULL),
('Crachás para funcionários', '2025-02-20', '2025-02-18', 0, 6, 2, NULL),
('Flyers promocionais Páscoa', NULL, '2025-02-22', 1, 8, 3, NULL),
('Placa para fachada', NULL, '2025-02-25', 0, 4, 4, NULL),
('Material Gráfico Completo', NULL, '2025-03-01', 0, 10, 5, NULL);

-- 5. Inserção de Pedidos
insert into pedidos (descricao, data_cancel, data_pedido, id_cliente, id_usuario, id_orcamento) values
('Pedido Cartões de Visita (Aprovado)', NULL, '2025-02-04', 2, 2, 2),
('Pedido Criação de Logo (Aprovado)', NULL, '2025-02-13', 7, 5, 5),
('Pedido Flyers Páscoa (Aprovado)', NULL, '2025-02-23', 8, 3, 8),
('Pedido direto - Blocos de Nota', NULL, '2025-03-02', 1, 1, NULL),
('Pedido direto - Crachás', NULL, '2025-03-05', 6, 2, NULL),
('Ajuste Sinalização (Pedido Urgente)', NULL, '2025-03-10', 9, 4, NULL),
('Pedido Banners Evento', NULL, '2025-03-11', 1, 1, 1),
('Pedido Placa Fachada (Pendente)', NULL, '2025-03-12', 4, 4, 9),
('Consultoria de Design (Fechado)', NULL, '2025-03-15', 5, 5, NULL),
('Impressão Material Gráfico', '2025-03-20', '2025-03-18', 10, 3, 10);

-- 6. UPDATE em Produtos para vincular a Pedidos e Orçamentos
-- (Agora que Pedidos 1-10 e Orçamentos 1-10 existem)

-- Vinculando produtos a ORÇAMENTOS (que ainda não viraram pedido)
UPDATE produtos SET orcamento_id = 3 WHERE id_produto = 3; 
UPDATE produtos SET orcamento_id = 4 WHERE id_produto = 4; 
UPDATE produtos SET orcamento_id = 6 WHERE id_produto = 5; 
-- Vinculando produtos a PEDIDOS (que já foram aprovados)
UPDATE produtos SET pedido_id = 1 WHERE id_produto = 2; 
UPDATE produtos SET pedido_id = 2 WHERE id_produto = 7; 
UPDATE produtos SET pedido_id = 3 WHERE id_produto = 9; 
UPDATE produtos SET pedido_id = 5 WHERE id_produto = 6; 
UPDATE produtos SET pedido_id = 7 WHERE id_produto = 1; 
UPDATE produtos SET pedido_id = 8 WHERE id_produto = 10;
UPDATE produtos SET pedido_id = 9 WHERE id_produto = 8; 