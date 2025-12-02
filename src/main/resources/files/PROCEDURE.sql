USE interdb
GO

CREATE PROCEDURE SP_CADUSUARIO
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

CREATE PROCEDURE SP_CADCLIENTE
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

            INSERT INTO pessoas (nome, data_nascimento, status) VALUES (@NOMECLI, @DATNAS, 0);
            
            DECLARE @NOVO_ID INT = SCOPE_IDENTITY();

            INSERT INTO clientes (id_pessoa, email, endereco, telefone, data_cadastro, usuario_id)
            VALUES (@NOVO_ID, @EMAILCLI, @ENDCLI, @TELCLI, GETDATE(), @USUARIOID);
        COMMIT
        
        SELECT @NOVO_ID as id_gerado;
    END TRY
    BEGIN CATCH
        ROLLBACK;
        THROW; 
    END CATCH
END
GO