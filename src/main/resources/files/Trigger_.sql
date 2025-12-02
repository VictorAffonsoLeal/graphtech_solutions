use interdb
go

-- TABELA
CREATE TABLE LOG_HISTORICOPRECO
(
	PRODUTO_ID	INT			NOT NULL,
	DATA		DATETIME	NOT NULL,
	PRECOANTIGO MONEY		NOT NULL,
	PRECONOVO	MONEY		NOT NULL,
	USUARIO		VARCHAR(50)	NOT NULL,
	FOREIGN KEY(PRODUTO_ID)		REFERENCES		PRODUTOS(ID_PRODUTO),
	PRIMARY KEY(PRODUTO_ID, DATA)
)
GO

-- Trigger 1: Registrar alterações
CREATE TRIGGER TG_LOGALTERACAOPRECO
ON		PRODUTOS	
AFTER	UPDATE		
AS
BEGIN
	INSERT INTO LOG_HISTORICOPRECO (PRODUTO_ID, DATA, PRECOANTIGO, PRECONOVO, USUARIO)
		SELECT I.id_Produto, GETDATE(), D.valor, I.valor, SYSTEM_USER
		FROM INSERTED AS I, DELETED AS D
		WHERE I.ID_PRODUTO = D.ID_PRODUTO AND I.VALOR != d.VALOR
END
GO


-- Trigger 2: Reduzir estoque quando pedido_produto for inserido
CREATE TRIGGER trg_reduzir_estoque_pedido
ON pedido_produto
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE p
    SET p.qtd = p.qtd - i.qtd
    FROM produtos p
    INNER JOIN inserted i ON p.id_produto = i.produto_id;
END;
GO



-- Trigger 3: Validar estoque antes de inserir pedido_produto
CREATE TRIGGER trg_validar_estoque_antes_pedido
ON pedido_produto
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @estoque_disponivel INT;
    DECLARE @qtd_solicitada INT;
    DECLARE @nome_produto VARCHAR(255);
    DECLARE @produto_id INT;
    DECLARE @mensagem_erro VARCHAR(500);
    
    DECLARE cur_validacao CURSOR FOR
    SELECT i.produto_id, i.qtd, p.qtd, p.descricao
    FROM inserted i
    INNER JOIN produtos p ON i.produto_id = p.id_produto;
    
    OPEN cur_validacao;
    
    FETCH NEXT FROM cur_validacao 
    INTO @produto_id, @qtd_solicitada, @estoque_disponivel, @nome_produto;
    
    WHILE @@FETCH_STATUS = 0
    BEGIN
        IF @estoque_disponivel < @qtd_solicitada
        BEGIN
            SET @mensagem_erro = 'Estoque insuficiente para o produto "' + 
                                @nome_produto + 
                                '". Disponível: ' + 
                                CAST(@estoque_disponivel AS VARCHAR(10)) + 
                                ' | Solicitado: ' + 
                                CAST(@qtd_solicitada AS VARCHAR(10));
            
            CLOSE cur_validacao;
            DEALLOCATE cur_validacao;
            
            THROW 50001, @mensagem_erro, 1;
            RETURN;
        END;
        
        FETCH NEXT FROM cur_validacao 
        INTO @produto_id, @qtd_solicitada, @estoque_disponivel, @nome_produto;
    END;
    
    CLOSE cur_validacao;
    DEALLOCATE cur_validacao;
    
    INSERT INTO pedido_produto (pedido_id, produto_id, qtd, valor_unitario)
    SELECT pedido_id, produto_id, qtd, valor_unitario
    FROM inserted;
END;
GO

CREATE TRIGGER TG_EXCLUIPESSOA
ON			PESSOAS
INSTEAD OF	DELETE
AS
BEGIN
	-- UPDATE NA TABELA PESSOAS
	UPDATE PESSOAS SET STATUS = 1
	WHERE id_Pessoa IN	(
							SELECT id_pessoa
							FROM deleted
						)
END
GO