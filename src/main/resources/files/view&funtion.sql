-----------------------------------------------------------------------------------
-- VIEW
-----------------------------------------------------------------------------------
CREATE VIEW vw_taxa_conversao_vendedores AS
WITH 
-- Total de orçamentos por vendedor
orc AS (
    SELECT 
        usuario_id,
        COUNT(*) AS total_orcamentos,
        SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS orcamentos_convertidos
    FROM orcamentos
    GROUP BY usuario_id
),

-- Total de pedidos por vendedor
ped AS (
    SELECT 
        id_usuario,
        COUNT(*) AS total_pedidos
    FROM pedidos
    GROUP BY id_usuario
)

SELECT
    u.id_pessoa,
    pe.nome,

    COALESCE(o.total_orcamentos, 0)        AS total_orcamentos,
    COALESCE(o.orcamentos_convertidos, 0)  AS orcamentos_convertidos,
    

    -- Taxa de conversão formatada
    CASE 
        WHEN COALESCE(o.total_orcamentos, 0) = 0 THEN 0.00
        ELSE CAST(COALESCE(o.orcamentos_convertidos, 0) * 100.0 /
                  COALESCE(o.total_orcamentos, 0) AS DECIMAL(6,2))
    END AS taxa_conversao,

    CASE
        -- 1) Tem orçamentos e pelo menos 1 convertido
        WHEN COALESCE(o.total_orcamentos, 0) > 0 
             AND COALESCE(o.orcamentos_convertidos, 0) > 0
            THEN 'Conversão normal'

        -- 2) Tem orçamentos, nenhum convertido, MAS tem pedidos
        WHEN COALESCE(o.total_orcamentos, 0) > 0 
             AND COALESCE(o.orcamentos_convertidos, 0) = 0
             AND COALESCE(p.total_pedidos, 0) > 0
            THEN 'Sem conversão'

        -- 3) Tem orçamentos, nenhum convertido e não tem pedidos
        WHEN COALESCE(o.total_orcamentos, 0) > 0
             AND COALESCE(o.orcamentos_convertidos, 0) = 0
             AND COALESCE(p.total_pedidos, 0) = 0
            THEN 'Orçamentos sem conversão e sem pedidos'

        -- 4) Tem pedidos, mas NÃO tem orçamentos
        WHEN COALESCE(o.total_orcamentos, 0) = 0
             AND COALESCE(p.total_pedidos, 0) > 0
            THEN 'Venda direta'

        -- 5) Não tem orçamentos e nem pedidos
        WHEN COALESCE(o.total_orcamentos, 0) = 0 
             AND COALESCE(p.total_pedidos, 0) = 0
            THEN 'Sem pedidos e orçamentos'

        ELSE 'Indefinido'
    END AS classificacao,

    COALESCE(p.total_pedidos, 0) AS vendas

FROM usuarios u
INNER JOIN pessoas pe ON pe.id_pessoa = u.id_pessoa
LEFT JOIN orc o ON o.usuario_id = u.id_pessoa
LEFT JOIN ped p ON p.id_usuario = u.id_pessoa;


select * from dbo.vw_taxa_conversao_vendedores

-----------------------------------------------------------------------------------
-- FUNCTION
-----------------------------------------------------------------------------------
use interdb
go 

create function fc_qtdPedidoMesUsuario
(
	@id int, @mes		int,	@ano	int
)
RETURNS TABLE
as 
		return (
				SELECT C.id_pessoa, C.nome, SUM(A.valor_unitario * A.QTD) AS VALOR_TOTAL
				FROM  pedido_produto A 
				INNER JOIN pedidos B ON A.pedido_id = B.id_pedido
				INNER JOIN pessoas C ON B.id_usuario = C.id_pessoa
				WHERE C.id_pessoa = @ID AND MONTH(B.data_pedido) = @mes AND YEAR(B.data_pedido) = @ano
				GROUP BY C.id_pessoa, C.nome
				)
go

select * from dbo.fc_qtdPedidoMesUsuario(4, 3, 2025)
go

select * from pedidos
go