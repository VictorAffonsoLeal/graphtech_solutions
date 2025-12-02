use interdb
go

CREATE PROCEDURE SP_CADCLIENTE
(
    @NOMECLI    VARCHAR(50),
    @EMAILCLI   VARCHAR(50),
    @ENDCLI     VARCHAR(50),
    @TELCLI     VARCHAR(20),
    @DATNAS     DATE,
    @USUARIOID  INT   
)
AS
BEGIN
    BEGIN TRY
        BEGIN TRAN

        -- Inserir dados na tabela Pessoas
        INSERT INTO Pessoas (NOME, data_nascimento, status)
        VALUES (@NOMECLI, @DATNAS, 0)

        -- Capturar o ID gerado para a pessoa
        DECLARE @ID_PESSOA INT = SCOPE_IDENTITY()

        -- Inserir dados na tabela Clientes, incluindo usuario_id e data_cadastro
        INSERT INTO Clientes (id_pessoa, email, endereco, telefone, data_cadastro, usuario_id)
        VALUES (@ID_PESSOA, @EMAILCLI, @ENDCLI, @TELCLI, GETDATE(), @USUARIOID)

        COMMIT
        PRINT 'DADOS CADASTRADOS COM SUCESSO'
    END TRY
    BEGIN CATCH
        ROLLBACK
        PRINT 'DADOS NÃO CADASTRADOS'
        PRINT ERROR_MESSAGE()
    END CATCH
END
GO

EXEC SP_CADCLIENTE 'CLIENTE TESTE', 'clienteteste@gmail.com', 'teste, 123', '(11)98888-7777', '1990-05-20', 4


CREATE PROCEDURE SP_CADUSUARIO
(	-- PARAMETROS RECEBIDOS PELA PROCEDURE
	@NOMECLI VARCHAR(50), 
    @DATNAS  DATE,
    @LOGIN   VARCHAR(50),
    @SENHA   VARCHAR(20)    
)    
    
AS 
BEGIN
	-- INICIAR UM TRATAMENTO DE ERRO
	BEGIN TRY
		-- INICIAR UMA TRANSAÇÃO
		BEGIN TRAN
			-- INSERIR OS DADOS DA PESSOA NA TABELA PESSOAS
			INSERT INTO Pessoas (NOME, data_nascimento, status)       -- COLUNAS DA TABELA
			-- VALUES ('BRUNO GABRIEL', '123.666.555-14', 1) -- VALORES INSERIDOS
			VALUES (@NOMECLI, @DATNAS, 0) -- VALORES RECEBIDOS POR PARAMETRO
			
			
			INSERT INTO usuarios(id_pessoa, login, senha)
			VALUES (SCOPE_IDENTITY(), @LOGIN, @SENHA)
			-- SE DEU TUDO CERTO, CONFIRMAR OS DADOS NAS DUAS TABELAS
			COMMIT
			PRINT 'DADOS CADASTRADOS COM SUCESSO'
	END TRY
	BEGIN CATCH
		-- SE DEU ERRO, DESFAZER TUDO (OS DOIS INSERTS)
		ROLLBACK
		PRINT 'DADOS NÃO CADASTRADOS'
	END CATCH
END
GO

EXEC SP_CADUSUARIO 'USUARIO TESTE', '1990-05-20', 'TESTEMASTER', 'senha123'



select * from LOG_HISTORICOPRECO
go

alter PROCEDURE SP_CADUSUARIO
(
	@NOMECLI VARCHAR(255), 
    @DATNAS  DATE,
    @LOGIN   VARCHAR(100),
    @SENHA   VARCHAR(100)    
)    
AS 
BEGIN
    SET NOCOUNT ON;
	BEGIN TRY
		BEGIN TRAN
			INSERT INTO pessoas (nome, data_nascimento, status) VALUES (@NOMECLI, @DATNAS, 0);
			DECLARE @NOVO_ID INT = SCOPE_IDENTITY();

			INSERT INTO usuarios(id_pessoa, login, senha) VALUES (@NOVO_ID, @LOGIN, @SENHA);
		COMMIT
        SELECT @NOVO_ID as id_gerado;
	END TRY
	BEGIN CATCH
		ROLLBACK;
        THROW;
	END CATCH
END
GO

alter PROCEDURE SP_CADCLIENTE
(
    @NOMECLI    VARCHAR(255),
    @EMAILCLI   VARCHAR(100),
    @ENDCLI     VARCHAR(255),
    @TELCLI     VARCHAR(50),
    @DATNAS     DATE,
    @USUARIOID  INT   
)
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        BEGIN TRAN
            -- 1. Inserir na tabela Pessoas
            INSERT INTO pessoas (nome, data_nascimento, status) VALUES (@NOMECLI, @DATNAS, 0);
            
            -- 2. Pegar o ID gerado
            DECLARE @NOVO_ID INT = SCOPE_IDENTITY();

            -- 3. Inserir na tabela Clientes
            INSERT INTO clientes (id_pessoa, email, endereco, telefone, data_cadastro, usuario_id)
            VALUES (@NOVO_ID, @EMAILCLI, @ENDCLI, @TELCLI, GETDATE(), @USUARIOID);
        COMMIT
        
        -- 4. Retornar o ID para o Java
        SELECT @NOVO_ID as id_gerado;
    END TRY
    BEGIN CATCH
        ROLLBACK;
        THROW; -- Lança o erro original para o Java capturar
    END CATCH
END
GO