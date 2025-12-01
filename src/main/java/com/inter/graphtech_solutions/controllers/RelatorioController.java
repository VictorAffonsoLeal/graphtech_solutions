package com.inter.graphtech_solutions.controllers;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inter.graphtech_solutions.projection.RelatorioPedidoUsuarioProjection;
import com.inter.graphtech_solutions.projection.TaxaConversaoProjection;
import com.inter.graphtech_solutions.repositories.PedidoRepository;
import com.inter.graphtech_solutions.repositories.UsuarioRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
// Não precisamos mais do EntityManager para backup/restore via JDBC direto
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // Injetando credenciais do application.properties para usar na conexão JDBC manual
    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPass;

    // --- RELATÓRIOS (Function e View) ---

    @GetMapping("/vendas-usuario")
    public ResponseEntity<List<RelatorioPedidoUsuarioProjection>> getVendasUsuario(
            @RequestParam Integer idUsuario,
            @RequestParam Integer mes,
            @RequestParam Integer ano) {
        List<RelatorioPedidoUsuarioProjection> resultado = 
            pedidoRepository.buscarTotalPedidosUsuario(idUsuario, mes, ano);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/taxa-conversao")
    public ResponseEntity<List<TaxaConversaoProjection>> getTaxaConversao() {
        List<TaxaConversaoProjection> resultado = usuarioRepository.buscarTaxaConversao();
        return ResponseEntity.ok(resultado);
    }

    private String getCaminhoBackup() {
        // Pega o diretório onde o projeto está rodando (Raiz do projeto)
        String projetoDir = System.getProperty("user.dir");
        
        // Monta o caminho relativo até a pasta de resources
        // Nota: File.separator garante que funcione em Windows (\) ou Linux (/)
        String caminhoRelativo = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "backup";
        
        // Cria o objeto File para garantir que o diretório exista
        File pastaBackup = new File(projetoDir, caminhoRelativo);
        if (!pastaBackup.exists()) {
            pastaBackup.mkdirs(); // Cria a pasta se não existir
        }

        // Retorna o caminho completo do arquivo .bak
        return new File(pastaBackup, "interdb.bak").getAbsolutePath();
    }

    @PostMapping("/backup")
    public ResponseEntity<String> realizarBackup() {
        String caminhoBackup = getCaminhoBackup();
        
        // Conecta no master para evitar bloqueios, embora backup possa ser feito no próprio banco
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=master;encrypt=true;trustServerCertificate=true";

        try (Connection con = DriverManager.getConnection(connectionUrl, dbUser, dbPass);
             Statement stmt = con.createStatement()) {

            // SQL Server precisa do caminho absoluto
            String sql = "BACKUP DATABASE interdb TO DISK = '" + caminhoBackup + "' WITH INIT";
            
            stmt.execute(sql);
            
            return ResponseEntity.ok("Backup realizado com sucesso em: " + caminhoBackup);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao fazer backup (Verifique permissões de escrita na pasta): " + e.getMessage());
        }
    }

    @PostMapping("/restore")
    public ResponseEntity<String> realizarRestore() {
        String caminhoBackup = getCaminhoBackup();
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=master;encrypt=true;trustServerCertificate=true";

        try (Connection con = DriverManager.getConnection(connectionUrl, dbUser, dbPass);
             Statement stmt = con.createStatement()) {

            String sql = 
                "ALTER DATABASE interdb SET SINGLE_USER WITH ROLLBACK IMMEDIATE; " +
                "RESTORE DATABASE interdb FROM DISK = '" + caminhoBackup + "' WITH REPLACE; " +
                "ALTER DATABASE interdb SET MULTI_USER;";

            stmt.execute(sql);
            
            return ResponseEntity.ok("Banco de dados restaurado com sucesso a partir de: " + caminhoBackup);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro crítico ao restaurar: " + e.getMessage());
        }
    }

        // --- PROCEDURES ---

    @PostMapping("/procedure/cliente")
    @Transactional
    public ResponseEntity<String> executarSpCadCliente(@RequestBody Map<String, Object> dados) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("SP_CADCLIENTE");
            
            // Registra parâmetros
            query.registerStoredProcedureParameter("NOMECLI", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("EMAILCLI", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("ENDCLI", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("TELCLI", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("DATNAS", java.sql.Date.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("USUARIOID", Integer.class, ParameterMode.IN);

            // Define valores
            query.setParameter("NOMECLI", dados.get("nome"));
            query.setParameter("EMAILCLI", dados.get("email"));
            query.setParameter("ENDCLI", dados.get("endereco"));
            query.setParameter("TELCLI", dados.get("telefone"));
            // Converte string de data para java.sql.Date
            query.setParameter("DATNAS", java.sql.Date.valueOf((String) dados.get("dataNascimento")));
            query.setParameter("USUARIOID", Integer.parseInt(dados.get("usuarioId").toString()));

            query.execute();
            return ResponseEntity.ok("Cliente cadastrado via Procedure com sucesso!");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro na Procedure Cliente: " + e.getMessage());
        }
    }

    @PostMapping("/procedure/usuario")
    @Transactional
    public ResponseEntity<String> executarSpCadUsuario(@RequestBody Map<String, Object> dados) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("SP_CADUSUARIO");

            query.registerStoredProcedureParameter("NOMECLI", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("DATNAS", java.sql.Date.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("LOGIN", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("SENHA", String.class, ParameterMode.IN);

            query.setParameter("NOMECLI", dados.get("nome"));
            query.setParameter("DATNAS", java.sql.Date.valueOf((String) dados.get("dataNascimento")));
            query.setParameter("LOGIN", dados.get("login"));
            query.setParameter("SENHA", dados.get("senha"));

            query.execute();
            return ResponseEntity.ok("Usuário cadastrado via Procedure com sucesso!");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro na Procedure Usuário: " + e.getMessage());
        }
    }
}