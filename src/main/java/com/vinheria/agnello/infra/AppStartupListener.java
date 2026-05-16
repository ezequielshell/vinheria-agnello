package com.vinheria.agnello.infra;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class AppStartupListener implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(AppStartupListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ConnectionFactory.dataSource();

            Properties props = loadProps();
            boolean autoBootstrap = Boolean.parseBoolean(
                props.getProperty("db.autoBootstrap", "true").trim());

            if (autoBootstrap) {
                SchemaBootstrap.run();
                DataSeeder.run();
            }
            LOG.info("Vinheria Agnello inicializada com sucesso.");
        } catch (Exception e) {
            LOG.log(Level.SEVERE,
                "Falha inicializando aplicação. Verifique src/main/resources/database.properties.", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ConnectionFactory.shutdown();
        LOG.info("Pool de conexões finalizado.");
    }

    private Properties loadProps() {
        Properties p = new Properties();
        try (InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("database.properties")) {
            if (in != null) p.load(in);
        } catch (IOException ignored) {}
        return p;
    }
}
