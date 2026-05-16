package com.vinheria.agnello.infra;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SchemaBootstrap {

    private static final Logger LOG = Logger.getLogger(SchemaBootstrap.class.getName());

    private SchemaBootstrap() {}

    public static void run() {
        String dialect = ConnectionFactory.dialect();
        try (Connection con = ConnectionFactory.getConnection()) {
            if (tabelasJaExistem(con)) {
                LOG.info("Schema já existente. Skipping DDL.");
            } else {
                LOG.info("Schema vazio. Executando DDL para [" + dialect + "].");
                executarDDL(con, "sql/schema-" + dialect + ".sql");
            }
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Falha verificando/criando schema", e);
            throw new IllegalStateException("Falha verificando/criando schema", e);
        }
    }

    private static boolean tabelasJaExistem(Connection con) {
        try (Statement st = con.createStatement()) {
            st.executeQuery("SELECT 1 FROM tb_vinho FETCH FIRST 1 ROWS ONLY");
            return true;
        } catch (SQLException oracleStyle) {
            try (Statement st = con.createStatement()) {
                st.executeQuery("SELECT 1 FROM tb_vinho LIMIT 1");
                return true;
            } catch (SQLException mysqlStyle) {
                return false;
            }
        }
    }

    private static void executarDDL(Connection con, String classpathFile) {
        String script = lerScript(classpathFile);
        String[] comandos = script.split(";\\s*\\r?\\n");
        try (Statement st = con.createStatement()) {
            for (String raw : comandos) {
                String sql = raw.trim();
                if (sql.isEmpty() || sql.startsWith("--")) continue;
                try {
                    st.execute(sql);
                } catch (SQLException e) {
                    // Tabela já existe (ORA-00955 / MySQL 1050): ignora.
                    String msg = String.valueOf(e.getMessage()).toLowerCase();
                    if (msg.contains("already exists")
                            || msg.contains("ora-00955")
                            || e.getErrorCode() == 955
                            || e.getErrorCode() == 1050) {
                        LOG.fine("Objeto já existe, ignorando: " + sql.substring(0, Math.min(60, sql.length())));
                    } else {
                        throw e;
                    }
                }
            }
            LOG.info("DDL aplicada com sucesso.");
        } catch (SQLException e) {
            throw new IllegalStateException("Falha executando DDL " + classpathFile, e);
        }
    }

    private static String lerScript(String classpathFile) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream in = cl.getResourceAsStream(classpathFile)) {
            if (in == null) throw new IllegalStateException("Script não encontrado: " + classpathFile);
            StringBuilder sb = new StringBuilder();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = r.readLine()) != null) sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Erro lendo " + classpathFile, e);
        }
    }
}
