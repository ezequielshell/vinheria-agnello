package com.vinheria.agnello.infra;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConnectionFactory {

    private static final Logger LOG = Logger.getLogger(ConnectionFactory.class.getName());

    private static volatile HikariDataSource dataSource;
    private static volatile String dialect = "oracle";

    private ConnectionFactory() {}

    public static Connection getConnection() throws SQLException {
        return dataSource().getConnection();
    }

    public static DataSource dataSource() {
        if (dataSource == null) {
            synchronized (ConnectionFactory.class) {
                if (dataSource == null) {
                    dataSource = build();
                }
            }
        }
        return dataSource;
    }

    public static String dialect() {
        dataSource();
        return dialect;
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            dataSource = null;
        }
    }

    private static HikariDataSource build() {
        Properties props = loadProps();
        dialect = props.getProperty("db.dialect", "oracle").trim().toLowerCase();

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(required(props, "db.url"));
        cfg.setUsername(required(props, "db.user"));
        cfg.setPassword(props.getProperty("db.password", ""));
        cfg.setDriverClassName(driverFor(dialect));
        cfg.setMaximumPoolSize(intProp(props, "db.pool.maxSize", 10));
        cfg.setMinimumIdle(intProp(props, "db.pool.minIdle", 2));
        cfg.setConnectionTimeout(intProp(props, "db.pool.connectionTimeoutMs", 10000));
        cfg.setPoolName("VinheriaPool");

        LOG.info("Inicializando pool [" + dialect + "] -> " + cfg.getJdbcUrl());
        return new HikariDataSource(cfg);
    }

    private static Properties loadProps() {
        Properties p = new Properties();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try (InputStream in = cl.getResourceAsStream("database.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                    "database.properties não encontrado no classpath (src/main/resources)");
            }
            p.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Falha lendo database.properties", e);
        }
        return p;
    }

    private static String required(Properties p, String key) {
        String v = p.getProperty(key);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException("Propriedade obrigatória ausente: " + key);
        }
        return v.trim();
    }

    private static int intProp(Properties p, String key, int def) {
        try {
            return Integer.parseInt(p.getProperty(key, String.valueOf(def)).trim());
        } catch (NumberFormatException e) {
            LOG.log(Level.WARNING, "Valor inválido para " + key + ", usando padrão " + def);
            return def;
        }
    }

    private static String driverFor(String dialect) {
        return switch (dialect) {
            case "oracle" -> "oracle.jdbc.OracleDriver";
            case "mysql"  -> "com.mysql.cj.jdbc.Driver";
            default -> throw new IllegalStateException("Dialeto não suportado: " + dialect);
        };
    }
}
