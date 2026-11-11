package web.com.config;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/** Reads simple KEY=value settings from .env, with process environment taking precedence. */
public final class Environment {
    private static final Logger LOGGER = Logger.getLogger(Environment.class.getName());
    private static final Properties FILE_VALUES = loadFile();

    private Environment() {
    }

    private static Properties loadFile() {
        Properties values = new Properties();
        String configuredPath = configuredFile();
        Path path = Path.of(configuredPath == null || configuredPath.isBlank() ? ".env" : configuredPath)
                .toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            if (configuredPath != null && !configuredPath.isBlank()) {
                throw new IllegalStateException("Không tìm thấy tệp cấu hình: " + path);
            }
            LOGGER.info("No .env file at " + path
                    + "; using process environment and defaults. Configure APP_ENV_FILE to load a specific file.");
            return values;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            values.load(reader);
            LOGGER.info("Loaded configuration from " + path);
        } catch (IOException e) {
            throw new IllegalStateException("Không thể đọc tệp cấu hình: " + path, e);
        }
        return values;
    }

    private static String configuredFile() {
        String path = System.getenv("APP_ENV_FILE");
        if (path == null || path.isBlank()) {
            path = System.getProperty("APP_ENV_FILE");
        }
        if (path == null || path.isBlank()) {
            // Per-application Tomcat configuration also works when an IDE starts Java directly.
            try {
                InitialContext context = new InitialContext();
                try {
                    Object value = context.lookup("java:comp/env/APP_ENV_FILE");
                    path = value instanceof String ? (String) value : null;
                } finally {
                    context.close();
                }
            } catch (NamingException e) {
                // No JNDI entry (or running outside a servlet container): use the working directory.
            }
        }
        return path;
    }

    public static String get(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : FILE_VALUES.getProperty(key, defaultValue);
    }
}
